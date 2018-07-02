package pl.arieals.minigame.bedwars.event;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerArenaEvent;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;

/**
 * Event powiązany z graczem BedWars i areną.
 */
public abstract class BedWarsPlayerArenaEvent extends PlayerArenaEvent
{
    private final BedWarsPlayer bedWarsPlayer;

    /**
     * Konstruktor pobierający Bukkitowego Playera z BedWarsPlayer.
     *
     * @param arena Arena na której gra gracz.
     * @param bedWarsPlayer BedWarsPlayer powiązany z graczem.
     */
    public BedWarsPlayerArenaEvent(final LocalArena arena, final BedWarsPlayer bedWarsPlayer)
    {
        super(arena, bedWarsPlayer.getBukkitPlayer());
        this.bedWarsPlayer = bedWarsPlayer;
    }

    /**
     * Konstruktor pobierający BedWarsPlayer z Bukkitowego Playera.
     *
     * @throws IllegalArgumentException W przypadku gdy gracz nie posiada  powiązanego BedWarsPlayer.
     * @param arena Arena na której gra gracz.
     * @param player Player z którego zostani pobrany BedWarsPlayer.
     */
    public BedWarsPlayerArenaEvent(final LocalArena arena, final Player player)
    {
        super(arena, player);

        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        if (playerData == null)
        {
            throw new IllegalArgumentException("No BedWarsPlayer found");
        }

        this.bedWarsPlayer = playerData;
    }

    public BedWarsPlayer getBedWarsPlayer()
    {
        return this.bedWarsPlayer;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("bedWarsPlayer", this.bedWarsPlayer).toString();
    }
}
