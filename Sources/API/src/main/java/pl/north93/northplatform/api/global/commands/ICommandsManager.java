package pl.north93.northplatform.api.global.commands;

public interface ICommandsManager
{
    void registerCommand(NorthCommand northCommand);

    void stop();
}
