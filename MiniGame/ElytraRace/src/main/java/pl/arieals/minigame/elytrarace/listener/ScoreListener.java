package pl.arieals.minigame.elytrarace.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.api.minigame.server.gamehost.region.IRegionManager;
import pl.arieals.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.arieals.minigame.elytrarace.ElytraRaceMode;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ElytraScorePlayer;
import pl.arieals.minigame.elytrarace.arena.ScoreController;
import pl.arieals.minigame.elytrarace.cfg.Score;
import pl.arieals.minigame.elytrarace.cfg.ScoreGroup;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class ScoreListener implements Listener
{
    @Inject @Messages("ElytraRace")
    private MessagesBox messages;

    @EventHandler(priority = EventPriority.HIGH) // post ArenaStartListener
    public void startGame(final GameStartEvent event)
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

            final ITrackedRegion region = regionManager.create(cuboid);
            region.whenEnter(player -> this.addPoints(player, arenaData, score));

            final ScoreController scoreController = new ScoreController(arena, score);
            arenaData.getScoreControllers().put(score, scoreController);
            scoreController.setup();
            for (final Player player : arena.getPlayersManager().getPlayers())
            {
                scoreController.makeNormal(player);
            }
        }
    }

    private void addPoints(final Player player, final ElytraRaceArena arena, final Score score)
    {
        final ScoreGroup scoreGroup = arena.getScoreGroup(score.getScoreGroup());
        final ElytraScorePlayer scorePlayer = getPlayerData(player, ElytraScorePlayer.class);

        // gracz moze zaliczyc tylko jeden score z danej achieveGroup
        final String achieveGroup = score.getAchieveGroup();
        if (achieveGroup != null)
        {
            final List<String> reachedAchieveGroups = scorePlayer.getReachedAchieveGroups();
            if (reachedAchieveGroups.contains(achieveGroup))
            {
                return;
            }
            reachedAchieveGroups.add(achieveGroup);

            // wyszarzamy odpowiednie score na podstawie achievegroup
            for (final Score scoreToCheck : arena.getArenaConfig().getScores())
            {
                if (scoreToCheck == score || ! score.getAchieveGroup().equals(scoreToCheck.getAchieveGroup()))
                {
                    continue;
                }

                arena.getScoreController(scoreToCheck).makeGray(player);
            }
        }

        // gracz moze zaliczyc kazdy score jeden raz
        final List<Score> reachedScores = scorePlayer.getReachedScores();
        if (reachedScores.contains(score))
        {
            return;
        }
        reachedScores.add(score);

        // wyszarzamy ten score.
        final ScoreController scoreController = arena.getScoreController(score);
        scoreController.makeGray(player);
        scoreController.playBreakAnimation(player);

        final int combo = scorePlayer.checkCombo(scoreGroup);
        int points = scoreGroup.getPoints();

        this.messages.sendMessage(player, "score.points_added", points, this.pointsForm(player, points));

        if (combo >= 3)
        {
            scorePlayer.setCombo(0);
            final int comboPoints = scoreGroup.getComboPoints();
            points += comboPoints;
            this.messages.sendMessage(player, "score.points_added_combo", comboPoints, this.pointsForm(player, comboPoints));
        }

        scorePlayer.incrementPoints(points);
    }

    private String pointsForm(final Player player, final int points)
    {
        final String locale = player.spigot().getLocale();
        if (points == 1)
        {
            return this.messages.getMessage(locale, "score.points.one");
        }
        else if (points < 5)
        {
            return this.messages.getMessage(locale, "score.points.some");
        }
        return this.messages.getMessage(locale, "score.points.many");
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
                block.setType(Material.AIR, false);
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
