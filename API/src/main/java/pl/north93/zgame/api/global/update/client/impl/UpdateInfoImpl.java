package pl.north93.zgame.api.global.update.client.impl;

import java.util.Collection;

import pl.north93.zgame.api.global.update.api.UpdateFile;
import pl.north93.zgame.api.global.update.client.api.IUpdateInfo;

public class UpdateInfoImpl implements IUpdateInfo
{
    private final Collection<UpdateFile> files;

    public UpdateInfoImpl(final Collection<UpdateFile> files)
    {
        this.files = files;
    }

    @Override
    public boolean isNeedsUpdate()
    {
        return ! this.files.isEmpty();
    }

    @Override
    public Collection<UpdateFile> getFiles()
    {
        return this.files;
    }
}
