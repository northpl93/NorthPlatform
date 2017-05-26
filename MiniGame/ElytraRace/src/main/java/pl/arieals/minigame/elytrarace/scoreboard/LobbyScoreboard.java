package pl.arieals.minigame.elytrarace.scoreboard;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;


import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.zgame.api.global.component.annotations.InjectMessages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class LobbyScoreboard implements IScoreboardLayout
{
    @InjectMessages("ElytraRace")
    private MessagesBox msg;

    @Override
    public String getTitle(final IScoreboardContext context)
    {
        return "&eElytra Race";
    }

    @Override
    public List<String> getContent(final IScoreboardContext context)
    {
        final Player player = context.getPlayer();
        final LocalArena arena = getArena(player);

        final ContentBuilder content = IScoreboardLayout.builder();
        content.box(this.msg).locale(player.spigot().getLocale());

        if (arena.getStartScheduler().isStartScheduled())
        {
            content.translated("scoreboard.lobby.start", arena.getTimer().calcTimeTo(0, TimeUnit.SECONDS, TimeUnit.SECONDS));
        }
        else
        {
            content.translated("scoreboard.lobby.waiting");
        }

        content.add("");
        content.translated("scoreboard.lobby.players", arena.getPlayers().size(), arena.getPlayersManager().getMaxPlayers());

        return content.getContent();
    }

    @Override
    public int updateEvery()
    {
        return 10;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
