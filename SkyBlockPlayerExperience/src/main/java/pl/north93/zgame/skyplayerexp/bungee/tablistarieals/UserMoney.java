package pl.north93.zgame.skyplayerexp.bungee.tablistarieals;

import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.ITransaction;
import pl.north93.zgame.api.economy.impl.client.EconomyComponent;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyplayerexp.bungee.tablist.ICellProvider;
import pl.north93.zgame.skyplayerexp.bungee.tablist.TablistDrawingContext;

public class UserMoney implements ICellProvider
{
    @InjectComponent("API.Economy")
    private EconomyComponent economy;

    @Override
    public String process(final TablistDrawingContext ctx)
    {
        final ICurrency skyblock = this.economy.getEconomyManager().getCurrency("skyblock");
        try (final ITransaction transaction = this.economy.getEconomyManager().openTransaction(skyblock, ctx.getPlayer().getName()))
        {
            return "&6pieniadze: &r" + (int)transaction.getAmount();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            return "&cerror";
        }
    }
}
