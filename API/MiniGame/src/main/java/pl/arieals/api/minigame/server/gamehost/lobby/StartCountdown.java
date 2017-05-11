package pl.arieals.api.minigame.server.gamehost.lobby;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.PlayersManager;
import pl.arieals.api.minigame.server.gamehost.utils.Countdown;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardManager;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;

public class StartCountdown extends Countdown
{
    @InjectComponent("API.Scoreboard")
    private IScoreboardManager scoreboardManager;
    private final LocalArena   arena;

    public StartCountdown(final LocalArena arena)
    {
        super(30);
        this.arena = arena;
    }

    @Override
    protected void loop(final int time)
    {
        final PlayersManager playersManager = this.arena.getPlayersManager();
        for (final Player player : playersManager.getPlayers())
        {
            player.sendMessage("Start areny za " + time);
            this.scoreboardManager.getContext(player).update();
        }
    }

    @Override
    protected void end()
    {

    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arena", this.arena).toString();
    }
}
