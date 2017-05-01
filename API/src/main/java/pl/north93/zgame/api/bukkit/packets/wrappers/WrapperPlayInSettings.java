package pl.north93.zgame.api.bukkit.packets.wrappers;

import java.lang.invoke.MethodHandle;

import net.minecraft.server.v1_10_R1.PacketPlayInSettings;

public class WrapperPlayInSettings extends AbstractWrapper
{
    private static final MethodHandle set_field_lang = unreflectSetter(PacketPlayInSettings.class, "a");
    private final PacketPlayInSettings packet;

    public WrapperPlayInSettings(final PacketPlayInSettings packet)
    {
        this.packet = packet;
    }

    public String getLanguage()
    {
        return this.packet.a();
    }

    public void setLanguage(final String language)
    {
        try
        {
            set_field_lang.invokeExact(this.packet, language);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    // todo
}
