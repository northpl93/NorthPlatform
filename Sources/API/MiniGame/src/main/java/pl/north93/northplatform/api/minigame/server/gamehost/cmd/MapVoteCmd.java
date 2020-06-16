package pl.north93.northplatform.api.minigame.server.gamehost.cmd;

import java.util.Locale;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.text.similarity.JaroWinklerDistance;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.minigame.server.MiniGameServer;
import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.world.MapVote;
import pl.north93.northplatform.api.minigame.server.lobby.LobbyManager;
import pl.north93.northplatform.api.minigame.shared.api.MapTemplate;

public class MapVoteCmd extends NorthCommand
{
    private static final double DISTANCE_THRESHOLD = 0.5;
    private static final JaroWinklerDistance DISTANCE = new JaroWinklerDistance();
    @Inject @Messages("MiniGameApi")
    private MessagesBox messages;
    @Inject
    private MiniGameServer server;

    public MapVoteCmd()
    {
        super("mapvote", "votemap", "vote");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (this.server.getServerManager() instanceof LobbyManager)
        {
            sender.sendMessage(this.messages, "cmd.general.only_gamehost");
            return;
        }

        final Player player = (Player) sender.unwrapped();
        final GameHostManager gameHostManager = this.server.getServerManager();

        final LocalArena arena = gameHostManager.getArenaManager().getArena(player);
        if (arena == null)
        {
            sender.sendMessage(this.messages, "vote.unavailable");
            return;
        }

        final MapVote mapVote = arena.getMapVote();
        if (mapVote == null)
        {
            sender.sendMessage(this.messages, "vote.unavailable");
            return;
        }

        if (args.isEmpty())
        {
            sender.sendMessage(this.messages, "vote.arguments");
            return;
        }

        final int option = this.getOption(mapVote, args);
        final MapTemplate template = mapVote.vote(player, option);
        if (template == null)
        {
            sender.sendMessage(this.messages, "vote.arguments");
            return;
        }

        sender.sendMessage(this.messages, "vote.success", template.getDisplayName());
    }

    private int getOption(final MapVote vote, final Arguments arguments)
    {
        if (arguments.isEmpty())
        {
            // uzytkownik nie podal zadnego argumentu
            return -1;
        }
        else if (arguments.length() == 1)
        {
            // uzytkownik podal jeden argument, prawdopodobnie cyfre
            final Integer number = arguments.asInt(0);
            if (number != null)
            {
                // faktycznie cyfra, zwracamy opcje
                return number;
            }
        }

        // próbujemy dopasowac mape po podanej nazwie
        return this.getOptionByDistance(vote, arguments);
    }

    private int getOptionByDistance(final MapVote vote, final Arguments arguments)
    {
        final String userInput = arguments.asText(0).toLowerCase(Locale.ROOT);
        final MapTemplate[] options = vote.getOptions();

        for (int i = 0; i < options.length; i++)
        {
            final MapTemplate mapTemplate = options[i];

            final String mapName = mapTemplate.getDisplayName().toLowerCase(Locale.ROOT);
            final double distance = DISTANCE.apply(userInput, mapName);
            if (distance <= DISTANCE_THRESHOLD)
            {
                continue;
            }

            return i + 1;
        }

        return -1;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
