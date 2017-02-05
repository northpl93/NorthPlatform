package pl.north93.zgame.api.global.update.api;

import pl.north93.zgame.api.global.Platform;

public interface IUpdateApiRpc
{
    boolean isControllerReady();

    void reloadConfig();

    UpdateFile[] getFilesFor(Platform platform, String clientId);
}
