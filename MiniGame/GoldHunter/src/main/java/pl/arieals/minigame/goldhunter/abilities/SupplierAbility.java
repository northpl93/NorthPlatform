package pl.arieals.minigame.goldhunter.abilities;

import org.bukkit.Location;

import pl.arieals.minigame.goldhunter.AbilityHandler;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.structure.SupplierStructure;

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
        
        int time = 60 * 20 + player.getShopItemLevel("engineer.dispenser.time2") * 15 * 20;
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
