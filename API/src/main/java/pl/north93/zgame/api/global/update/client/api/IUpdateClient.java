package pl.north93.zgame.api.global.update.client.api;

public interface IUpdateClient
{
    void checkForUpdate();

    IUpdateInfo getUpdateInfo();

    void beginUpdate();
}
