package pl.north93.northplatform.api.bukkit.protocol.wrappers;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.server.v1_12_R1.EnumGamemode;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

import com.mojang.authlib.GameProfile;

import lombok.SneakyThrows;

public class WrapperPlayOutPlayerInfo extends AbstractWrapper<PacketPlayOutPlayerInfo>
{
    private static final MethodHandle get_field_action = unreflectGetter(PacketPlayOutPlayerInfo.class, "a");
    private static final MethodHandle set_field_action = unreflectSetter(PacketPlayOutPlayerInfo.class, "a");
    private static final MethodHandle get_field_data = unreflectGetter(PacketPlayOutPlayerInfo.class, "b");

    public WrapperPlayOutPlayerInfo(final PacketPlayOutPlayerInfo packet)
    {
        super(packet);
    }

    public EnumPlayerInfoAction getAction()
    {
        try
        {
            return (EnumPlayerInfoAction) get_field_action.invokeExact(this.packet);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    public void setAction(final EnumPlayerInfoAction action)
    {
        try
        {
            set_field_action.invokeExact(this.packet, action);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    @Deprecated
    public void addPlayerData(final GameProfile gameProfile, final int ping, final EnumGamemode gameMode, final IChatBaseComponent displayName)
    {
        addPlayerData(new PlayerInfoData(gameProfile, ping, gameMode, displayName));
    }

    @SneakyThrows(Throwable.class)
    public List<Object> getRawData()
    {
        return (List<Object>) get_field_data.invokeExact(packet);
    }
    
    public List<PlayerInfoData> getPlayerData()
    {
        return getRawData().stream().map(PlayerInfoData::new).collect(Collectors.toCollection(ArrayList::new));
    }
    
    public void addPlayerData(PlayerInfoData data)
    {
        getRawData().add(data.handle);
    }
    
    public static class PlayerInfoData
    {
        private static final MethodHandle GET_GAMEPROFILE;
        private static final MethodHandle GET_PING;
        private static final MethodHandle GET_GAMEMODE;
        private static final MethodHandle GET_DISPLAY_NAME;
        
        private static final MethodHandle CONSTRUCT;
        
        private final Object handle; // We cannot use direct reference due to badly built spigot jar which causes compile error when we use PlayerInfoData class.
        
        static
        {
            try
            {
                Class<?> clazz = Class.forName("net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo$PlayerInfoData");
                CONSTRUCT = lookup.findConstructor(clazz, MethodType.methodType(void.class, PacketPlayOutPlayerInfo.class, GameProfile.class, int.class, EnumGamemode.class, IChatBaseComponent.class));
            
                GET_GAMEPROFILE = unreflectGetter(clazz, "d");
                GET_PING = unreflectGetter(clazz, "b");
                GET_GAMEMODE = unreflectGetter(clazz, "c");
                GET_DISPLAY_NAME = unreflectGetter(clazz, "e");
            }
            catch ( Throwable e ) 
            {
                throw new RuntimeException(e);
            }
        }
        
        public PlayerInfoData(Object handle)
        {
            this.handle = handle;
        }

        @SneakyThrows(Throwable.class)
        public PlayerInfoData(GameProfile gameProfile, int ping, EnumGamemode gamemode, IChatBaseComponent displayName)
        {
            this.handle = CONSTRUCT.invoke(null, gameProfile, ping, gamemode, displayName); // It seems that we don't have to pass outer class refence
        }

        @SneakyThrows(Throwable.class)
        public GameProfile getGameProfile()
        {
            return (GameProfile) GET_GAMEPROFILE.invoke(handle);
        }

        @SneakyThrows(Throwable.class)
        public int getPing()
        {
            return (Integer) GET_PING.invoke(handle);
        }

        @SneakyThrows(Throwable.class)
        public EnumGamemode getGameMode()
        {
            return (EnumGamemode) GET_GAMEMODE.invoke(handle);
        }

        @SneakyThrows(Throwable.class)
        public IChatBaseComponent getDisplayName()
        {
            return (IChatBaseComponent) GET_DISPLAY_NAME.invoke(handle);
        }
    }
}
