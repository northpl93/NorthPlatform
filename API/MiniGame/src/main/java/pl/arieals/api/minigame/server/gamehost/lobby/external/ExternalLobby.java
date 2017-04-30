package pl.arieals.api.minigame.server.gamehost.lobby.external;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.lobby.ILobbyManager;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.PostInject;

public class ExternalLobby implements ILobbyManager
{
    private Map<Player, LocalArena> players = new WeakHashMap<>();
    private BukkitApiCore apiCore;

    @PostInject
    private void init()
    {
        this.apiCore.registerEvents(new LobbyExitTracker());
    }

    @Override
    public void addPlayer(final LocalArena arena, final Player player)
    {
        for (final Map.Entry<Player, LocalArena> entry : this.players.entrySet())
        {
            if (entry.getValue() == arena)
            {
                continue;
            }

            player.hidePlayer(entry.getKey());
            entry.getKey().hidePlayer(player);
        }
        this.players.put(player, arena);
    }

    @Override
    public void removePlayer(final Player player)
    {
        this.players.remove(player);
        for (final Player inLobby : this.players.keySet())
        {
            player.showPlayer(inLobby);
            inLobby.showPlayer(player);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("players", this.players).toString();
    }
}
