package pl.arieals.api.minigame.server.gamehost.arena;

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;

import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GamePhaseEventFactory;
import pl.arieals.api.minigame.shared.api.GameIdentity;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.arena.IArena;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.api.arena.netevent.ArenaCreatedNetEvent;
import pl.arieals.api.minigame.shared.api.cfg.MiniGameConfig;
import pl.arieals.api.minigame.shared.impl.arena.ArenaManager;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.chat.global.ChatManager;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.chat.global.ChatRoomPriority;
import pl.north93.zgame.api.chat.global.formatter.PermissionsBasedFormatter;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class LocalArenaManager
{
    @Inject
    private BukkitApiCore  apiCore;
    @Inject
    private Logger         logger;
    @Inject
    private MiniGameServer miniGameServer;
    @Inject
    private ChatManager    chatManager;
    private final List<LocalArena> arenas = new ArrayList<>();

    public LocalArena createArena()
    {
        final GameHostManager serverManager = this.miniGameServer.getServerManager();
        final MiniGameConfig miniGameConfig = serverManager.getMiniGameConfig();
        final ArenaManager arenaManager = this.miniGameServer.getArenaManager();

        final UUID arenaId = UUID.randomUUID();
        final UUID serverId = this.apiCore.getServerId();
        final GameIdentity miniGame = miniGameConfig.getGameIdentity();
        final Boolean dynamic = miniGameConfig.isDynamic();
        final Integer maxPlayers = miniGameConfig.getSlots();

        final RemoteArena arenaData = new RemoteArena(arenaId, serverId, miniGame, dynamic, GamePhase.INITIALISING, maxPlayers, new HashSet<>());
        final LocalArena localArena = new LocalArena(serverManager, arenaManager, arenaData);
        this.arenas.add(localArena);
        arenaManager.setArena(arenaData);

        GamePhaseEventFactory.getInstance().callEvent(localArena); // invoke GameInitEvent
        serverManager.publishArenaEvent(new ArenaCreatedNetEvent(arenaId, miniGame));

        final String msg = "Added new local arena! GameID:{0}/{1}, ArenaID:{2}, ServerID:{3}, GamePhase:{4}";
        this.logger.info(format(msg, miniGame.getGameId(), miniGame.getVariantId(), arenaId, serverId, arenaData.getGamePhase()));

        return localArena;
    }

    public List<LocalArena> getArenas()
    {
        return this.arenas;
    }

    public Optional<LocalArena> getArenaAssociatedWith(final UUID player)
    {
        return this.arenas.stream().filter(arena -> arena.getPlayersManager().containsPlayer(player)).findFirst();
    }

    public LocalArena getArena(final UUID arenaId)
    {
        for (final LocalArena arena : this.arenas)
        {
            if (arena.getId().equals(arenaId))
            {
                return arena;
            }
        }
        return null;
    }

    public LocalArena getArena(final World world)
    {
        Preconditions.checkNotNull(world, "world can't be null");
        for (final LocalArena arena : this.arenas)
        {
            if (world.equals(arena.getWorld().getCurrentWorld()))
            {
                return arena;
            }
        }
        return null;
    }

    public void removeArenas()
    {
        for (final LocalArena arena : new ArrayList<>(this.arenas)) // unikamy ConcurrentModificationException
        {
            arena.delete();
        }
    }

    public ChatRoom getChatRoomFor(final IArena arena, final boolean spectators)
    {
        final ChatRoom room;
        if (spectators)
        {
            final String id = "spectators:" + arena.getId();
            room = this.chatManager.getOrCreateRoom(id, PermissionsBasedFormatter.INSTANCE, ChatRoomPriority.HIGH);
        }
        else
        {
            final String id = "arena:" + arena.getId();
            room =  this.chatManager.getOrCreateRoom(id, PermissionsBasedFormatter.INSTANCE, ChatRoomPriority.NORMAL);
        }

        if (room.getParent() == null)
        {
            // jesli pokój areny nie ma rodzica to dodajemy go jako dziecko do ogólnego pokoju minigry
            this.getGameRoom().addChild(room);
        }

        return room;
    }

    private ChatRoom getGameRoom()
    {
        final GameHostManager serverManager = this.miniGameServer.getServerManager();

        final String roomId = "game:" + serverManager.getMiniGameConfig().getGameIdentity().getGameId();
        return this.chatManager.getOrCreateRoom(roomId, PermissionsBasedFormatter.INSTANCE, ChatRoomPriority.NORMAL);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arenas", this.arenas).toString();
    }
}
