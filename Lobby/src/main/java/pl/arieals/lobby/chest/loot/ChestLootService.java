package pl.arieals.lobby.chest.loot;

import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.server.IGlobalShops;
import pl.arieals.lobby.chest.ChestService;
import pl.arieals.lobby.chest.ChestType;
import pl.north93.zgame.api.bukkit.server.IBukkitExecutor;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

/**
 * Usluga generujaca i zarzadzajaca lootem ze skrzynek.
 */
public class ChestLootService
{
    @Inject
    private IBukkitExecutor executor;
    @Inject
    private IGlobalShops    globalShops;
    @Inject
    private ChestService    chestService;

    @Bean
    private ChestLootService()
    {
    }

    public CompletableFuture<LootResult> openChest(final Player player, final ChestType type)
    {
        final boolean successfullyTakenChest = this.chestService.takeChest(player, type);
        if (! successfullyTakenChest)
        {
            return null;
        }

        final CompletableFuture<LootResult> generatedLoot = this.generateLoot(type);
        generatedLoot.whenComplete((result, throwable) -> result.applyTo(player));

        return generatedLoot;
    }

    public CompletableFuture<LootResult> generateLoot(final ChestType type)
    {
        final CompletableFuture<LootResult> future = new CompletableFuture<>();
        this.executor.async(new LootGenerateTask(future, type));
        return future;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
