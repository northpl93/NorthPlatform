package pl.north93.northplatform.minigame.goldhunter.gui;

import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.bukkit.gui.ClickHandler;
import pl.north93.northplatform.api.bukkit.gui.HotbarMenu;
import pl.north93.northplatform.api.bukkit.gui.event.HotbarClickEvent;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class LobbyHotbar extends HotbarMenu
{
    @Inject
    @Messages("gh_gui")
    private static MessagesBox messages;
    
    private final GoldHunterPlayer player;
    
    public LobbyHotbar(GoldHunterPlayer player)
    {
        super(messages, "gh/lobby");
        this.player = player;
    }
    
    @ClickHandler
    public void joinTeam(HotbarClickEvent event)
    {
        new JoinTeamGui(player).open();
    }
    
    @ClickHandler
    public void selectClass(HotbarClickEvent event)
    {
        new SelectClassGui(player).open();
    }
    
    @ClickHandler
    public void quit(HotbarClickEvent event)
    {
        event.getWhoClicked().performCommand("hub");
    }
}
