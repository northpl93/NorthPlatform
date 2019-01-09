package pl.north93.northplatform.discord.rewards.builtin;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.discord.rewards.IDiscordReward;
import pl.north93.northplatform.api.economy.ICurrency;
import pl.north93.northplatform.api.economy.IEconomyManager;
import pl.north93.northplatform.api.economy.ITransaction;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.Identity;

@Slf4j
@ToString(of = {"currency", "amount"})
public class CurrencyDiscordReward implements IDiscordReward
{
    @Inject
    private IEconomyManager economyManager;

    private final String currency;
    private final double amount;

    public CurrencyDiscordReward(final String currency, final double amount)
    {
        this.currency = currency;
        this.amount = amount;
    }

    @Override
    public void apply(final Identity identity)
    {
        final ICurrency currency = this.economyManager.getCurrency(this.currency);

        try (final ITransaction t = this.economyManager.openTransaction(currency, identity))
        {
            t.add(this.amount);
        }
        catch (final Exception e)
        {
            log.error("Failed to add currency discord reward", e);
        }
    }
}
