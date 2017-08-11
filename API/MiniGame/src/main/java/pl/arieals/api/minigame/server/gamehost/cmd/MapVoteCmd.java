package pl.arieals.api.minigame.server.gamehost.cmd;

import java.util.Optional;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.MapVote;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class MapVoteCmd extends NorthCommand
{
    @Inject @Messages("MiniGameApi")
    private MessagesBox    messages;
    @Inject
    private MiniGameServer server;

    public MapVoteCmd()
    {
        super("mapvote");
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

        final Optional<LocalArena> arenaOptional = gameHostManager.getArenaManager().getArenaAssociatedWith(player.getUniqueId());
        if (! arenaOptional.isPresent())
        {
            return; // todo komunikat o braku areny(?)/wylaczonej komendzie
        }

        final LocalArena arena = arenaOptional.get();

        final MapVote mapVote = arena.getMapVote();
        if (mapVote == null)
        {
            return; // todo komunikat o wylaczonym glosowaniu
        }

        if (mapVote.vote(player, args.asInt(0)))
        {
            sender.sendRawMessage("&cUdalo sie zaglosowac!");
        }
        else
        {
            sender.sendRawMessage("&cNie mozna bylo zaglosowac");
        }
    }
}
