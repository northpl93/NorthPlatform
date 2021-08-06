package pl.north93.northplatform.lobby.gui.goldhunter;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Preconditions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.tuple.Pair;

import pl.north93.northplatform.api.bukkit.gui.ClickHandler;
import pl.north93.northplatform.api.bukkit.gui.Gui;
import pl.north93.northplatform.api.bukkit.gui.element.ButtonElement;
import pl.north93.northplatform.api.bukkit.gui.element.GuiElement;
import pl.north93.northplatform.api.bukkit.gui.event.GuiClickEvent;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.LegacyMessage;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.messages.TranslatableString;
import pl.north93.northplatform.api.global.utils.Vars;
import pl.north93.northplatform.globalshops.server.IGlobalShops;
import pl.north93.northplatform.globalshops.server.IPlayerContainer;
import pl.north93.northplatform.globalshops.server.IPlayerExperienceService;
import pl.north93.northplatform.globalshops.server.domain.BuyResult;
import pl.north93.northplatform.globalshops.server.domain.Item;
import pl.north93.northplatform.globalshops.server.domain.ItemsGroup;
import pl.north93.northplatform.lobby.gui.ShopGui;

public class GoldHunterShopGui extends Gui
{
    private static final String GOLD_HUNTER_GROUP_NAME = "GoldHunterShop";

    //@Inject
    //@Messages("GoldHunterShop")
    //private static MessagesBox messages;
    @Inject
    private static IGlobalShops globalShops;
    @Inject
    private static IPlayerExperienceService experienceService;

    private final IPlayerContainer playerContainer;

    private GoldHunterShopGui(IPlayerContainer playerContainer, String guiName)
    {
        super(new MessagesBox(GoldHunterShopGui.class.getClassLoader(), "gui/goldhunter/shop/" + guiName), "goldhunter/shop/" + guiName);
        this.playerContainer = playerContainer;
    }

    @Override
    protected void onRender()
    {
        for (ButtonElement element : getContent().getChildrenByClassDeep(ButtonElement.class))
        {
            if (isBuyButton(element))
            {
                addShopItemVariablesToGuiElement(element);
            }
            else if (isFolder(element))
            {
                addFolderItemVariables(element);
            }
        }
    }

    private boolean isBuyButton(ButtonElement element)
    {
        return element.getClickHandlers().size() == 1 && ! element.getMetadata().containsKey("guiName");
    }

    private boolean isFolder(ButtonElement element)
    {
        return ! isBuyButton(element) && element.getMetadata().containsKey("folder");
    }

    private void addShopItemVariablesToGuiElement(GuiElement element)
    {
        Pair<Item, Integer> item = getShopItemByGuiElement(element);
        if (item == null)
        {
            return;
        }

        ItemState itemState = ItemState.getItemState(playerContainer, item);
        LegacyMessage price = ShopGui.getPrice(playerContainer, item.getLeft(), item.getRight());

        element.addVariables(Vars.<Object>of("color", itemState.getMainColor())
                                 .and("status", itemState.getBuyText())
                                 .and("price", TranslatableString.of(getMessagesBox(), "@itemstate.price$priceValue"))
                                 .and("priceValue", price));
    }

    private void addFolderItemVariables(GuiElement element)
    {
        int totalCount = 0;
        int boughtCount = 0;

        for (Entry<String, String> entry : element.getMetadata().entrySet())
        {
            if (! entry.getKey().startsWith("item"))
            {
                continue;
            }

            Pair<Item, Integer> item = getShopItemFromString(entry.getValue());

            if (playerContainer.hasBoughtItemAtLevel(item.getLeft(), item.getRight()))
            {
                boughtCount++;
            }

            totalCount++;
        }

        element.addVariables(Vars.<Object>of("color", getFolderColor(boughtCount, totalCount) + "")
                                 .and("count", boughtCount)
                                 .and("max", totalCount));
    }

    private ChatColor getFolderColor(int bought, int total)
    {
        if (bought == 0)
        {
            return ChatColor.RED;
        }
        if (bought == total)
        {
            return ChatColor.GREEN;
        }

        return ChatColor.YELLOW;
    }

    private Pair<Item, Integer> getShopItemFromString(String string)
    {
        String[] split = string.split(":");

        String itemName = split[0];
        int itemLevel = split.length > 1 ? Integer.parseInt(split[1]) : 1;

        return Pair.of(globalShops.getItem(getGoldHunterItemsGroup(), itemName), itemLevel);
    }

    private Pair<Item, Integer> getShopItemByGuiElement(GuiElement element)
    {
        Map<String, String> metadata = element.getMetadata();
        String itemId = metadata.get("item");

        if (itemId == null)
        {
            return null;
        }

        //int level = Integer.parseInt(metadata.getOrDefault("level", "1"));

        //return Pair.of(globalShops.getItem(getGoldHunterItemsGroup(), itemId), level);
        return getShopItemFromString(itemId);
    }

    private ItemsGroup getGoldHunterItemsGroup()
    {
        return globalShops.getGroup(GOLD_HUNTER_GROUP_NAME);
    }

    @Override
    protected void onOpen(Player player)
    {
        // Prevent open gui by other player
        Preconditions.checkState(this.playerContainer.getBukkitPlayer().getUniqueId().equals(player.getUniqueId()));
    }

    @ClickHandler
    public void buy(GuiClickEvent event)
    {
        Pair<Item, Integer> item = getShopItemByGuiElement(event.getClickedElement());

        experienceService.processClick(playerContainer, item.getLeft(), item.getRight());
    }

    @ClickHandler
    public void open(GuiClickEvent event)
    {
        String guiName = event.getClickedElement().getMetadata().get("guiName");
        new GoldHunterShopGui(playerContainer, guiName).open(playerContainer.getBukkitPlayer());
    }

    @ClickHandler
    public void exit(GuiClickEvent event)
    {
        closeAll();
    }

    public static GoldHunterShopGui openMainGui(Player player)
    {
        GoldHunterShopGui gui = new GoldHunterShopGui(globalShops.getPlayer(player), "main");
        gui.open(player);
        return gui;
    }

    private enum ItemState
    {
        BOUGHT(ChatColor.GREEN, "@itemstate.bought"),
        CAN_BUY(ChatColor.YELLOW, "@itemstate.canbuy"),
        NO_MONEY(ChatColor.RED, "@itemstate.nomoney"),
        CANNOT_BUY(ChatColor.RED, "@itemstate.cannotbuy"),
        ;

        private final ChatColor mainColor;
        private final TranslatableString buyText;

        private ItemState(ChatColor mainColor, String buyText)
        {
            this.mainColor = mainColor;

            MessagesBox messagesBox = new MessagesBox(ItemState.class.getClassLoader(), "GoldHunterShop");
            this.buyText = TranslatableString.of(messagesBox, buyText);
        }

        public ChatColor getMainColor()
        {
            return mainColor;
        }

        public TranslatableString getBuyText()
        {
            return buyText;
        }

        public static ItemState getItemState(IPlayerContainer playerContainer, Pair<Item, Integer> item)
        {
            BuyResult buyResult = experienceService.checkCanBuy(playerContainer, item.getLeft(), item.getRight());

            if (playerContainer.hasBoughtItemAtLevel(item.getLeft(), item.getRight()))
            {
                return ItemState.BOUGHT;
            }
            if (buyResult == BuyResult.NO_MONEY)
            {
                return ItemState.NO_MONEY;
            }
            if (buyResult != BuyResult.CAN_BUY)
            {
                return ItemState.CANNOT_BUY;
            }

            return ItemState.CAN_BUY;
        }
    }
}



