package pl.arieals.globalshops.server.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import pl.arieals.globalshops.server.BuyResult;
import pl.arieals.globalshops.server.IPlayerContainer;
import pl.arieals.globalshops.server.IPlayerExperienceService;
import pl.arieals.globalshops.server.event.ItemBuyEvent;
import pl.arieals.globalshops.server.event.ItemCheckBuyEvent;
import pl.arieals.globalshops.shared.GroupType;
import pl.arieals.globalshops.shared.Item;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

class PlayerExperienceServiceImpl implements IPlayerExperienceService
{
    @Inject
    private BukkitApiCore apiCore;
    @Inject
    private Logger logger;

    @Bean
    private PlayerExperienceServiceImpl()
    {
    }

    @Override
    public BuyResult checkCanBuy(final IPlayerContainer playerContainer, final Item item, final int level)
    {
        final ItemCheckBuyEvent event = new ItemCheckBuyEvent(playerContainer.getBukkitPlayer(), playerContainer, item, level);

        if (playerContainer.hasMaxLevel(item))
        {
            event.setResult(BuyResult.MAX_LEVEL);
        }

        // todo money check

        return this.apiCore.callEvent(event).getResult();
    }

    @Override
    public void processClick(final IPlayerContainer playerContainer, final Item item)
    {
        if (item.getGroup().getGroupType() == GroupType.SINGLE_PICK)
        {
            if (playerContainer.hasBoughtItem(item))
            {
                this.markActive(playerContainer, item);
            }
            else
            {
                this.buyItem(playerContainer, item);
            }
        }
        else
        {
            this.upgradeItem(playerContainer, item);
        }
    }

    private void markActive(final IPlayerContainer playerContainer, final Item item)
    {
        playerContainer.markAsActive(item);

        final String playerName = playerContainer.getBukkitPlayer().getName();
        this.logger.log(Level.INFO, "Player {0} marked {1} as active in group {2}", new Object[]{playerName, item.getId(), item.getGroup().getId()});
    }

    private void buyItem(final IPlayerContainer playerContainer, final Item item)
    {
        final Player bukkitPlayer = playerContainer.getBukkitPlayer();
        final BuyResult buyResult = this.checkCanBuy(playerContainer, item, 1);

        if (buyResult != BuyResult.CAN_BUY)
        {
            this.logger.log(Level.INFO, "Buy cancelled for player {0} item {1}", new Object[]{bukkitPlayer.getName(), item.getId()});
            return;
        }

        final ItemBuyEvent buyEvent = this.apiCore.callEvent(new ItemBuyEvent(bukkitPlayer, item, 1));
        if (buyEvent.isCancelled())
        {
            return;
        }

        final boolean success = playerContainer.addItem(item);
        if (! success)
        {
            this.logger.log(Level.WARNING, "Player {0} failed to buy {1}", new Object[]{bukkitPlayer.getName(), item.getId()});
            return;
        }

        // todo bierzemy kase
        this.logger.log(Level.INFO, "Player {0} bought {1}", new Object[]{bukkitPlayer.getName(), item.getId()});

        // oznaczamy dany item jako aktywny
        this.markActive(playerContainer, item);
    }

    private void upgradeItem(final IPlayerContainer playerContainer, final Item item)
    {
        final int nextLevel = playerContainer.getBoughtItemLevel(item) + 1;
        final Player bukkitPlayer = playerContainer.getBukkitPlayer();

        final BuyResult buyResult = this.checkCanBuy(playerContainer, item, nextLevel);
        if (buyResult != BuyResult.CAN_BUY)
        {
            this.logger.log(Level.INFO, "Upgrade cancelled with reason {0} for player {1} item {2} level {3}", new Object[]{buyResult, bukkitPlayer.getName(), item.getId(), nextLevel});
            return;
        }

        final ItemBuyEvent buyEvent = this.apiCore.callEvent(new ItemBuyEvent(bukkitPlayer, item, nextLevel));
        if (buyEvent.isCancelled())
        {
            return;
        }

        final boolean success = playerContainer.addItem(item, nextLevel);
        if (! success)
        {
            this.logger.log(Level.WARNING, "Player {0} failed to buy {1} level {2} (success false)", new Object[]{bukkitPlayer.getName(), item.getId(), nextLevel});
            return;
        }

        // todo bierzemy kase
        this.logger.log(Level.INFO, "Player {0} bought {1} level {2}", new Object[]{bukkitPlayer.getName(), item.getId(), nextLevel});
    }
}
