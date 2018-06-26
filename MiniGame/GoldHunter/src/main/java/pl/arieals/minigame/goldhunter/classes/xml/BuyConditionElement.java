package pl.arieals.minigame.goldhunter.classes.xml;

import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;

public interface BuyConditionElement
{
    String getCondition();
    
    default boolean check(GoldHunterPlayer player)
    {
        String condition = getCondition();
        
        if ( condition == null )
        {
            return true;
        }
        
        String[] split = condition.split(":");
        String shopItemName = split[0];
        
        int shopItemLevel = 1;
        if ( split.length > 1 ) 
        {
            Integer.parseInt(split[1]);
        }
        
        return player.hasBuyed(shopItemName, shopItemLevel);
    }
}
