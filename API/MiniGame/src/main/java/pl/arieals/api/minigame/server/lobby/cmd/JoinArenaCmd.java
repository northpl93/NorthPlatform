package pl.arieals.api.minigame.server.lobby.cmd;

import java.util.UUID;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.api.minigame.server.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.impl.ArenaManager;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class JoinArenaCmd extends NorthCommand
{
    @Inject
    private MiniGameServer server;
    @Inject
    private ArenaManager   arenaManager;

    public JoinArenaCmd()
    {
        super("joinarena");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final LobbyManager serverManager = this.server.getServerManager(); // will throw exception on GameHost.

        final RemoteArena arena = this.arenaManager.getArena(UUID.fromString(args.asString(0)));
        serverManager.getArenaClient().connect(arena, new PlayerJoinInfo(player.getUniqueId(), false, false));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
