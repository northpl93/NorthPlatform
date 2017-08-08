package pl.north93.zgame.skyblock.server.listeners.islandhost;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import net.minecraft.server.v1_10_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_10_R1.NBTTagCompound;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;

public class ItemRecoveryListener implements Listener
{
    private static final MetaKey RECOVERY_KEY = MetaKey.get("expRecovered");
    @Inject
    private BukkitApiCore   apiCore;
    @Inject
    private INetworkManager networkManager;
    @Inject
    private SkyBlockServer  server;

    @EventHandler
    public void onJoin(final PlayerJoinEvent event)
    {
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() -> this.fixAsync(event.getPlayer()));
    }

    private void fixAsync(final Player player)
    {
        final Value<IOnlinePlayer> value = this.networkManager.getPlayers().unsafe().getOnline(player.getName());
        final IOnlinePlayer iOnlinePlayer = value.get();
        if (iOnlinePlayer.getMetaStore().contains(RECOVERY_KEY))
        {
            return;
        }

        final SkyPlayer skyPlayer = SkyPlayer.get(value);
        if (! skyPlayer.hasIsland())
        {
            return;
        }

        final UUID islandId = this.server.getIslandDao().getIsland(skyPlayer.getIslandId()).getServerId();
        if (! iOnlinePlayer.getServerId().equals(islandId))
        {
            return;
        }

        final File worldFolder = Bukkit.getWorlds().get(0).getWorldFolder();
        final File playerDat = new File(worldFolder, "playerdata/" + player.getUniqueId() + ".dat");
        if (! playerDat.exists())
        {
            return;
        }

        System.out.println("Recovering " + player.getName() + " file:" + playerDat.getAbsolutePath());

        final NBTTagCompound compound;
        try
        {
            compound = NBTCompressedStreamTools.a(new FileInputStream(playerDat));
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            return;
        }

        System.out.println("Loaded file! ");

        final int xpLevel = compound.getInt("XpLevel");
        final float xpP = compound.getFloat("XpP");

        System.out.println("XpLevel=" + xpLevel + " uuid:" + player.getUniqueId());
        System.out.println("xpP=" + xpP + " uuid: " + player.getUniqueId());

        value.update(player1 -> {
            player1.getMetaStore().set(RECOVERY_KEY, "XpLevel=" + xpLevel + ",XpP=" + xpP + ",at=" + System.currentTimeMillis());
        });

        try
        {
            synchronized (this)
            {
                this.wait(1500);
            }
        }
        catch (final InterruptedException e)
        {
            e.printStackTrace();
        }

        System.out.println("Recovery of " + player.getUniqueId() + " begin...");

        this.apiCore.sync(() ->
        {
            int newLevel = player.getLevel() + xpLevel;
            if (xpLevel > 0)
            {
                newLevel += 1;
            }
            player.setLevel(newLevel);
            System.out.println("new level = " + newLevel);

            final float newExp = player.getExp() + xpP;
            player.setExp(newExp);
            System.out.println("new exp = " + newExp);

            System.out.println("recovery of " + player.getUniqueId() + " completed!");
        });
    }
}
