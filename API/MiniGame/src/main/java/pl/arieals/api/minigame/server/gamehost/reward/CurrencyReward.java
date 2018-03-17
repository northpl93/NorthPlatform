package pl.arieals.api.minigame.server.gamehost.reward;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.IEconomyManager;
import pl.north93.zgame.api.economy.ITransaction;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;

public class CurrencyReward implements IReward
{
    @Inject
    private static IEconomyManager manager;
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
    public void apply(final Identity identity)
    {
        final ICurrency currency = manager.getCurrency(this.currencyId);
        try (final ITransaction t = manager.openTransaction(currency, identity))
        {
            t.add(this.amount);
        }
        catch (final Exception e)
        {
            logger.log(Level.SEVERE, "CurrencyReward can't add money to user's account.", e);
        }
    }

    @Override
    public RewardMessageRenderer getRenderer()
    {
        return (messagesBox, locale, allRewardsOfType) ->
        {
            final double totalAmount = allRewardsOfType.stream().map(reward -> ((CurrencyReward) reward)).mapToDouble(CurrencyReward::getAmount).sum();
            final String msgKey = "rewards." + this.rewardId;

            return messagesBox.getMessage(locale, msgKey, totalAmount);
        };
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("currencyId", this.currencyId).append("amount", this.amount).toString();
    }
}
