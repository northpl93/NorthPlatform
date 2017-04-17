package pl.north93.zgame.api.bukkitcommands;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.commands.annotation.QuickCommand;

public class TestClass
{
    @QuickCommand(name = "testujemy", async = true)
    public static void test(final NorthCommandSender sender, final Arguments args, final String label)
    {
        sender.sendMessage("Testujemy");
    }
}
