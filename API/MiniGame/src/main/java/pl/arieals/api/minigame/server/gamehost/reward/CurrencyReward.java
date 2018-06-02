package pl.arieals.api.minigame.server.gamehost.reward;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.booster.IBoosterManager;
import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.IEconomyManager;
import pl.north93.zgame.api.economy.ITransaction;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.Identity;

public class CurrencyReward implements IReward
{
    @Inject
    private static IEconomyManager economyManager;
    @Inject
    private static IBoosterManager boosterManager;
    @Inject
    private static Logger          logger;
    private final String rewardId;
    private final String currencyId;
    private final double amount;

    public CurrencyReward(final String rewardId, final String currencyId, final double amount)
    {
        this.rewardId = rewardId;
        this.currencyId = currencyId;
        this.amount = amount;
    }

    @Override
    public String getId()
    {
        return this.rewardId;
    }

    public String getCurrencyId()
    {
        return this.currencyId;
    }

    public double getAmount()
    {
        return this.amount;
    }

    @Override
    public IReward apply(final Identity identity)
    {
        final ICurrency currency = economyManager.getCurrency(this.currencyId);
        try (final ITransaction t = economyManager.openTransaction(currency, identity))
        {
            final double amount = this.calculateFinalAmount(t.getAssociatedPlayer());
            t.add(this.amount);

            return new CurrencyReward(this.rewardId, this.currencyId, amount);
        }
        catch (final Exception e)
        {
            logger.log(Level.SEVERE, "CurrencyReward can't add money to user's account.", e);
            return this;
        }
    }

    private double calculateFinalAmount(final IPlayer player)
    {
        return this.amount * boosterManager.calculateFinalMultiplier(player);
    }

    @Override
    public RewardMessageRenderer getRenderer()
    {
        return (messagesBox, locale, allRewardsOfType) ->
        {
            final Function<IReward, CurrencyReward> mapper = reward -> ((CurrencyReward) reward);
            final int totalAmount = (int) allRewardsOfType.stream().map(mapper).mapToDouble(CurrencyReward::getAmount).sum();
            final String msgKey = "rewards." + this.rewardId;

            return MessageLayout.CENTER.processMessage(messagesBox.getMessage(locale, msgKey, totalAmount));
        };
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("currencyId", this.currencyId).append("amount", this.amount).toString();
    }
}
