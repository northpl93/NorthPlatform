package pl.north93.northplatform.api.bukkit.protocol.wrappers;

import java.lang.invoke.MethodHandle;

import net.minecraft.server.v1_12_R1.PacketPlayInSettings;

public class WrapperPlayInSettings extends AbstractWrapper<PacketPlayInSettings>
{
    private static final MethodHandle set_field_lang = unreflectSetter(PacketPlayInSettings.class, "a");

    public WrapperPlayInSettings(final PacketPlayInSettings packet)
    {
        super(packet);
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
