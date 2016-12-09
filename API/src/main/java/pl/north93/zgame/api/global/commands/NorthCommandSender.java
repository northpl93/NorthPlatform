package pl.north93.zgame.api.global.commands;

public interface NorthCommandSender
{
    void sendMessage(String message);

    boolean isPlayer();

    boolean isConsole();
}
