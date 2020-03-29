package pl.north93.northplatform.minigame.elytrarace.scoreboard;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;


import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.messages.TranslatableString;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.minigame.elytrarace.ElytraRaceMode;
import pl.north93.northplatform.minigame.elytrarace.arena.ElytraRaceArena;

public class LobbyScoreboard implements IScoreboardLayout
{
    private static final int REFRESH_TICKS = 20;
    @Inject @Messages("ElytraRace")
    private MessagesBox msg;

    @Override
    public String getTitle(final IScoreboardContext context)
    {
        return "&e&lELYTRA RACE";
    }

    @Override
    public List<String> getContent(final IScoreboardContext context)
    {
        final Player player = context.getPlayer();
        final LocalArena arena = getArena(player);
        assert arena != null;

        final ElytraRaceArena arenaData = arena.getArenaData();

        final ContentBuilder content = IScoreboardLayout.builder();
        content.box(this.msg).locale(context.getLocale());
        content.add("");

        final String modeName = arenaData.getGameMode() == ElytraRaceMode.RACE_MODE ? "@scoreboard.lobby.mode_race" : "@scoreboard.lobby.mode_score";
        content.translated("scoreboard.lobby.mode", TranslatableString.of(this.msg, modeName));
        content.add("");

        content.translated("scoreboard.lobby.map", arena.getWorld().getCurrentMapTemplate().getDisplayName());
        content.add("");

        content.translated("scoreboard.lobby.players", arena.getPlayers().size(), arena.getPlayersManager().getMaxPlayers());
        content.add("");

        if (arena.getStartScheduler().isStartScheduled())
        {
            content.translated("scoreboard.lobby.start", arena.getTimer().calcTimeTo(0, TimeUnit.SECONDS, TimeUnit.SECONDS));
        }
        else
        {
            content.translated("scoreboard.lobby.waiting");
        }

        content.add("");
        content.translated("scoreboard.ip");

        return content.getContent();
    }

    @Override
    public int updateEvery()
    {
        return REFRESH_TICKS;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
