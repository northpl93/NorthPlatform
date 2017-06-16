package pl.arieals.api.minigame.server.shared.cmd;

import java.util.Set;
import java.util.UUID;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ListArenasCmd extends NorthCommand
{
    @Inject
    private MiniGameServer miniGameServer;

    public ListArenasCmd()
    {
        super("listarenas");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Set<RemoteArena> allArenas = this.miniGameServer.getArenaManager().getAllArenas();
        for (final RemoteArena arena : allArenas)
        {
            sender.sendRawMessage("&e- " + arena.getId());
            sender.sendRawMessage("&e |- serverId:" + arena.getServerId());
            sender.sendRawMessage("&e |- gamePhase:" + arena.getGamePhase());
            sender.sendRawMessage("&e |- players:");
            for (final UUID uuid : arena.getPlayers())
            {
                sender.sendRawMessage("&e   *" + uuid);
            }
        }
    }
}
