package pl.north93.zgame.api.global.network.proxy;

public enum AntiDdosMode
{
    /**
     * Anty DDoS caly czas wlaczony na wszystkich serwerach proxy.
     */
    ON,

    /**
     * Anty DDoS caly czas wylaczony na wszystkich serwerach proxy.
     */
    OFF,

    /**
     * Serwery proxy automatycznie decyduja o wlaczeniu lub wylaczeniu Anty DDoS.
     */
    AUTO
}
