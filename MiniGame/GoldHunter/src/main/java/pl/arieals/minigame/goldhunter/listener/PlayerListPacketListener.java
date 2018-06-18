package pl.arieals.minigame.goldhunter.listener;

import net.minecraft.server.v1_12_R1.ChatComponentText;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

import org.bukkit.craftbukkit.v1_12_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import io.netty.channel.Channel;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.player.GameTeam;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.packets.event.AsyncPacketOutEvent;
import pl.north93.zgame.api.bukkit.packets.wrappers.WrapperPlayOutPlayerInfo;
import pl.north93.zgame.api.bukkit.packets.wrappers.WrapperPlayOutPlayerInfo.PlayerInfoData;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class PlayerListPacketListener implements AutoListener
{    
    private final GoldHunter goldHunter;
    
    public PlayerListPacketListener(GoldHunter goldHunter)
    {
        this.goldHunter = goldHunter;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUpdateNameOnPlayerList(AsyncPacketOutEvent event)
    {
        if ( !( event.getPacket() instanceof PacketPlayOutPlayerInfo ) )
        {
            return;
        }
        
        PacketPlayOutPlayerInfo handle = (PacketPlayOutPlayerInfo) event.getPacket();
        WrapperPlayOutPlayerInfo packet = new WrapperPlayOutPlayerInfo(handle);

        Player bukkitPlayer = event.getPlayer();
        if ( bukkitPlayer == null )
        {
            return;
        }

        GoldHunterPlayer player = goldHunter.getPlayer(bukkitPlayer);
        if ( player == null )
        {
            return;
        }
        
        if ( packet.getAction() != EnumPlayerInfoAction.ADD_PLAYER )
        {
            event.setCancelled(true);
            event.setPacket(null);
        }
        
        goldHunter.runTask(() -> syncProcessPacket(player, packet));
    }
    
    private void syncProcessPacket(GoldHunterPlayer receiver, WrapperPlayOutPlayerInfo originalPacket)
    {
        WrapperPlayOutPlayerInfo newPacket = new WrapperPlayOutPlayerInfo(new PacketPlayOutPlayerInfo());
        newPacket.setAction(originalPacket.getAction());
        
        originalPacket.getPlayerData().forEach(data -> newPacket.addPlayerData(processEntry(receiver, data)));
        
        Channel channel = receiver.getMinecraftPlayer().playerConnection.networkManager.channel;
        
        // skip handling event with our packet, to prevent infinite loop
        // TODO: add a way to get a tiny protocol handler name instead hardcoded one
        channel.eventLoop().execute(() -> channel.pipeline().context("tiny-API-1").write(newPacket.getPacket())); 
    }
    
    private PlayerInfoData processEntry(GoldHunterPlayer receiver, PlayerInfoData originalData)
    {
        GoldHunterPlayer player = goldHunter.getPlayer(originalData.getGameProfile().getId());
        
        if ( player == null || !player.isIngame() )
        {
            return originalData;
        }
        
        String statsString = player.getStatsTracker().getStatsString();
        
        String classDisplayName = "???";
        if ( receiver.getTeam() == player.getTeam() )
        {
            BaseComponent className = player.getCurrentClass().getDisplayName().getValue(receiver.getPlayer().getLocale());
            classDisplayName = ChatColor.stripColor(className.toLegacyText());
        }
        
        GameTeam displayTeam = player.getDisplayTeam();
        
        String listEntry = "§7" + statsString + " " + displayTeam.getSecondaryTeamColor() + "§l" + classDisplayName + " " + player.getDisplayName();
        IChatBaseComponent component = toMinecraftChatComponent(listEntry);
        
        return new PlayerInfoData(originalData.getGameProfile(), originalData.getPing(), originalData.getGameMode(), component);
    }
    
    private IChatBaseComponent toMinecraftChatComponent(String text)
    {
        IChatBaseComponent[] components = CraftChatMessage.fromString(text);
        
        ChatComponentText result = new ChatComponentText("");
        for ( IChatBaseComponent component : components )
        {
            result.addSibling(component);
        }
        
        return result;
    }
}
