package pl.arieals.lobby.chest.loot;

import static java.util.Collections.unmodifiableCollection;


import java.util.Collection;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Przedstawia wynik generowania lootu.
 */
public class LootResult
{
    private final Collection<ILoot> loot;

    public LootResult(final Collection<ILoot> loot)
    {
        this.loot = unmodifiableCollection(loot);
    }

    public Collection<ILoot> getLoot()
    {
        return this.loot;
    }

    /**
     * Aplikuje ten llot do podanego gracza.
     *
     * @param player Gracz ktoremu dajemy ten loot.
     */
    public void applyTo(final Player player)
    {
        this.loot.forEach(loot -> loot.apply(player));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("loot", this.loot).toString();
    }
}
