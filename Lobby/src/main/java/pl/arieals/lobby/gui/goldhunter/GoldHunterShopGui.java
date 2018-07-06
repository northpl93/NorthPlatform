package pl.arieals.lobby.gui.goldhunter;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Preconditions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.tuple.Pair;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.arieals.globalshops.server.IGlobalShops;
import pl.arieals.globalshops.server.IPlayerContainer;
import pl.arieals.globalshops.server.IPlayerExperienceService;
import pl.arieals.globalshops.server.domain.BuyResult;
import pl.arieals.globalshops.server.domain.Item;
import pl.arieals.globalshops.server.domain.ItemsGroup;
import pl.arieals.lobby.gui.ShopGui;
import pl.north93.zgame.api.bukkit.gui.ClickHandler;
import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.bukkit.gui.element.ButtonElement;
import pl.north93.zgame.api.bukkit.gui.element.GuiElement;
import pl.north93.zgame.api.bukkit.gui.event.GuiClickEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;
import pl.north93.zgame.api.global.utils.Vars;

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
		for ( ButtonElement element : getContent().getChildrenByClassDeep(ButtonElement.class) )
		{
			if ( isBuyButton(element) )
			{
				addShopItemVariablesToGuiElement(element);
			}
			else if ( isFolder(element) )
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
		return !isBuyButton(element) && element.getMetadata().containsKey("folder");
	}
	
	private void addShopItemVariablesToGuiElement(GuiElement element)
	{
		Pair<Item, Integer> item = getShopItemByGuiElement(element);
		if ( item == null )
		{
			return;
		}
		
		ItemState itemState = ItemState.getItemState(playerContainer, item);
		BaseComponent price = ShopGui.getPrice(playerContainer, item.getLeft(), item.getRight());
		
		element.addVariables(Vars.<Object>of("color", itemState.getMainColor())
				.and("status", itemState.getBuyText())
				.and("price", TranslatableString.of(getMessagesBox(), "@itemstate.price$priceValue"))
                .and("priceValue", price));
	}
	
	private void addFolderItemVariables(GuiElement element)
	{
		int totalCount = 0;
		int boughtCount = 0;
		
		for ( Entry<String, String> entry : element.getMetadata().entrySet() )
		{
			if ( !entry.getKey().startsWith("item") )
			{
				continue;
			}
			
			Pair<Item, Integer> item = getShopItemFromString(entry.getValue());
			
			if ( playerContainer.hasBoughtItemAtLevel(item.getLeft(), item.getRight()) )
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
		if ( bought == 0 )
		{
			return ChatColor.RED;
		}
		if ( bought == total )
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
		
		if ( itemId == null )
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
			
			if ( playerContainer.hasBoughtItemAtLevel(item.getLeft(), item.getRight()) )
			{
				return ItemState.BOUGHT;
			}
			if ( buyResult == BuyResult.NO_MONEY )
			{
				return ItemState.NO_MONEY;
			}
			if ( buyResult != BuyResult.CAN_BUY )
			{
				return ItemState.CANNOT_BUY;
			}
			
			return ItemState.CAN_BUY;
		}
	}

	public static void main2(String[] args)
	{
		String dupa = "archer.poison.enchant\n" +
							  "archer.poison.time\n" +
							  "archer.poison.chainchest\n" +
							  "archer.poison.budulec\n" +
							  "class.archer.bomb\n" +
							  "archer.bomb.enchant\n" +
							  "archer.bomb.time\n" +
							  "archer.bomb.chainchest\n" +
							  "archer.bomb.budulec\n" +
							  "class.warrior.knight\n" +
							  "warrior.knight.time\n" +
							  "warrior.knight.ironchest\n" +
							  "warrior.knight.budulec\n" +
							  "warrior.berserker.ironsword\n" +
							  "warrior.berserker.time\n" +
							  "warrior.berserker.ironchest\n" +
							  "warrior.berserker.budulec\n" +
							  "scout.slinger.time\n" +
							  "scout.slinger.time2\n" +
							  "scout.slinger.ironboots\n" +
							  "scout.slinger.budulec\n" +
							  "class.scout.sprinter\n" +
							  "scout.sprinter.time\n" +
							  "scout.sprinter.ironboots\n" +
							  "scout.sprinter.budulec\n" +
							  "medic.battle.time\n" +
							  "medic.battle.budulec\n" +
							  "class.medic.healer\n" +
							  "medic.healer.time\n" +
							  "medic.healer.potion\n" +
							  "medic.healer.budulec\n" +
							  "engineer.architect.time\n" +
							  "engineer.architect.chainchest\n" +
							  "engineer.architect.budulec\n" +
							  "class.engineer.dispenser\n" +
							  "engineer.dispenser.time\n" +
							  "engineer.dispenser.time2\n" +
							  "engineer.dispenser.chainchest\n" +
							  "engineer.dispenser.budulec\n" +
							  "class.vip.defender\n" +
							  "vip.defender.time\n" +
							  "vip.defender.ability\n" +
							  "vip.defender.diamondchest\n" +
							  "vip.defender.budulec\n" +
							  "vip.assasyn.time\n" +
							  "vip.assasyn.time2\n" +
							  "vip.assasyn.chainchest\n" +
							  "vip.assasyn.ironboots\n" +
							  "vip.assasyn.budulec\n" +
							  "svip.spy.time\n" +
							  "svip.spy.time2\n" +
							  "svip.spy.ironboots\n" +
							  "svip.spy.budulec\n" +
							  "svip.paladin.time\n" +
							  "svip.paladin.diamondleggins\n" +
							  "svip.paladin.budulec\n" +
							  "class.svip.paladin";

		final String[] classess = dupa.split("\n");

//        final StringBuilder builder = new StringBuilder("$unset: { ");
//        for (final String s : classess)
//        {
//            builder.append("\"metadata.globalShops_bought.GoldHunterShop$").append(s).append("\": 1, ");
//        }
//        builder.append(" }");

		final StringBuilder builder = new StringBuilder("db.getCollection('players').updateMany({}, { ");
		for (final String s : classess)
		{
			builder.append("$unset: { 'metadata.globalShops_bought.GoldHunterShop$").append(s).append("': 1 }, ");
		}
		builder.append(" })");

		System.out.println(builder);
	}

	public static void main(String[] args)
	{
		System.out.println(Arrays.toString("cxcxcXc:3".split(":")));
	}
}



