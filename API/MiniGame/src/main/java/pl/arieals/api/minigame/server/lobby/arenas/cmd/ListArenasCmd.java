package pl.arieals.api.minigame.server.lobby.arenas.cmd;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.shared.api.GameIdentity;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.impl.ArenaManager;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ListArenasCmd extends NorthCommand
{
    @Inject
    private MiniGameServer miniGameServer;
    @Inject
    private ArenaManager   arenaManager;

    public ListArenasCmd()
    {
        super("listarenas");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.isEmpty())
        {
            this.printHelp(sender);
        }
        else if (args.length() == 1)
        {
            final String subCommand = args.asString(0);
            switch (subCommand)
            {
                case "all":
                    this.printAllArenas(sender);
                    break;
                case "statistics":
                    this.printStatistics(sender);
                    break;
                default:
                    this.printHelp(sender);
                    break;
            }
        }
        else
        {
            this.printHelp(sender);
        }
    }

    private void printAllArenas(final NorthCommandSender sender)
    {
        for (final RemoteArena arena : this.arenaManager.getAllArenas())
        {
            sender.sendMessage("&e- " + arena.getId());
            sender.sendMessage("&e |- serverId:" + arena.getServerId());
            sender.sendMessage("&e |- gamePhase:" + arena.getGamePhase());
            sender.sendMessage("&e |- players:");
            for (final UUID uuid : arena.getPlayers())
            {
                sender.sendMessage("&e   *" + uuid);
            }
        }
    }

    private void printStatistics(final NorthCommandSender sender)
    {
        final Set<RemoteArena> arenas = this.arenaManager.getAllArenas();

        // calkowita ilosc aktywnych aren w systemie
        final int totalArenas = arenas.size();

        sender.sendMessage("&eCalkowita ilosc aren: {0}", totalArenas);
        if (arenas.isEmpty())
        {
            // nie wyswietlamy reszty statystyk jesli nie ma aren...
            return;
        }

        sender.sendMessage("&eWedlug etapu gry:");

        final Map<GamePhase, List<RemoteArena>> arenasByGamePhase = arenas.stream().collect(Collectors.groupingBy(RemoteArena::getGamePhase));
        for (final Map.Entry<GamePhase, List<RemoteArena>> entry : arenasByGamePhase.entrySet())
        {
            final GamePhase gamePhase = entry.getKey();
            final int count = entry.getValue().size();

            final int percent = (int) ((count * 100D) / totalArenas);

            sender.sendMessage("&e  * {0} - {1} ({2}%)", gamePhase, count, percent);
        }

        sender.sendMessage("&eWedlug rodzaju gry:");

        final Map<GameIdentity, List<RemoteArena>> arenasByIdentity = arenas.stream().collect(Collectors.groupingBy(RemoteArena::getMiniGame));
        for (final Map.Entry<GameIdentity, List<RemoteArena>> entry : arenasByIdentity.entrySet())
        {
            final GameIdentity gameIdentity = entry.getKey();
            final int count = entry.getValue().size();

            final int percent = (int) ((count * 100D) / totalArenas);

            sender.sendMessage("&e  * {0}/{1} - {2} ({3}%)", gameIdentity.getGameId(), gameIdentity.getVariantId(), count, percent);
        }
    }

    private void printHelp(final NorthCommandSender sender)
    {
        sender.sendMessage("&e/listarenas all - lista wszystkich aren");
        sender.sendMessage("&e/listarenas statistics - statystyki");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
