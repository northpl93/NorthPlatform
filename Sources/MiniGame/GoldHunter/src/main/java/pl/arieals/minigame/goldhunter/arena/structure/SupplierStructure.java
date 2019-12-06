package pl.arieals.minigame.goldhunter.arena.structure;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import com.google.common.base.Preconditions;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;

import org.slf4j.Logger;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.player.GameTeam;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.bukkit.tick.Tick;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class SupplierStructure extends PlayerStructure
{
    @Inject
    @GoldHunterLogger
    private static Logger logger;
    
    @Inject
    private static GoldHunter goldHunter;
    
    private final GameTeam team;
    private int time;
    
    public SupplierStructure(BlockVector baseLocation, GoldHunterPlayer player, int time)
    {
        super(baseLocation, player);

        Preconditions.checkArgument(time > 0, "Time must be greter than 0");
        this.team = Preconditions.checkNotNull(player.getTeam());
        this.time = time;
    }
    
    @Override
    protected boolean trySpawn()
    {
        return structureBuilder(0, 0, 0, Material.FENCE)
                .and(0, 1, 0, Material.DROPPER, 0)
                .and(0, 2, 0, Material.CARPET, team == GameTeam.RED ? 14 : 3)
                .tryBuild();
    }
    
    @Override
    protected void onDestroy(GoldHunterPlayer destroyer)
    {
        if ( getPlayer().equals(destroyer) )
        {
            getPlayer().sendMessage("destroy_own_supplier");
            removeStructure();
        }
        else if ( destroyer.getTeam() == team )
        {
            destroyer.sendMessage("cannot_destroy_own_team_supplier");
            return;
        }
        else
        {
            getPlayer().sendMessage("destroy_supplier", destroyer.getDisplayName());
            removeStructure();
        }
    }
    
    @Override
    protected void onRemove()
    {
        logger.debug("Supplier remove for player {}", getPlayer());
        getPlayer().getAbilityTracker().resetAbilityLoading();
    }
    
    @Tick
    private void handleTime()
    {
        if ( --time == 0 )
        {
            removeStructure();
        }
    }
    
    @Tick
    private void handleNearPlayers()
    {
        if ( MinecraftServer.currentTick % 20 != 0 )
        {
            return;
        }
        
        for ( Player p : getWorld().getEntitiesByClass(Player.class) )
        {
            if ( p.getLocation().add(-0.5, -0.5, -0.5).toVector().distanceSquared(getBaseLocation()) > 4 )
            {
                continue;
            }
            
            GoldHunterPlayer player = goldHunter.getPlayer(p);
            if ( player != null )
            {
                handlePlayer(player);
            }
        }
      
    }
    
    private void handlePlayer(GoldHunterPlayer player)
    {
        logger.debug("Supplier handle player {}", player);
        
        if ( player.getTeam() == team )
        {
            player.getPlayer().getInventory().addItem(new ItemStack(Material.WOOD));
        }
    }
}
