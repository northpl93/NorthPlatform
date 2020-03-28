package pl.north93.northplatform.minigame.goldhunter.listener;

import org.bukkit.craftbukkit.v1_12_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_12_R1.ChatComponentText;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

import pl.north93.northplatform.minigame.goldhunter.GoldHunter;
import pl.north93.northplatform.minigame.goldhunter.player.GameTeam;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.bukkit.protocol.wrappers.WrapperPlayOutPlayerInfo;
import pl.north93.northplatform.api.bukkit.protocol.wrappers.WrapperPlayOutPlayerInfo.PlayerInfoData;
import pl.north93.northplatform.api.bukkit.protocol.ChannelWrapper;
import pl.north93.northplatform.api.bukkit.protocol.HandlerPriority;
import pl.north93.northplatform.api.bukkit.protocol.PacketEvent;
import pl.north93.northplatform.api.bukkit.protocol.PacketHandler;
import pl.north93.northplatform.api.bukkit.protocol.ProtocolManager;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;

public class PlayerListPacketListener implements AutoListener
{    
    private final GoldHunter goldHunter;
    private final ProtocolManager protocolManager;
    
    public PlayerListPacketListener(GoldHunter goldHunter, ProtocolManager protocolManager)
    {
        this.goldHunter = goldHunter;
        this.protocolManager = protocolManager;
    }
    
    @PacketHandler(priority = HandlerPriority.HIGHEST)
    public void onUpdateNameOnPlayerList(PacketEvent<PacketPlayOutPlayerInfo> event)
    {
        PacketPlayOutPlayerInfo handle = event.getPacket();
        WrapperPlayOutPlayerInfo packet = new WrapperPlayOutPlayerInfo(handle);

        Player bukkitPlayer = event.getPlayer();
        if ( bukkitPlayer == null )
        {
            return;
        }
        
        if ( packet.getAction() != EnumPlayerInfoAction.ADD_PLAYER && packet.getAction() != EnumPlayerInfoAction.UPDATE_DISPLAY_NAME )
        {
            return;
        }
        
        if ( packet.getAction() != EnumPlayerInfoAction.ADD_PLAYER )
        {
            event.setCancelled(true);
            //event.setPacket(null);
        }
        
        goldHunter.runTask(() -> syncProcessPacket(bukkitPlayer, packet));
    }
    
    private void syncProcessPacket(Player receiver, WrapperPlayOutPlayerInfo originalPacket)
    {
        WrapperPlayOutPlayerInfo newPacket = new WrapperPlayOutPlayerInfo(new PacketPlayOutPlayerInfo());
        newPacket.setAction(EnumPlayerInfoAction.UPDATE_DISPLAY_NAME);
        
        originalPacket.getPlayerData().forEach(data -> newPacket.addPlayerData(processEntry(receiver, data)));
        
        ChannelWrapper channelWrapper = protocolManager.getChannelWrapper(receiver);
        channelWrapper.writePacket(newPacket.getPacket());
    }
    
    private PlayerInfoData processEntry(Player receiver, PlayerInfoData originalData)
    {
        GoldHunterPlayer player = goldHunter.getPlayer(originalData.getGameProfile().getId());
        
        if ( player == null || !player.isIngame() )
        {
            return originalData;
        }
        
        GoldHunterPlayer receiverPlayer = goldHunter.getPlayer(receiver);
        
        String statsString = player.getStatsTracker().getStatsString();
        
        String classDisplayName = "???";
        
        // null reveiverPlayer means that player isn't on the arena or is a spectator
        if ( receiverPlayer == null || receiverPlayer.getTeam() == player.getTeam() || receiver.hasPermission("goldhunter.enemyclasses") )
        {
            BaseComponent className = player.getCurrentClass().getDisplayName().getValue(receiver.getLocale());
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
