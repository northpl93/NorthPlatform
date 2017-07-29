package pl.arieals.api.minigame.server.gamehost.cmd;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;


import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class CancelStarting extends NorthCommand
{
    public CancelStarting()
    {
        super("cancelstarting");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final LocalArena arena = getArena(player);
        if (arena == null)
        {
            sender.sendRawMessage("&cBrak areny powiazanej z graczem.");
            return;
        }

        if (arena.getGamePhase() != GamePhase.LOBBY)
        {
            sender.sendRawMessage("&cAnulowac start mozna tylko w gamephase lobby.");
            return;
        }

        if (arena.getStartScheduler().isStartScheduled())
        {
            sender.sendRawMessage("&aAnulowano start.");
            arena.getStartScheduler().cancelStarting();
        }
        else
        {
            sender.sendRawMessage("&cOdliczanie do startu nie jest aktywne.");
        }
    }
}
