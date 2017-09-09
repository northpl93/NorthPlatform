package pl.north93.zgame.api.bukkitcommands;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.server.IBukkitServerManager;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.server.Server;

public class WtfServer extends NorthCommand
{
    @Inject
    private IBukkitServerManager serverManager;

    public WtfServer()
    {
        super("wtfserver");
        this.setPermission("api.command.wtfserver");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Server server = this.serverManager.getServer();
        sender.sendRawMessage("ID serwera: " + server.getUuid());
        sender.sendRawMessage("Nazwa w proxy: " + server.getProxyName());
        sender.sendRawMessage("Typ serwera: " + server.getType());
        sender.sendRawMessage("Czy uruchomiony przez demona: " + (server.isLaunchedViaDaemon() ? "tak" : "nie"));
        sender.sendRawMessage("Stan serwera: " + server.getServerState());
        sender.sendRawMessage("Grupa serwerów: " + server.getServersGroup().getName());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
