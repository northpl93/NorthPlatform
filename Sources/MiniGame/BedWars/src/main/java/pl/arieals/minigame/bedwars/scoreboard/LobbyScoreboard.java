package pl.arieals.minigame.bedwars.scoreboard;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;


import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.cfg.BwConfig;
import pl.north93.zgame.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;

public class LobbyScoreboard implements IScoreboardLayout
{
    @Inject @Messages("BedWars")
    private MessagesBox messages;
    @Inject
    private BwConfig    bwConfig;

    @Override
    public String getTitle(final IScoreboardContext context)
    {
        return "&e&lBEDWARS";
    }

    @Override
    public List<String> getContent(final IScoreboardContext context)
    {
        final LocalArena arena = getArena(context.getPlayer());
        assert arena != null;

        final ContentBuilder builder = IScoreboardLayout.builder();
        builder.box(this.messages).locale(context.getLocale());
        builder.add("");

        final TranslatableString modeName = TranslatableString.of(this.messages, "@scoreboard.mode." + this.bwConfig.getTeamSize());
        builder.translated("scoreboard.lobby.mode", modeName);
        builder.add("");

        builder.translated("scoreboard.lobby.map", arena.getWorld().getCurrentMapTemplate().getDisplayName());
        builder.add("");

        builder.translated("scoreboard.lobby.players", arena.getPlayers().size(), arena.getPlayersManager().getMaxPlayers());
        builder.add("");

        if (arena.getStartScheduler().isStartScheduled())
        {
            builder.translated("scoreboard.lobby.starting", arena.getTimer().calcTimeTo(0, TimeUnit.SECONDS, TimeUnit.SECONDS));
        }
        else
        {
            builder.translated("scoreboard.lobby.waiting");
        }

        builder.add("");
        builder.translated("scoreboard.ip");

        return builder.getContent();
    }

    @Override
    public int updateEvery()
    {
        return 20;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
