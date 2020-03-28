package pl.north93.northplatform.minigame.goldhunter.arena.structure;

import org.bukkit.util.BlockVector;

import com.google.common.base.Preconditions;

import pl.north93.northplatform.minigame.goldhunter.arena.Structure;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;

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
