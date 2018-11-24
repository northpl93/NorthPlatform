package pl.north93.northplatform.api.economy.impl.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import pl.north93.northplatform.api.economy.ICurrency;
import pl.north93.northplatform.api.economy.IEconomyManager;
import pl.north93.northplatform.api.economy.ITransaction;

public class NorthPlatformEconomy extends AbstractEconomy
{
    private final IEconomyManager economyManager;
    private final ICurrency       currency;

    public NorthPlatformEconomy(final IEconomyManager economyManager, final ICurrency currency)
    {
        this.economyManager = economyManager;
        this.currency = currency;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "NorthPlatform-" + this.currency.getName();
    }

    @Override
    public boolean hasBankSupport()
    {
        return false;
    }

    @Override
    public int fractionalDigits()
    {
        return -1;
    }

    @Override
    public String format(final double v)
    {
        return String.valueOf(v);
    }

    @Override
    public String currencyNamePlural()
    {
        return "";
    }

    @Override
    public String currencyNameSingular()
    {
        return "";
    }

    @Override
    public boolean hasAccount(final String player)
    {
        return true;
    }

    @Override
    public boolean hasAccount(final String s, final String s1)
    {
        return true;
    }

    @Override
    public double getBalance(final String s)
    {
        try (final ITransaction transaction = this.economyManager.openTransaction(this.currency, s))
        {
            return transaction.getAmount();
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getBalance(final String s, final String s1)
    {
        return this.getBalance(s);
    }

    @Override
    public boolean has(final String s, final double v)
    {
        try (final ITransaction transaction = this.economyManager.openTransaction(this.currency, s))
        {
            return transaction.has(v);
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean has(final String s, final String s1, final double v)
    {
        return this.has(s, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(final String player, final double amount)
    {
        if (amount < 0)
        {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
        }

        try (final ITransaction transaction = this.economyManager.openTransaction(this.currency, player))
        {
            final double currentBalance = transaction.getAmount();

            if (! transaction.has(amount))
            {
                return new EconomyResponse(0, currentBalance, ResponseType.FAILURE, "Insufficient funds");
            }

            final double newBalance = currentBalance - amount;
            transaction.setAmount(newBalance);

            return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(final String s, final String s1, final double v)
    {
        return this.withdrawPlayer(s, v);
    }

    @Override
    public EconomyResponse depositPlayer(final String player, final double amount)
    {
        if (amount < 0)
        {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot deposit negative funds");
        }

        try (final ITransaction transaction = this.economyManager.openTransaction(this.currency, player))
        {
            final double newBalance = transaction.getAmount() + amount;
            transaction.setAmount(newBalance);

            return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EconomyResponse depositPlayer(final String s, final String s1, final double v)
    {
        return this.depositPlayer(s, v);
    }

    @Override
    public EconomyResponse createBank(final String s, final String s1)
    {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "NorthPlatformEconomy: createBank unimplemented");
    }

    @Override
    public EconomyResponse deleteBank(final String s)
    {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "NorthPlatformEconomy: deleteBank unimplemented");
    }

    @Override
    public EconomyResponse bankBalance(final String s)
    {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "NorthPlatformEconomy: bankBalance unimplemented");
    }

    @Override
    public EconomyResponse bankHas(final String s, final double v)
    {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "NorthPlatformEconomy: bankHas unimplemented");
    }

    @Override
    public EconomyResponse bankWithdraw(final String s, final double v)
    {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "NorthPlatformEconomy: bankWithdraw unimplemented");
    }

    @Override
    public EconomyResponse bankDeposit(final String s, final double v)
    {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "NorthPlatformEconomy: bankDeposit unimplemented");
    }

    @Override
    public EconomyResponse isBankOwner(final String s, final String s1)
    {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "NorthPlatformEconomy: isBankOwner unimplemented");
    }

    @Override
    public EconomyResponse isBankMember(final String s, final String s1)
    {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "NorthPlatformEconomy: isBankMember unimplemented");
    }

    @Override
    public List<String> getBanks()
    {
        return new ArrayList<>();
    }

    @Override
    public boolean createPlayerAccount(final String s)
    {
        return false;
    }

    @Override
    public boolean createPlayerAccount(final String s, final String s1)
    {
        return false;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("economyManager", this.economyManager).append("currency", this.currency).toString();
    }
}
