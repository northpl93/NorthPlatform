package pl.north93.zgame.api.global.update.client.api;

import java.util.Collection;

import pl.north93.zgame.api.global.update.api.UpdateFile;

public interface IUpdateInfo
{
    boolean isNeedsUpdate();

    Collection<UpdateFile> getFiles();
}
