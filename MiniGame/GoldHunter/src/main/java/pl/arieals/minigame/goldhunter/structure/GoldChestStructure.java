package pl.arieals.minigame.goldhunter.structure;

import org.bukkit.Material;
import org.bukkit.util.BlockVector;

import com.google.common.base.Preconditions;

import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.goldhunter.GameTeam;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.SoundEffect;
import pl.arieals.minigame.goldhunter.Structure;

public class GoldChestStructure extends Structure
{
    private final GameTeam team;
    
    public GoldChestStructure(BlockVector baseLocation, GameTeam team)
    {
        super(baseLocation);
        
        Preconditions.checkNotNull(team);
        this.team = team;
    }
    
    public GameTeam getTeam()
    {
        return team;
    }
    
    @Override
    protected boolean trySpawn()
    {
        return structureBuilder(getBaseLocation(), Material.OBSIDIAN).tryBuild();
    }
    
    @Override
    protected void onDestroy(GoldHunterPlayer destroyer)
    {
        Preconditions.checkArgument(destroyer.isIngame());
        
        if ( getArena().getLocalArena().getGamePhase() != GamePhase.STARTED)
        {
            return;
        }
        
        if ( destroyer.getTeam() == team )
        {
            destroyer.sendMessage("cannot_destroy_own_chest");
            return;
        }
        
        breakChestStructure();
        
        getArena().broadcastSeparatedMessageIngame("chest_destroy", destroyer.getDisplayNameBold(), team.getColoredBoldGenitive());
        getArena().updateChestsCount();
    }
    
    private void breakChestStructure()
    {
        removeStructure();
        SoundEffect.CHEST_DESTROY.play(getArena());
    }

}