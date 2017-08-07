package pl.north93.zgame.api.bukkit.packets.wrappers;

import static java.lang.invoke.MethodType.methodType;


import java.lang.invoke.MethodHandle;
import java.util.List;

import net.minecraft.server.v1_10_R1.EnumGamemode;
import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_10_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

import com.mojang.authlib.GameProfile;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/*
 * Stosujemy tu troche magii poniewaz uposledzony kod minecrafta
 * buguje kompilator javy i nie da sie bezposrednio uzyc klasy
 * PlayerInfoData. Dlatego recznie zdobywam jej konstruktor
 * przez MethodHandle i uzywam go w #addPlayerData.
 */
public class WrapperPacketPlayOutPlayerInfo extends AbstractWrapper
{
    private static final MethodHandle get_field_action = unreflectGetter(PacketPlayOutPlayerInfo.class, "a");
    private static final MethodHandle set_field_action = unreflectSetter(PacketPlayOutPlayerInfo.class, "a");
    private static final MethodHandle get_field_data = unreflectGetter(PacketPlayOutPlayerInfo.class, "b");
    private static final MethodHandle data_constructor;
    private final PacketPlayOutPlayerInfo packet;

    static
    {
        try
        {
            final Class<?> clazz = Class.forName("net.minecraft.server.v1_10_R1.PacketPlayOutPlayerInfo$PlayerInfoData");
            data_constructor = lookup.findConstructor(clazz, methodType(void.class, PacketPlayOutPlayerInfo.class, GameProfile.class, int.class, EnumGamemode.class, IChatBaseComponent.class))
                                     .asType(methodType(Object.class, PacketPlayOutPlayerInfo.class, GameProfile.class, int.class, EnumGamemode.class, IChatBaseComponent.class));
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    public WrapperPacketPlayOutPlayerInfo(final PacketPlayOutPlayerInfo packet)
    {
        this.packet = packet;
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

    @SuppressWarnings("unchecked")
    public void addPlayerData(final GameProfile gameProfile, final int ping, final EnumGamemode gameMode, final IChatBaseComponent displayName)
    {
        try
        {
            final List dataList = (List) get_field_data.invokeExact(this.packet);
            //final Object playerListData = ;

            dataList.add(data_constructor.invokeExact(this.packet, gameProfile, ping, gameMode, displayName));
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("packet", this.packet).toString();
    }
}
