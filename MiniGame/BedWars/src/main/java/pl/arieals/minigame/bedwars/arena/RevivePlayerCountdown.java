package pl.arieals.minigame.bedwars.arena;

import com.destroystokyo.paper.Title;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.utils.AbstractCountdown;

public class RevivePlayerCountdown extends AbstractCountdown
{
    private final Player        player;
    private final BedWarsPlayer bedWarsPlayer;

    public RevivePlayerCountdown(final Player player, final BedWarsPlayer bedWarsPlayer)
    {
        super(5);
        this.player = player;
        this.bedWarsPlayer = bedWarsPlayer;
    }

    @Override
    protected void loop(final int time)
    {
        this.player.sendTitle(new Title("Respawn za " + time, "", 0, 20, 0));
    }

    @Override
    protected void end()
    {
        if (! this.player.isOnline())
        {
            return;
        }

        this.player.setAllowFlight(false);
        this.player.setVisible(true);
        this.player.sendMessage("Revived");
        this.player.teleport(this.bedWarsPlayer.getTeam().getSpawn());
    }
}
