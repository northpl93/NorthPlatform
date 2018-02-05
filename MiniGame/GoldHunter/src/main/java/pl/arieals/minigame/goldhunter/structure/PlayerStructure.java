package pl.arieals.minigame.goldhunter.structure;

import org.bukkit.util.BlockVector;

import com.google.common.base.Preconditions;

import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.Structure;

public abstract class PlayerStructure extends Structure
{
    private final GoldHunterPlayer player;
    
    protected PlayerStructure(BlockVector baseLocation, GoldHunterPlayer player)
    {
        super(baseLocation);
        
        this.player = Preconditions.checkNotNull(player);
    }
    
    public final GoldHunterPlayer getPlayer()
    {
        return player;
    }
}
