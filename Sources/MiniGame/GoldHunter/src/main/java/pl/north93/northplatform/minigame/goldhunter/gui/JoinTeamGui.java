package pl.north93.northplatform.minigame.goldhunter.gui;

import pl.north93.northplatform.minigame.goldhunter.player.GameTeam;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.bukkit.gui.ClickHandler;
import pl.north93.northplatform.api.bukkit.gui.event.GuiClickEvent;

public class JoinTeamGui extends GoldHunterGui
{
    public JoinTeamGui(GoldHunterPlayer player)
    {
        super(player, "gh/select_team");
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
        
        player.getArena().trySignToTeam(player, team);
        closeAll();
    }
}
