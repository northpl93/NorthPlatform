package pl.north93.zgame.skyblock.server.cmd;

import java.util.ResourceBundle;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.skyblock.server.SkyBlockServer;

public class SpawnCmd extends NorthCommand
{
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;
    @InjectResource(bundleName = "SkyBlock")
    private ResourceBundle  messages;

    public SpawnCmd()
    {
        super("spawn");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        sender.sendMessage(this.messages, "info.tp_to_spawn");
        final Player player = (Player) sender.unwrapped();
        this.server.getServerManager().tpPlayerToSpawn(player);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
