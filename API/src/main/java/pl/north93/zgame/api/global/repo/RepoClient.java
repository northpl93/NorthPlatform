package pl.north93.zgame.api.global.repo;

import java.io.File;

public interface RepoClient
{
    File getLocalCache();

    RepoFile getFileByName();

    //RepoFile get
}
