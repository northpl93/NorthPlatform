package pl.arieals.minigame.bedwars.arena;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class PlayerReconnectTimedOut implements Runnable
{
    @Inject
    private Logger logger;
    private final BedWarsPlayer player;

    public PlayerReconnectTimedOut(final BedWarsPlayer player)
    {
        this.player = player;
    }

    @Override
    public void run()
    {
        if (this.player.isOnline())
        {
            // gracz wrocil na czas
            return;
        }

        // eliminujemy tego gracza
        this.player.eliminate();

        final Player bukkitPlayer = this.player.getBukkitPlayer();
        this.logger.log(Level.INFO, "Player {0} doesn't returned in time, marked as eliminated.", bukkitPlayer.getName());

        final Team team = this.player.getTeam();
        if (team.isAnyPlayerAlive())
        {
            // w teamie został inny grający gracz, więc nic dalej nie kombinujemy
            return;
        }

        // niszczymy łózko tego zespołu bo nie został w nim żaden żywy gracz online
        // to powinno także oznaczyć team jako wyeliminowany
        team.destroyBed(true);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).toString();
    }
}
