package pl.north93.northplatform.api.minigame.server.lobby.arenas.cmd;

import java.util.Map;
import java.util.UUID;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.metadata.MetaKey;
import pl.north93.northplatform.api.minigame.server.MiniGameServer;
import pl.north93.northplatform.api.minigame.shared.api.GameIdentity;
import pl.north93.northplatform.api.minigame.shared.api.arena.RemoteArena;
import pl.north93.northplatform.api.minigame.shared.impl.arena.ArenaManager;

public class ArenaInfoCmd extends NorthCommand
{
    @Inject
    private MiniGameServer miniGameServer;
    @Inject
    private ArenaManager arenaManager;

    public ArenaInfoCmd()
    {
        super("arenainfo");
        this.setPermission("minigameapi.cmd.arenainfo");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.isEmpty())
        {
            sender.sendMessage("&c/arenainfo <uuid>");
            return;
        }

        final UUID arenaId = UUID.fromString(args.asString(0));

        final RemoteArena arena = this.arenaManager.getArena(arenaId);
        if (arena == null)
        {
            sender.sendMessage("&cNie ma areny o takim UUID");
            return;
        }

        sender.sendMessage("&eArena ID: &f{0}", arena.getId());
        sender.sendMessage("&eServer ID: &f{0}", arena.getServerId());

        final GameIdentity miniGame = arena.getMiniGame();
        sender.sendMessage("&eGame ID: &f{0} &eVariant ID: &f{1}", miniGame.getGameId(), miniGame.getVariantId());

        sender.sendMessage("&eGamePhase: &f{0}", arena.getGamePhase());

        sender.sendMessage("&eMetadata: ", arena.getId());
        for (final Map.Entry<MetaKey, Object> metaEntry : arena.getMetaStore().getInternalMap().entrySet())
        {
            final String keyName = metaEntry.getKey().getKey();
            sender.sendMessage("  &e{0}: &f{1}", keyName, metaEntry.getValue());
        }
    }
}
