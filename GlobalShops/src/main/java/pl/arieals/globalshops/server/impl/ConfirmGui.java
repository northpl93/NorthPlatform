package pl.arieals.globalshops.server.impl;

import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import pl.north93.zgame.api.bukkit.gui.ClickHandler;
import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.bukkit.gui.GuiClickEvent;
import pl.north93.zgame.api.bukkit.gui.IGuiManager;
import pl.north93.zgame.api.bukkit.utils.ISyncCallback;
import pl.north93.zgame.api.bukkit.utils.SimpleSyncCallback;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class ConfirmGui extends Gui
{
	@Inject
	@Messages("GlobalShops")
	private static MessagesBox messagesBox;
	@Inject
	private static IGuiManager guiManager;
	
	private final Gui previousGui;
	
	private final SimpleSyncCallback callback;
	
	private ConfirmGui(Gui previousGui)
	{
		super(messagesBox, "shop-confirm");
		
		this.previousGui = previousGui;
		this.callback = new SimpleSyncCallback();
	}
	
	@Override
	protected void onOpen(Player player)
	{
		Preconditions.checkState(getViewers().size() == 1);
	}
	
	@ClickHandler
	public void accept(GuiClickEvent event)
	{
		callback.callComplete();
		previousGui.open(getViewers().stream().findFirst().get());
	}
	
	@ClickHandler
	public void deny(GuiClickEvent event)
	{
		previousGui.open(getViewers().stream().findFirst().get());
	}
	
	public static ISyncCallback openConfirmGui(Player player)
	{
		Gui currentGui = guiManager.getCurrentGui(player);
		ConfirmGui gui = new ConfirmGui(currentGui);
		gui.open(player);
		return gui.callback;
	}
}
