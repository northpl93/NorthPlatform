package pl.arieals.minigame.goldhunter.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.classes.CharacterClass;
import pl.arieals.minigame.goldhunter.classes.CharacterClassManager;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.gui.ClickHandler;
import pl.north93.zgame.api.bukkit.gui.event.GuiClickEvent;
import pl.north93.zgame.api.bukkit.utils.itemstack.ItemStackBuilder;
import pl.north93.zgame.api.bukkit.utils.xml.XmlEnchant;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.uri.UriHandler;

public class SelectClassGui extends GoldHunterGui
{
    @Inject
    private static CharacterClassManager classManager;
    
    public SelectClassGui(GoldHunterPlayer player)
    {
        super(player, "gh/select_class");
    }
    
    @ClickHandler
    public void select(GuiClickEvent event)
    {
        String className = event.getClickedElement().getMetadata().get("class");
        CharacterClass characterClass = classManager.getByName(className);
        
        if ( characterClass.hasEnoughRank(player) && characterClass.hasBought(player) )
        {
            player.selectClass(characterClass);
            closeAll();
        }
    }
    
}

class ClassIconRenderer
{
    private final GoldHunter goldHunter;
    
    private final CharacterClassManager classManager;
    
    private final MessagesBox messages;
    
    public ClassIconRenderer(GoldHunter goldHunter, CharacterClassManager classManager, @Messages("gh_gui") MessagesBox messages)
    {
        this.goldHunter = goldHunter;
        this.classManager = classManager;
        this.messages = messages;
    }
    
    @UriHandler("/gh/gui/selectClass/renderClassIcon/:class/:playerId")
    public ItemStack renderClassIcon(String calledUri, Map<String, String> parameters)
    {
        GoldHunterPlayer player = goldHunter.getPlayer(UUID.fromString(parameters.get("playerId")));
        CharacterClass characterClass = classManager.getByName(parameters.get("class"));
        
        boolean canSelect;
        String infoText;
        
        if ( !characterClass.hasEnoughRank(player) )
        {
            canSelect = false;
            infoText = messages.getMessage(player.getPlayer().getLocale(), "select_class.require_rank." + characterClass.getRank().name().toLowerCase());
        }
        else if ( !characterClass.hasBought(player) )
        {
            canSelect = false;
            infoText = messages.getMessage(player.getPlayer().getLocale(), "select_class.must_buy_first");
        }
        else
        {
            canSelect = true;
            infoText = messages.getMessage(player.getPlayer().getLocale(), "select_class.click_to_select");
        }
         
        // String displayName = ( canSelect ? "§a§l" : "§c§l" ) + characterClass.getDisplayName().getValue(player.getPlayer()).toLegacyText();
        
        BaseComponent displayName = characterClass.getDisplayName().getValue(player.getPlayer());
        displayName.setBold(true);
        displayName.setColor(canSelect ? ChatColor.GREEN : ChatColor.RED);
                
        ArrayList<String> lore = new ArrayList<>(Arrays.asList(characterClass.getLore().getValue(player.getPlayer()).toLegacyText().split("\n")));
        lore.add("");
        lore.add(infoText);
        
        ItemStackBuilder builder = ItemStackBuilder.wrap(characterClass.getIcon().createItemStack())
                .name(displayName.toLegacyText())
                .lore(lore)
                .flags(ItemFlag.values());
        
        if ( player.getSelectedClass() == characterClass )
        {
            builder.enchant(new XmlEnchant(Enchantment.ARROW_DAMAGE));
        }
        
        return builder.build();
    }
}
