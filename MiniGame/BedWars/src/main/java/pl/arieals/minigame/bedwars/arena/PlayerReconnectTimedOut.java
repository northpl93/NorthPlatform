package pl.arieals.minigame.bedwars.arena;

import java.util.logging.Level;
import java.util.logging.Logger;

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
        if (this.player.getBukkitPlayer().isOnline())
        {
            // gracz wrocil na czas
            return;
        }

        this.logger.log(Level.INFO, "Player {0} doesn't returned in time, marked as eliminated.", this.player.getBukkitPlayer().getName());
        this.player.setEliminated(true);
        // nie ma potrzeby wywolywania eliminatedevent.
        //
        // jesli ktos zniszczyl lozko to wtedy poszedl teameliminatedevent,
        // jak lozko jeszcze stoi to ktos je kiedys zniszczy to pojdzie eliminatedevent
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).toString();
    }
}
