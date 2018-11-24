package pl.north93.northplatform.api.global.network.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.network.impl.servers.ServerDto;
import pl.north93.northplatform.api.global.redis.event.INetEvent;

/**
 * Event sieciowy wywoływany gdy demon wykryje niepoprawne zamknięcie serwera.
 */
public class ServerDeathNetEvent implements INetEvent
{
    private ServerDto serverDto;

    public ServerDeathNetEvent()
    {
    }

    public ServerDeathNetEvent(final ServerDto serverDto)
    {
        this.serverDto = serverDto;
    }

    /**
     * Zwraca obiekt reprezentujący serwer w momencie wyłączenia.
     * Wszystkie zasoby używane przez serwer zostały już zwolnione.
     *
     * @return Obiekt reprezentujący serwer.
     */
    public ServerDto getServer()
    {
        return this.serverDto;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serverDto", this.serverDto).toString();
    }
}
