package pl.north93.zgame.lobby;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.pets.IPet;

public class PlayerPetsManager implements Listener
{
    private final Map<Player, IPet> playersPets = new WeakHashMap<>();

    public PlayerPetsManager()
    {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    public IPet getPlayersPet(final Player player)
    {
        return this.playersPets.get(player);
    }

    public boolean hasPet(final Player player)
    {
        return this.playersPets.containsKey(player);
    }

    public void setPlayersPet(final Player player, final IPet pet)
    {
        if (pet == null)
        {
            this.playersPets.remove(player);
        }
        this.playersPets.put(player, pet);
    }

    private void cleanup(final Player player)
    {
        if (! this.hasPet(player))
        {
            return;
        }
        final IPet pet = this.getPlayersPet(player);
        pet.setOwner(null);
        pet.kill();
        this.playersPets.remove(player);
    }

    @EventHandler
    public void onLeave(final PlayerQuitEvent event)
    {
        this.cleanup(event.getPlayer());
    }

    @EventHandler
    public void onKick(final PlayerKickEvent event)
    {
        this.cleanup(event.getPlayer());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("playersPets", this.playersPets).toString();
    }
}
