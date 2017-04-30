package pl.arieals.api.minigame.server.gamehost.lobby.external;

import java.util.Optional;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;

public class MapVoteCmd extends NorthCommand
{
    @InjectComponent("MiniGameApi.Server")
    private MiniGameServer server;

    public MapVoteCmd()
    {
        super("mapvote");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final GameHostManager gameHostManager = this.server.getServerManager();

        final Optional<LocalArena> arenaOptional = gameHostManager.getArenaManager().getArenaAssociatedWith(player.getUniqueId());
        if (! arenaOptional.isPresent())
        {
            return;
        }

        final LocalArena arena = arenaOptional.get();

        if (arena.getWorld().getMapVote().vote(player, args.asInt(0)))
        {
            sender.sendMessage("&cUdalo sie zaglosowac!");
        }
        else
        {
            sender.sendMessage("&cNie mozna bylo zaglosowac");
        }
    }
}
