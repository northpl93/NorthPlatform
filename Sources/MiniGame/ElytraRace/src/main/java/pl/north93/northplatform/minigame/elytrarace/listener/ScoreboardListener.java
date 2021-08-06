package pl.north93.northplatform.minigame.elytrarace.listener;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardManager;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.north93.northplatform.minigame.elytrarace.ElytraRaceMode;
import pl.north93.northplatform.minigame.elytrarace.arena.ElytraRaceArena;
import pl.north93.northplatform.minigame.elytrarace.scoreboard.LobbyScoreboard;
import pl.north93.northplatform.minigame.elytrarace.scoreboard.RaceScoreboard;
import pl.north93.northplatform.minigame.elytrarace.scoreboard.ScoreScoreboard;

public class ScoreboardListener implements AutoListener
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
        if (arenaData.getGameMode() == ElytraRaceMode.RACE_MODE)
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
