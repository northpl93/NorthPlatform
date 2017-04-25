package pl.arieals.api.minigame.server.lobby.cmd;

import java.util.UUID;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;

public class JoinArenaCmd extends NorthCommand
{
    @InjectComponent("MiniGameApi.Server")
    private MiniGameServer server;

    public JoinArenaCmd()
    {
        super("joinarena");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final LobbyManager serverManager = this.server.getServerManager(); // will throw exception on GameHost.
        serverManager.tryConnectPlayer(((Player) sender.unwrapped()), UUID.fromString(args.asString(0)));
    }
}
