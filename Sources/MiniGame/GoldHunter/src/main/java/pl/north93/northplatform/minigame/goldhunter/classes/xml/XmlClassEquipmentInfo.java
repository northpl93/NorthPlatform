package pl.north93.northplatform.minigame.goldhunter.classes.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;

@XmlAccessorType(XmlAccessType.FIELD)
public class XmlClassEquipmentInfo
{
    private XmlClassEquipmentSlot chestplate = new XmlClassEquipmentSlot();
    
    private XmlClassEquipmentSlot leggings = new XmlClassEquipmentSlot();
    
    private XmlClassEquipmentSlot boots = new XmlClassEquipmentSlot();
    
    @XmlElement(name = "inv")
    private List<XmlClassEquipmentInvSlot> inventory = new ArrayList<>();
    
    public XmlClassEquipmentSlot getChestplate()
    {
        return chestplate;
    }
    
    public XmlClassEquipmentSlot getLeggins()
    {
        return leggings;
    }
    
    public XmlClassEquipmentSlot getBoots()
    {
        return boots;
    }
    
    public List<XmlClassEquipmentInvSlot> getInventory()
    {
        return inventory;
    }
    
    public void applyToPlayer(GoldHunterPlayer player)
    {
        PlayerInventory inv = player.getPlayer().getInventory();
        
        inv.setContents(getInventoryContents(player));
        
        inv.setChestplate(chestplate.getItemStack(player));
        inv.setLeggings(leggings.getItemStack(player));
        inv.setBoots(boots.getItemStack(player));
    }
    
    private ItemStack[] getInventoryContents(GoldHunterPlayer player)
    {
        ItemStack[] result = new ItemStack[36];
        
        for ( XmlClassEquipmentInvSlot slot : getInventory() )
        {
            result[slot.getSlot()] = slot.getItemStack(player);
        }
        
        return result;
    }
}
