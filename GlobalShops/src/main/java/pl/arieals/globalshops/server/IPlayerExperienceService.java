package pl.arieals.globalshops.server;

import pl.arieals.globalshops.shared.Item;

public interface IPlayerExperienceService
{
    /**
     * Sprawdza czy gracz moze kupic/ulepszyc.
     * @param playerContainer
     * @param item
     * @return
     */
    BuyResult checkCanBuy(IPlayerContainer playerContainer, Item item, int level);

    /**
     * Obsluguje klikniecie przedmiotu w gui.
     * @param playerContainer
     * @param item
     */
    void processClick(IPlayerContainer playerContainer, Item item);
}
