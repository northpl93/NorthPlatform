package pl.arieals.minigame.goldhunter.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.PacketPlayOutSetSlot;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.gui.JoinTeamGui;
import pl.arieals.minigame.goldhunter.gui.SelectClassGui;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northspigot.event.PlayerPressQEvent;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class KeyShortcutListener implements AutoListener
{
    private final GoldHunter goldHunter;
    
    public KeyShortcutListener(GoldHunter goldHunter)
    {
        this.goldHunter = goldHunter;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPressQ(PlayerPressQEvent event)
    {
        event.setCancelled(true);
        
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        if ( player.isIngame() )
        {
            player.getAbilityTracker().useAbility();
        }
        else
        {
            new JoinTeamGui(player).open();
        }
        
        // due to different 1.13.1 behavior we have to update clientside held item slot 
        refreshSlot(event.getPlayer());
    }
    
    private void refreshSlot(Player player)
    {
        EntityPlayer entityPlayer = INorthPlayer.asCraftPlayer(player).getHandle();
        
        int handSlot = entityPlayer.inventory.itemInHandIndex;
        ItemStack itemInHand = entityPlayer.inventory.getItem(handSlot);
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutSetSlot(0, handSlot, itemInHand));
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPressF(PlayerSwapHandItemsEvent event)
    {
        event.setCancelled(true);
        
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        new SelectClassGui(player).open();
    }
}
