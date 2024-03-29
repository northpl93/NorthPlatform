package pl.north93.northplatform.minigame.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.deathmatch.DeathMatchLoadedEvent;
import pl.north93.northplatform.minigame.bedwars.arena.BedWarsArena;
import pl.north93.northplatform.minigame.bedwars.arena.BedWarsPlayer;

@Slf4j
public class BwDeathMatchStartListener implements AutoListener
{
    @EventHandler
    public void onDeathMatchStart(final DeathMatchLoadedEvent event)
    {
        final LocalArena arena = event.getArena();
        log.info("Bedwars is preparing deathmatch on {}", arena.getId());

        final BedWarsArena arenaData = arena.getArenaData();
        arenaData.getGenerators().clear(); // usuwamy generatory by je wylaczyc
        arenaData.getPlayerBlocks().clear(); // usuwamy bloki by nie trzymac referencji na swiat
        arenaData.getSecureRegions().clear(); // usuwamy bo niepotrzebne

        for (final INorthPlayer player : arena.getPlayersManager().getPlayers())
        {
            this.preparePlayerToDeathMatch(player);
        }
    }

    private void preparePlayerToDeathMatch(final INorthPlayer player)
    {
        final BedWarsPlayer playerData = player.getPlayerData(BedWarsPlayer.class);
        if (playerData == null)
        {
            return;
        }

        while (playerData.getLives() > 0)
        {
            playerData.removeLife();
        }

        // usuwamy efekt nadany przez upgrade Healbot który powinien dzialac tylko na terenie bazy
        player.removePotionEffect(PotionEffectType.REGENERATION);
    }
}
