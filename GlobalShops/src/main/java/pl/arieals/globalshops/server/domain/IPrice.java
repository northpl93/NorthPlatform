package pl.arieals.globalshops.server.domain;

import pl.arieals.globalshops.server.IPlayerContainer;

public interface IPrice
{
    boolean canBuy(IPlayerContainer container, Item item);

    boolean processBuy(IPlayerContainer container, Item item);
}
