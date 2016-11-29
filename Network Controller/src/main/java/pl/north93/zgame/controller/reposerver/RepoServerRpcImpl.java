package pl.north93.zgame.controller.reposerver;

import java.util.UUID;

import pl.north93.zgame.api.global.repo.RepoFile;
import pl.north93.zgame.api.global.repo.RepoServerRpc;

public class RepoServerRpcImpl implements RepoServerRpc
{
    @Override
    public RepoFile getFileByUuid(final UUID uuid)
    {
        return null;
    }

    @Override
    public RepoFile getFileByLabel(final String label)
    {
        return null;
    }

    @Override
    public RepoFile[] getAllFiles()
    {
        return new RepoFile[0];
    }

    @Override
    public void deleteFile(final UUID uuid)
    {

    }

    @Override
    public String setLabel(final UUID uuid, final String newLabel)
    {
        return null;
    }
}
