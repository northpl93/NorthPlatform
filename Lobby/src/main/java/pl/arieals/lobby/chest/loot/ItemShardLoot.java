package pl.arieals.lobby.chest.loot;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.math.DioriteRandomUtils;
import org.diorite.utils.math.IWeightedRandomChoice;

import pl.arieals.globalshops.server.IGlobalShops;
import pl.arieals.globalshops.server.IPlayerContainer;
import pl.arieals.globalshops.shared.Item;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.TranslatableString;

/**
 * Przedstawia loot w postaci odlamkow danego przedmiotu.
 */
public class ItemShardLoot implements ILoot
{
    @Inject
    private IGlobalShops globalShops;

    private final Item item;
    private final int  shards;

    public ItemShardLoot(final Item item, final int shards)
    {
        this.item = item;
        this.shards = shards;
    }

    public static ItemShardLoot create(final Random random, final Item item)
    {
        return new ItemShardLoot(item, WeightedShardAmount.getRandomShardAmount(random));
    }

    @Override
    public TranslatableString getName()
    {
        return this.item.getName();
    }

    /**
     * Zwraca przedmiot ktorego odlamki zawiera ten loot.
     *
     * @return Przedmiot ktorego odlamki zawieramy.
     */
    public Item getItem()
    {
        return this.item;
    }

    /**
     * Zwraca ilosc odlamkow danego przedmiotu ktora zawiera ten loot.
     *
     * @return Ilosc odlamkow przedmiotu.
     */
    public int getShards()
    {
        return this.shards;
    }

    @Override
    public void apply(final Player player)
    {
        final IPlayerContainer container = this.globalShops.getPlayer(player);
        if (container.hasMaxLevel(this.item))
        {
            // gracz ma maksymalny level, odlamki przepadaja
            return;
        }

        container.addShards(this.item, this.shards);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("item", this.item).append("shards", this.shards).toString();
    }

    public enum WeightedShardAmount implements IWeightedRandomChoice
    {
        FIRST(18, 9, 19),
        SECOND(10, 20, 30),
        THIRD(3, 31, 40),
        FOURTH(1, 41, 50);

        private final int weight;
        private final int from;
        private final int to;

        WeightedShardAmount(final int weight, final int from, final int to)
        {
            this.weight = weight;
            this.from = from;
            this.to = to;
        }

        public int getRandomInRange(final Random random) // losuje w danym zakresie
        {
            return DioriteRandomUtils.getRandomInt(random, this.from, this.to);
        }

        public static WeightedShardAmount pickRandomRange(final Random random) // wybiera losowy zakres
        {
            final List<WeightedShardAmount> values = Arrays.asList(WeightedShardAmount.values());
            return DioriteRandomUtils.getWeightedRandom(random, values);
        }

        public static int getRandomShardAmount(final Random random) // wybiera zakres i losuje w danym zakresie
        {
            return pickRandomRange(random).getRandomInRange(random);
        }

        @Override
        public int getWeight()
        {
            return this.weight;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("weight", this.weight).append("from", this.from).append("to", this.to).toString();
        }
    }
}
