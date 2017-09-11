package pl.arieals.minigame.goldhunter;

import pl.north93.zgame.api.bukkit.gui.ClickHandler;
import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.bukkit.gui.GuiClickEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class JoinTeamGui extends Gui
{
    @Inject
    @Messages("gh_gui")
    private static MessagesBox messages;
    
    private final GoldHunterPlayer player;
    
    public JoinTeamGui(GoldHunterPlayer player)
    {
        super(messages, "gh/select_team");
        this.player = player;
    }
    
    @ClickHandler
    public void select(GuiClickEvent event)
    {
        String teamName = event.getClickedElement().getMetadata().get("team");
        GameTeam team = null;
        
        if ( teamName != null )
        {
            team = GameTeam.valueOf(teamName);
        }
        
        player.getArena().signToTeam(player, team);
        closeAll();
    }
}
