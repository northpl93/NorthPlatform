package pl.arieals.minigame.elytrarace.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartedEvent;
import pl.arieals.api.minigame.server.gamehost.region.IRegionManager;
import pl.arieals.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.arieals.minigame.elytrarace.ElytraRaceMode;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ElytraScorePlayer;
import pl.arieals.minigame.elytrarace.cfg.Score;
import pl.arieals.minigame.elytrarace.cfg.ScoreGroup;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;

public class ScoreListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGH) // post ArenaStartListener
    public void startGame(final GameStartedEvent event)
    {
        final LocalArena arena = event.getArena();
        final ElytraRaceArena arenaData = arena.getArenaData();

        if (arenaData.getGameMode() == ElytraRaceMode.SCORE_MODE)
        {
            this.setupScoreMode(arena, arenaData);
        }
        else
        {
            this.removeScorePoints(arena, arenaData);
        }
    }

    private void setupScoreMode(final LocalArena arena, final ElytraRaceArena arenaData)
    {
        for (final Score score : arenaData.getArenaConfig().getScores())
        {
            final IRegionManager regionManager = arena.getRegionManager();
            final Cuboid cuboid = score.getArea().toCuboid(arena.getWorld().getCurrentWorld());

            final World world = cuboid.getWorld();
            for (final Block block : cuboid)
            {
                if (block.isEmpty())
                {
                    continue; // air
                }

                // pobierze typ bloku z podanej lokalizacji
                final Entity fallingBlock = world.spawnEntity(block.getLocation(), EntityType.FALLING_BLOCK);
                fallingBlock.setGravity(false);

                block.setType(Material.AIR);
            }

            final ITrackedRegion region = regionManager.create(cuboid);
            region.whenEnter(player -> this.addPoints(player, arenaData, score));
        }
    }

    private void addPoints(final Player player, final ElytraRaceArena arena, final Score score)
    {
        final ScoreGroup scoreGroup = arena.getScoreGroup(score.getScoreGroup());
        final ElytraScorePlayer scorePlayer = getPlayerData(player, ElytraScorePlayer.class);

        final List<Score> reachedScores = scorePlayer.getReachedScores();
        if (reachedScores.contains(score))
        {
            return;
        }
        reachedScores.add(score);

        final int combo = scorePlayer.checkCombo(scoreGroup);
        final int points;
        if (combo >= 3)
        {
            scorePlayer.setCombo(0);
            points = scoreGroup.getComboPoints();
        }
        else
        {
            points = scoreGroup.getPoints();
        }

        scorePlayer.incrementPoints(points);
        player.sendMessage("Zyskales " + points + " punktów!");
    }

    private void removeScorePoints(final LocalArena arena, final ElytraRaceArena arenaData)
    {
        for (final Score score : arenaData.getArenaConfig().getScores())
        {
            final Cuboid cuboid = score.getArea().toCuboid(arena.getWorld().getCurrentWorld());
            for (final Block block : cuboid)
            {
                if (block.isEmpty())
                {
                    continue; // air
                }
                block.setType(Material.AIR);
            }
        }
    }
}
