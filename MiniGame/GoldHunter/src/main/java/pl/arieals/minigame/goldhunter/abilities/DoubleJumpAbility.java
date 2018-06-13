package pl.arieals.minigame.goldhunter.abilities;

import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayInAbilities;
import net.minecraft.server.v1_12_R1.PacketPlayOutAbilities;
import net.minecraft.server.v1_12_R1.PlayerAbilities;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.player.AbilityHandler;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.packets.event.AsyncPacketInEvent;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class DoubleJumpAbility implements AbilityHandler, AutoListener
{
    @Inject
    private static GoldHunter goldHunter;
    @Inject
    @GoldHunterLogger
    private static Logger logger;
    
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        // Prevent use ability with Q key
        return false;
    }
    
    @Override
    public void onReady(GoldHunterPlayer player)
    {
        logger.debug("Double jump ready for {}", player);
        
        EntityPlayer ep = ((CraftPlayer) player.getPlayer()).getHandle();
        PlayerAbilities current = ep.abilities;
        
        PlayerAbilities newAbilities = new PlayerAbilities();
        newAbilities.canFly = true;
        newAbilities.isFlying = false;
        newAbilities.canInstantlyBuild = current.canInstantlyBuild;
        newAbilities.flySpeed = current.flySpeed;
        newAbilities.mayBuild = current.mayBuild;
        newAbilities.walkSpeed = current.walkSpeed;
        
        ep.playerConnection.sendPacket(new PacketPlayOutAbilities(newAbilities));
        player.setDoubleJumpActive(true);
    }
    
    @EventHandler
    public void onDoubleJumpAsync(AsyncPacketInEvent event)
    {
        if ( !( event.getPacket() instanceof PacketPlayInAbilities ) )
        {
            return;
        }
        
        event.setCancelled(true);
        goldHunter.runTask(() -> onDoubleJumpSync(event));
    }
    
    private void onDoubleJumpSync(AsyncPacketInEvent event)
    {
        PacketPlayInAbilities packet = (PacketPlayInAbilities) event.getPacket();
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        
        if ( player == null )
        {
            return;
        }
        
        if ( !player.isDoubleJumpActive() )
        {
            player.getPlayer().setFlying(packet.isFlying());
            return;
        }
        
        if ( packet.isFlying() )
        {
            player.doubleJump();
            player.setDoubleJumpActive(false);
            player.getAbilityTracker().resetAbilityLoading();
            resetClientSideAbilities(player);
        }
    }
    
    private void resetClientSideAbilities(GoldHunterPlayer player)
    {
        EntityPlayer ep = player.getMinecraftPlayer();
        ep.playerConnection.sendPacket(new PacketPlayOutAbilities(ep.abilities));
    }
}
