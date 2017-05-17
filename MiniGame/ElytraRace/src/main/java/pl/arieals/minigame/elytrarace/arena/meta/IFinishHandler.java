package pl.arieals.minigame.elytrarace.arena.meta;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;

public interface IFinishHandler
{
    void handle(LocalArena arena, Player player);

    static boolean checkFinished(final LocalArena arena)
    {
        for (final Player player : arena.getPlayersManager().getPlayers())
        {
            final ElytraRacePlayer playerData = getPlayerData(player, ElytraRacePlayer.class);
            if (! playerData.isFinished())
            {
                return false;
            }
        }

        return true;
    }
}
