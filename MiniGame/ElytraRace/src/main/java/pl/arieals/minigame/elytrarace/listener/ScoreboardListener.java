package pl.arieals.minigame.elytrarace.listener;

import static pl.arieals.minigame.elytrarace.ElytraRaceMode.RACE_MODE;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.scoreboard.LobbyScoreboard;
import pl.arieals.minigame.elytrarace.scoreboard.RaceScoreboard;
import pl.arieals.minigame.elytrarace.scoreboard.ScoreScoreboard;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardManager;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ScoreboardListener implements Listener
{
    @Inject
    private IScoreboardManager scoreboardManager;

    @EventHandler
    public void lobbyJoin(final PlayerJoinArenaEvent event)
    {
        // ustawiamy graczowi scoreboard lobby gdy wejdzie
        this.scoreboardManager.setLayout(event.getPlayer(), new LobbyScoreboard());
    }

    @EventHandler
    public void gameStart(final GameStartEvent event)
    {
        final ElytraRaceArena arenaData = event.getArena().getArenaData();

        final IScoreboardLayout layout;
        if (arenaData.getGameMode() == RACE_MODE)
        {
            layout = new RaceScoreboard();
        }
        else
        {
            layout = new ScoreScoreboard();
        }

        for (final Player player : event.getArena().getPlayersManager().getPlayers())
        {
            this.scoreboardManager.setLayout(player, layout);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
