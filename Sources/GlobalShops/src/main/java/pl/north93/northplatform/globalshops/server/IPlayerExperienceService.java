package pl.north93.northplatform.globalshops.server;

import pl.north93.northplatform.globalshops.server.domain.BuyResult;
import pl.north93.northplatform.globalshops.server.domain.Item;

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
    
    /**
     * Obsluguje klikniecie przedmiotu w gui na danym poziomie.
     * 
     * @param playerContainer
     * @param item
     * @param level
     */
    void processClick(IPlayerContainer playerContainer, Item item, int level);
}
