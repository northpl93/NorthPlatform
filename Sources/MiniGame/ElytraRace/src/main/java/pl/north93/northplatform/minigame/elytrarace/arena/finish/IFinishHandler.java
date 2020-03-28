package pl.north93.northplatform.minigame.elytrarace.arena.finish;

import org.bukkit.entity.Player;

import pl.north93.northplatform.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;

public interface IFinishHandler
{
    void handle(LocalArena arena, INorthPlayer player, ElytraRacePlayer elytraPlayer);

    void playerQuit(LocalArena arena, Player player);

    void gameEnd(LocalArena arena);

    static boolean checkFinished(final LocalArena arena)
    {
        for (final INorthPlayer player : arena.getPlayersManager().getPlayers())
        {
            final ElytraRacePlayer playerData = player.getPlayerData(ElytraRacePlayer.class);
            if (! playerData.isFinished())
            {
                return false;
            }
        }

        return true;
    }
}
