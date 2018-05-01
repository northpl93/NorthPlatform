package pl.north93.zgame.itemshop.controller.handlers;

import java.util.Map;

import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.ITransaction;
import pl.north93.zgame.api.economy.impl.netcontroller.EconomyControllerComponent;
import pl.north93.zgame.api.economy.impl.shared.EconomyManagerImpl;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.itemshop.shared.IDataHandler;

public class MoneyDataHandler implements IDataHandler
{
    @Inject
    private EconomyControllerComponent economyController;

    @Override
    public String getId()
    {
        return "money";
    }

    @Override
    public boolean process(final Identity player, final Map<String, String> data)
    {
        final String currency = data.get("currency");
        final double amount = Double.parseDouble(data.get("amount"));

        final EconomyManagerImpl economyManager = this.economyController.getEconomyManager();
        final ICurrency currencyObj = economyManager.getCurrency(currency);
        try (final ITransaction transaction = economyManager.openTransaction(currencyObj, player))
        {
            transaction.add(amount);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
