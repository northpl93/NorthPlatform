package pl.north93.zgame.api.global.repo.clientimpl;

import java.io.File;

import pl.north93.zgame.api.global.repo.RepoClient;
import pl.north93.zgame.api.global.repo.RepoFile;

public class RepoClientImpl implements RepoClient
{
    @Override
    public File getLocalCache()
    {
        return null;
    }

    @Override
    public RepoFile getFileByName()
    {
        return null;
    }
}
