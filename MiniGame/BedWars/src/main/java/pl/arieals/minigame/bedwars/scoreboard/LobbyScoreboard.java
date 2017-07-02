package pl.arieals.minigame.bedwars.scoreboard;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;


import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class LobbyScoreboard implements IScoreboardLayout
{
    @Inject @Messages("BedWars")
    private MessagesBox messages;

    @Override
    public String getTitle(final IScoreboardContext context)
    {
        return "&e&lBEDWARS";
    }

    @Override
    public List<String> getContent(final IScoreboardContext context)
    {
        final LocalArena arena = getArena(context.getPlayer());

        final ContentBuilder builder = IScoreboardLayout.builder();
        builder.box(this.messages).locale(context.getLocale());
        builder.add("");

        builder.translated("scoreboard.lobby.mode", "Solo");
        builder.translated("scoreboard.lobby.players", arena.getPlayers().size(), arena.getPlayersManager().getMaxPlayers());
        builder.translated("scoreboard.lobby.map", arena.getWorld().getCurrentMapTemplate().getDisplayName());

        builder.add("");

        if (arena.getPlayersManager().isEnoughToStart())
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
}
