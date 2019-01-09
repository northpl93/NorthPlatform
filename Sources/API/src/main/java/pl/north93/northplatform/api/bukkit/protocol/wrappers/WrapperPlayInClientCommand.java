package pl.north93.northplatform.api.bukkit.protocol.wrappers;

import java.lang.invoke.MethodHandle;

import net.minecraft.server.v1_12_R1.PacketPlayInClientCommand;
import net.minecraft.server.v1_12_R1.PacketPlayInClientCommand.EnumClientCommand;

public class WrapperPlayInClientCommand extends AbstractWrapper<PacketPlayInClientCommand>
{
    private static final MethodHandle get_field_cmd = unreflectGetter(PacketPlayInClientCommand.class, "a");
    private static final MethodHandle set_field_cmd = unreflectSetter(PacketPlayInClientCommand.class, "a");

    public WrapperPlayInClientCommand(final PacketPlayInClientCommand packet)
    {
        super(packet);
    }

    public EnumClientCommand getClientCommand()
    {
        try
        {
            return (EnumClientCommand) get_field_cmd.invokeExact(this.packet);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    public void setClientCommand(final EnumClientCommand clientCommand)
    {
        try
        {
            set_field_cmd.invokeExact(this.packet, clientCommand);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }
}
