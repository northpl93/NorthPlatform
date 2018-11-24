package pl.north93.northplatform.api.global.config.server;

/**
 * Interfejs zdalnego wywolywania procedur udostepniany przez
 * hosta configow.
 */
public interface IConfigServerRpc
{
    Boolean reloadConfig(String configId);

    Boolean updateConfig(String configId, Object newValue);
}
