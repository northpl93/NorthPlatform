package pl.north93.zgame.skyblock.server.cmd.admin;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.Main;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.skyblock.server.SkyBlockServer;

public class SkyBypassCmd extends NorthCommand
{
    private BukkitApiCore   apiCore;
    @Inject
    private INetworkManager networkManager;
    @Inject
    private SkyBlockServer  server;

    public SkyBypassCmd()
    {
        super("skybypass");
        this.setPermission("skyblock.admin");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Main pluginMain = this.apiCore.getPluginMain();
        final Player player = (Player) sender.unwrapped();
        if (player.hasMetadata("skyblockbypass"))
        {
            player.removeMetadata("skyblockbypass", pluginMain);
            sender.sendMessage("&cWylaczono bypass");
        }
        else
        {
            player.setMetadata("skyblockbypass", new FixedMetadataValue(pluginMain, null));
            sender.sendMessage("&cWlaczono bypass");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
