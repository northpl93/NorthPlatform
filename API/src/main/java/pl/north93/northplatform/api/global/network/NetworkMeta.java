package pl.north93.northplatform.api.global.network;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.ToString;

/**
 * Używane do wczytywania konfiguracji z xml i do wysyłanie przez msg pack
 */
@ToString
@XmlRootElement(name = "network")
@XmlAccessorType(XmlAccessType.NONE)
public class NetworkMeta
{
    @XmlElement
    public JoiningPolicy joiningPolicy; // kto może wchodzić na serwer

    @XmlElement
    public Integer displayMaxPlayers; // wyswietlana maksymalna liczba graczy

    @XmlElement
    public String serverListMotd; // Wiadomosc dnia na liscie serwerow

    @XmlElement
    public String serverListVersion; // Tekst wysyłany jako wersja serwera

    @XmlElement
    public String defaultServersGroup; // nazwa domyślnej grupy serwerów, konfigurowane w autoscaler.xml
}
