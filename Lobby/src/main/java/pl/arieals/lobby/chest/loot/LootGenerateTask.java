package pl.arieals.lobby.chest.loot;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.math.DioriteRandomUtils;
import org.diorite.utils.math.IWeightedRandomChoice;

import pl.arieals.globalshops.server.IGlobalShops;
import pl.arieals.globalshops.shared.Item;
import pl.arieals.lobby.chest.ChestType;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

class LootGenerateTask implements Runnable
{
    @Inject
    private IGlobalShops globalShops;

    private static final Random RANDOM = new SecureRandom();
    private final CompletableFuture<LootResult> results;
    private final ChestType                     chestType;

    LootGenerateTask(final CompletableFuture<LootResult> results, final ChestType chestType)
    {
        this.results = results;
        this.chestType = chestType;
    }

    @Override
    public void run()
    {
        // pobieramy kolekcje przedmiotow z ktorych prowadzimy losowanie
        final Collection<WeightedItem> possibleItems = this.getPossibleItems();

        final ArrayList<ILoot> loot = new ArrayList<>();
        this.addShardsToLoot(loot, possibleItems, 3);

        this.results.complete(new LootResult(loot));
    }

    public void testShardsGroups()
    {
        final int[] groups = {0, 0, 0, 0, 0, 0};
        for (int i = 0; i < 1000; i++)
        {
            final ItemShardLoot.WeightedShardAmount weightedShardAmount =
                    ItemShardLoot.WeightedShardAmount.pickRandomRange(RANDOM);

            groups[weightedShardAmount.ordinal()] = groups[weightedShardAmount.ordinal()] + 1;
        }

        System.out.println(Arrays.toString(groups));
    }

    public void testRandomWeight(final Collection<WeightedItem> possibleItems)
    {
        final int[] rarities = {0, 0, 0, 0};

        for (int i = 0; i < 1000; i++)
        {
            final WeightedItem weightedItem = DioriteRandomUtils.getWeightedRandom(RANDOM, possibleItems);
            final int ordinal = weightedItem.getItem().getRarity().ordinal();
            rarities[ordinal] = rarities[ordinal] + 1;
        }
    }

    private void addShardsToLoot(final Collection<ILoot> loot, final Collection<WeightedItem> possibleItems, final int amount)
    {
        for (int i = 0; i < amount && ! possibleItems.isEmpty(); i++)
        {
            final WeightedItem weightedItem = DioriteRandomUtils.getWeightedRandom(RANDOM, possibleItems);
            possibleItems.remove(weightedItem);

            loot.add(ItemShardLoot.create(RANDOM, weightedItem.getItem()));
        }
    }

    private Collection<WeightedItem> getPossibleItems()
    {
        final List<WeightedItem> items =
                this.chestType.getCategories().stream()
                              .map(categoryId -> this.globalShops.getGroup(categoryId)) // pobieramy kategorie po id
                              .flatMap(group -> group.getItems().stream()) // wyciagamy listy itemow
                              .map(WeightedItem::new) // opakowujemy w WeightedItem
                              .collect(Collectors.toList());

        // mieszamy liste zeby byla taka sama szansa na wszystkie itemy
        Collections.shuffle(items, DioriteRandomUtils.getRandom());

        return items;
    }
}

class WeightedItem implements IWeightedRandomChoice
{
    private final Item item;

    public WeightedItem(final Item item)
    {
        this.item = item;
    }

    public Item getItem()
    {
        return this.item;
    }

    @Override
    public int getWeight()
    {
        return this.item.getRarity().getWeight();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("item", this.item).toString();
    }
}