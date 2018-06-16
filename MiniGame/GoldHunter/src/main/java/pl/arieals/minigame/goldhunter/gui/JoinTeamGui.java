package pl.arieals.minigame.goldhunter.gui;

import pl.arieals.minigame.goldhunter.player.GameTeam;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.player.PlayerRank;
import pl.north93.zgame.api.bukkit.gui.ClickHandler;
import pl.north93.zgame.api.bukkit.gui.event.GuiClickEvent;

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
            if ( !player.getPlayer().hasPermission(PlayerRank.VIP.getPermission()) )
            {
                return;
            }
            
            team = GameTeam.valueOf(teamName);
        }
        
        player.getArena().signToTeam(player, team);
        closeAll();
    }
}
