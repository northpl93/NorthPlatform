package pl.north93.zgame.api.global.config.server;

/**
 * Interfejs zdalnego wywolywania procedur udostepniany przez
 * hosta configow.
 */
public interface IConfigServerRpc
{
    Boolean reloadConfig(String configId);
}