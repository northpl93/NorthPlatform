package pl.arieals.globalshops.shared;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public enum Rarity
{
    NORMAL(17),
    RARE(10),
    EPIC(4),
    LEGENDARY(1);

    private final int weight;

    Rarity(final int weight)
    {
        this.weight = weight;
    }

    /**
     * Zwraca wage tej grupy.
     * Uzywane przy losowosci wazonej.
     *
     * @return Waga tej opcji przy losowaniu wazonym.
     */
    public int getWeight()
    {
        return this.weight;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("weight", this.weight).toString();
    }
}
