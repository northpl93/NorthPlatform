package pl.north93.zgame.api.global.commands;

import pl.north93.zgame.api.global.messages.Messageable;

public interface NorthCommandSender extends Messageable
{
    String getName();

    boolean isPlayer();

    boolean isConsole();

    Object unwrapped();
}
