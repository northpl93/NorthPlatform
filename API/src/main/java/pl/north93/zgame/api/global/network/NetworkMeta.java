package pl.north93.zgame.api.global.network;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Używane do wczytywania konfiguracji z xml i do wysyłanie przez msg pack
 */
@XmlRootElement(name = "network")
@XmlAccessorType(XmlAccessType.FIELD)
public class NetworkMeta
{
    @XmlElement
    public JoiningPolicy joiningPolicy; // kto może wchodzić na serwer

    @XmlElement
    public Integer displayMaxPlayers; // wyswietlana maksymalna liczba graczy

    @XmlElement
    public String serverListMotd; // Wiadomosc dnia na liscie serwerow

    @XmlElement
    public String defaultServersGroup; // nazwa domyślnej grupy serwerów, konfigurowane w autoscaler.xml

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("joiningPolicy", this.joiningPolicy).append("displayMaxPlayers", this.displayMaxPlayers).append("serverListMotd", this.serverListMotd).toString();
    }
}
