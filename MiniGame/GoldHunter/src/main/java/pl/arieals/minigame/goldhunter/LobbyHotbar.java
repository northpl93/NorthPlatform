package pl.arieals.minigame.goldhunter;

import pl.arieals.minigame.goldhunter.gui.SelectClassGui;
import pl.north93.zgame.api.bukkit.gui.ClickHandler;
import pl.north93.zgame.api.bukkit.gui.HotbarMenu;
import pl.north93.zgame.api.bukkit.gui.impl.HotbarClickEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

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
        new JoinTeamGui(player).open(player.getPlayer());
    }
    
    @ClickHandler
    public void selectClass(HotbarClickEvent event)
    {
        new SelectClassGui(player).open(player.getPlayer());
    }
    
    @ClickHandler
    public void quit(HotbarClickEvent event)
    {
        // TODO:
        event.getWhoClicked().kickPlayer("TODO");
    }
}
