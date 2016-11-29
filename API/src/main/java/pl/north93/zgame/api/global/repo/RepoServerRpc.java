package pl.north93.zgame.api.global.repo;

import java.util.UUID;

public interface RepoServerRpc
{
    RepoFile getFileByUuid(UUID uuid);

    RepoFile getFileByLabel(String label);

    RepoFile[] getAllFiles();

    void deleteFile(UUID uuid);

    /**
     * Zmiania nazwę wyświetlaną pliku.
     *
     * @param uuid identyfikator pliku.
     * @param newLabel nowa nazwa wyświetlana.
     * @return stara nazwa pliku.
     */
    String setLabel(UUID uuid, String newLabel);
}
