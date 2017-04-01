package pl.north93.zgame.skyblock.server.cmd.admin;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.server.SkyBlockServer;

public class SkyDelete extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;

    public SkyDelete()
    {
        super("skydelete");
        this.setPermission("skyblock.admin");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 1)
        {
            sender.sendMessage("&cTa komenda USUNIE wyspe podanego gracza. Wpisz /skydelete nick.");
            return;
        }

        String target = args.asString(0);
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(target))
        {
            final SkyPlayer skyPlayer = SkyPlayer.get(t.getPlayer());
            if (!skyPlayer.hasIsland()) {
                sender.sendMessage("&Gracz " + target + " nie ma wyspy!");
                return;
            }


            this.server.getSkyBlockManager().deleteIsland(skyPlayer.getIslandId());
            sender.sendMessage("&aUsunieto wyspe gracza " + target);
        }
        catch (final PlayerNotFoundException e)
        {
            sender.sendMessage("&cGracz " + target + " nie istnieje!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
