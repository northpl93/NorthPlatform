package pl.arieals.globalshops.server.impl.price;

import pl.arieals.globalshops.server.IPlayerContainer;
import pl.arieals.globalshops.server.domain.IPrice;
import pl.arieals.globalshops.server.domain.Item;

public final class NullPrice implements IPrice
{
    public static final NullPrice INSTANCE = new NullPrice();

    @Override
    public boolean canBuy(final IPlayerContainer container, final Item item)
    {
        return true;
    }

    @Override
    public boolean processBuy(final IPlayerContainer container, final Item item)
    {
        return true;
    }
}