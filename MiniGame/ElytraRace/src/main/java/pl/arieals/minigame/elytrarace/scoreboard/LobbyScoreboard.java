package pl.arieals.minigame.elytrarace.scoreboard;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;


import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;

public class LobbyScoreboard implements IScoreboardLayout
{
    public static final LobbyScoreboard INSTANCE = new LobbyScoreboard();

    private LobbyScoreboard()
    {
    }

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

        if (arena.getStartScheduler().isStartScheduled())
        {
            content.add("&7Start za");
            content.add("&6" + arena.getTimer().calcTimeTo(0, TimeUnit.SECONDS, TimeUnit.SECONDS) + " sekund");
        }
        else
        {
            content.add("&7Czekanie na", "&7graczy");
        }

        content.add("", "&7Ilosc graczy", "&6" + arena.getPlayers().size() + " / " + arena.getPlayersManager().getMaxPlayers());

        return content.getContent();
    }

    @Override
    public int updateEvery()
    {
        return 10;
    }
}
