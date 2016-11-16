package pl.north93.zgame.api.global.deployment;

public enum ServersAllocatorType
{
    /**
     * Ilośćserwerów zawsze jest stała.
     */
    STATIC,
    /**
     * Serwer uznawany jest za niedostępny dopiero gdy jest pełny.
     */
    PLAYER_COUNT,
    /**
     * Serwer uznawany jest za niedostępny gdy ustawi politykę dołączania
     * na NOBODY. <br>
     * Przydatne w minigrach.
     */
    JOINING_POLICY
}
