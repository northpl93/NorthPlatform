package pl.north93.northplatform.minigame.bedwars.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.minigame.bedwars.arena.BedWarsPlayer;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerArenaEvent;

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
    public BedWarsPlayerArenaEvent(final LocalArena arena, final INorthPlayer player)
    {
        super(arena, player);

        final BedWarsPlayer playerData = player.getPlayerData(BedWarsPlayer.class);
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
