package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.deathmatch.DeathMatchLoadedEvent;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class BwDeathMatchStartListener implements Listener
{
    @Inject
    private Logger logger;

    @EventHandler
    public void onDeathMatchStart(final DeathMatchLoadedEvent event)
    {
        final LocalArena arena = event.getArena();
        this.logger.log(Level.INFO, "Bedwars is preparing deathmatch on {0}", arena.getId());

        final BedWarsArena arenaData = arena.getArenaData();
        arenaData.getGenerators().clear(); // usuwamy generatory by je wylaczyc
        arenaData.getPlayerBlocks().clear(); // usuwamy bloki by nie trzymac referencji na swiat
        arenaData.getSecureRegions().clear(); // usuwamy bo niepotrzebne

        for (final Player player : arena.getPlayersManager().getPlayers())
        {
            final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
            if (playerData == null)
            {
                continue;
            }

            while (playerData.getLives() > 0)
            {
                playerData.removeLife();
            }
        }
    }
}
