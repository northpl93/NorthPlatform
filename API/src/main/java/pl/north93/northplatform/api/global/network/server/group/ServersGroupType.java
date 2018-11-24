package pl.north93.northplatform.api.global.network.server.group;

/**
 * Opisuje typ grupy serwerow.
 */
public enum ServersGroupType
{
    /**
     * Grupa zarzadzana automatycznie przez kontroler.
     * Brak mozliwosci skonfigurowania listy serwerow.
     * Serwery uruchamiane sa automatycznie.
     */
    MANAGED,
    /**
     * Konfigurowana jest lista serwerow ktore beda obecne w tej grupie.
     * Serwery uruchamiane sa recznie.
     */
    UN_MANAGED
}
