package pl.arieals.minigame.goldhunter.abilities;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayInAbilities;
import net.minecraft.server.v1_12_R1.PacketPlayOutAbilities;
import net.minecraft.server.v1_12_R1.PlayerAbilities;

import org.slf4j.Logger;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.player.AbilityHandler;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.protocol.PacketEvent;
import pl.north93.zgame.api.bukkit.protocol.PacketHandler;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class DoubleJumpAbility implements AbilityHandler
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
        
        EntityPlayer ep = player.getMinecraftPlayer();
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
    
    @PacketHandler
    public void onDoubleJumpAsync(PacketEvent<PacketPlayInAbilities> event)
    {
        event.setCancelled(true);
        goldHunter.runTask(() -> onDoubleJumpSync(event));
    }
    
    private void onDoubleJumpSync(PacketEvent<PacketPlayInAbilities> event)
    {
        PacketPlayInAbilities packet = event.getPacket();
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        
        if ( player == null || !player.isDoubleJumpActive() )
        {
            EntityPlayer playerEntity = INorthPlayer.asCraftPlayer(event.getPlayer()).getHandle();
            
            if ( !playerEntity.abilities.canFly && packet.isFlying() )
            {
                playerEntity.updateAbilities();
            }
            else
            {
                playerEntity.playerConnection.a(packet);
            }
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
