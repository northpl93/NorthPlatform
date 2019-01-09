package pl.north93.northplatform.globalshops.server.domain;

import pl.north93.northplatform.globalshops.server.IPlayerContainer;

public interface IPrice
{
    boolean canBuy(IPlayerContainer container, Item item);

    boolean processBuy(IPlayerContainer container, Item item);
}
