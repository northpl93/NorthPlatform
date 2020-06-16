package pl.north93.northplatform.api.global.config.server;

/**
 * Interfejs zdalnego wywolywania procedur udostepniany przez
 * hosta configow.
 */
public interface IConfigServerRpc
{
    boolean reloadConfig(String configId);

    boolean updateConfig(String configId, Object newValue);
}
