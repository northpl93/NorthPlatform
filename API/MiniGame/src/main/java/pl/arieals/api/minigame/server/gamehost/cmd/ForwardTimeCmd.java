package pl.arieals.api.minigame.server.gamehost.cmd;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;


import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class ForwardTimeCmd extends NorthCommand
{
    public ForwardTimeCmd()
    {
        super("forwardtime");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final LocalArena arena = getArena(player);

        if (arena.getGamePhase() != GamePhase.STARTED)
        {
            sender.sendRawMessage("&cTa komenda dziala tylko gdy arena jest w gamephase STARTED.");
            return;
        }

        if (args.length() == 1)
        {
            final Long seconds = args.asLong(0);

            sender.sendRawMessage("&aAktualny czas areny: &e{0}&a. Dodawanie &e{1} &asekund.", arena.getTimer(), seconds);
            arena.forwardTime(seconds, TimeUnit.SECONDS);
            sender.sendRawMessage("&aZakonczono symulacje. Nowy czas areny: &e{0}", arena.getTimer());

        }
        else
        {
            sender.sendRawMessage("&a/forwardtime <czas w sekundach>");
        }
    }
}
