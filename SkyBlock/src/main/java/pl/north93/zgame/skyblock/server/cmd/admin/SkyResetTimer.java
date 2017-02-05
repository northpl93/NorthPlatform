package pl.north93.zgame.skyblock.server.cmd.admin;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.skyblock.api.player.SkyPlayer;

public class SkyResetTimer extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;

    public SkyResetTimer()
    {
        super("skyresettimer");
        this.setPermission("skyblock.admin");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final String player = args.length() == 1 ? args.asString(0) : sender.getName();

        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(player))
        {
            final SkyPlayer skyPlayer = SkyPlayer.get(t.getPlayer());
            skyPlayer.setIslandCooldown(0);
            sender.sendMessage("&cResetowanie timera dla " + player);
        }
        catch (final Exception e)
        {
            sender.sendMessage("&cGracz " + player + " musi byc online!");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
