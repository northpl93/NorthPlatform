package pl.north93.zgame.skyblock.server.cmd;

import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.skyblock.shared.api.IslandRole;
import pl.north93.zgame.skyblock.shared.api.NorthBiome;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.gui.BiomeChangeGui;

public class BiomeCmd extends NorthCommand
{
    private BukkitApiCore   apiCore;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private     INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private     SkyBlockServer  server;
    @InjectResource(bundleName = "SkyBlock")
    private     ResourceBundle  messages;

    public BiomeCmd()
    {
        super("biome", "biom");
        this.setPermission("skyblock.biome");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final SkyPlayer skyPlayer = SkyPlayer.get(this.networkManager.getOnlinePlayer(sender.getName()));
        if (! skyPlayer.hasIsland())
        {
            sender.sendMessage(this.messages, "error.you_must_have_island");
            return;
        }

        if (skyPlayer.getIslandRole().equals(IslandRole.MEMBER))
        {
            sender.sendMessage(this.messages, "error.you_must_be_owner");
            return;
        }

        final Consumer<NorthBiome> callback = biome -> this.server.getSkyBlockManager().changeBiome(skyPlayer.getIslandId(), biome);
        this.apiCore.getWindowManager().openWindow((Player) sender.unwrapped(), new BiomeChangeGui(callback));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
