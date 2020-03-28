package pl.north93.northplatform.minigame.goldhunter.abilities;

import org.bukkit.Location;

import pl.north93.northplatform.minigame.goldhunter.arena.structure.SupplierStructure;
import pl.north93.northplatform.minigame.goldhunter.player.AbilityHandler;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;

public class SupplierAbility implements AbilityHandler
{
    @Override
    public boolean onUse(GoldHunterPlayer player, Location targetBlock)
    {
        if ( targetBlock == null )
        {
            player.sendMessage("must_look_at_block");
            return false;
        }
        
        int time = 40 * 20 + player.getShopItemLevel("supplier.abilityduration") * 15 * 20;
        SupplierStructure supplier = new SupplierStructure(targetBlock.add(0, 1, 0).toVector().toBlockVector(), player, time);
        
        if ( !player.getArena().getStructureManager().spawn(supplier) )
        {
            player.sendMessage("cannot_build_supplier_here");
            return false;
        }
        
        player.getAbilityTracker().suspendAbilityLoading();
        return true;
    }
}
