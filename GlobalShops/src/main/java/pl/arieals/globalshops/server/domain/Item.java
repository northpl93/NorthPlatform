package pl.arieals.globalshops.server.domain;

import java.util.Locale;
import java.util.Map;

import com.carrotsearch.hppc.IntObjectMap;
import com.google.common.base.Preconditions;

import lombok.AllArgsConstructor;
import lombok.ToString;
import pl.arieals.globalshops.shared.Rarity;
import pl.north93.zgame.api.global.messages.TranslatableString;

@ToString
@AllArgsConstructor
public final class Item
{
    private final ItemsGroup           group;
    private final String               id;
    private final int                  maxLevel;
    private final Rarity               rarity;
    private final IntObjectMap<IPrice> price;
    private final Map<Locale, String>  name;
    private final Map<String, String>  data;

    public ItemsGroup getGroup()
    {
        return this.group;
    }

    public String getId()
    {
        return this.id;
    }

    public int getMaxLevel()
    {
        return this.maxLevel;
    }

    public Rarity getRarity()
    {
        return this.rarity;
    }

    public IntObjectMap<IPrice> getPrices()
    {
        return this.price;
    }

    /**
     * Zwraca cene dla okreslonego przedmiotu.
     * Jesli nie jest ona zdefiniowana zostanie zwrócona wartosc dla poziomu 1.
     *
     * @param level Poziom dla którego pobieramy cene.
     * @return Cena przedmiotu dla danego poziomu.
     */
    public IPrice getPrice(final int level)
    {
        Preconditions.checkState(level > 0);
        Preconditions.checkState(this.maxLevel >= level);

        return this.price.getOrDefault(level, this.price.get(1));
    }

    /**
     * Zwraca nazwe tego przedmiotu jako {@link TranslatableString}.
     * Zestaw tlumaczen pobierany jest z configu.
     *
     * @return Nazwa przedmiotu jako tlumaczalny lancuch tekstowy.
     */
    public TranslatableString getName()
    {
        return TranslatableString.custom(this.name);
    }

    public String getName(final Locale locale)
    {
        return this.name.getOrDefault(locale, this.id);
    }

    public Map<String, String> getData()
    {
        return this.data;
    }
}
