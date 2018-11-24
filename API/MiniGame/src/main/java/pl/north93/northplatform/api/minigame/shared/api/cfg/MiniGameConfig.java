package pl.north93.northplatform.api.minigame.shared.api.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.shared.api.GameIdentity;
import pl.north93.northplatform.api.minigame.shared.api.LobbyMode;

@XmlRootElement(name = "minigame")
@XmlAccessorType(XmlAccessType.FIELD)
public class MiniGameConfig
{
    @XmlElement
    private GameIdentity     gameIdentity; // identyfikator minigry
    @XmlElement
    private String           hubId; // identyfikator huba tej minigry
    @XmlElement
    private LobbyMode        lobbyMode; // Tryb pracy lobby
    @XmlElement
    private MapVotingConfig  mapVoting; // Ustawienia glosowania na mape
    @XmlElement
    private DeathMatchConfig deathMatch; // ustawienia death matchu
    @XmlElement
    private Boolean          isDynamic; // Czy gra jest dynamiczna (pozwalajaca na wchodzenie w trakcie gry)
    @XmlElement
    private ReconnectConfig  reconnect; // Ustawienia powrotu do gry
    @XmlElement
    private Integer          slots; // Maksymalna ilość graczy na arenie
    @XmlElement
    private Integer          vipSlots; // Sloty zarezerwowane dla VIPów (odejmowane od wartości slots)
    @XmlElement
    private Integer          toStart; // Ilosc graczy wymagana do rozpoczecia gry
    @XmlElement
    private Integer          startCooldown; // Po jakim czasie gra startuje po uzyskaniu toStart graczy
    @XmlElement
    private String           mapsDirectory; // Katalog z konfiguracją map
    @XmlElement
    private Integer          arenas; // Liczba aren uruchamianych na serwerze hostującym minigrę

    public GameIdentity getGameIdentity()
    {
        return this.gameIdentity;
    }

    public String getHubId()
    {
        return this.hubId;
    }

    public LobbyMode getLobbyMode()
    {
        return this.lobbyMode;
    }

    public MapVotingConfig getMapVoting()
    {
        return this.mapVoting;
    }

    public DeathMatchConfig getDeathMatch()
    {
        return this.deathMatch;
    }

    public Boolean isDynamic()
    {
        return this.isDynamic;
    }

    public ReconnectConfig getReconnect()
    {
        return this.reconnect;
    }

    public Integer getSlots()
    {
        return this.slots;
    }

    public Integer getVipSlots()
    {
        return this.vipSlots;
    }

    public Integer getToStart()
    {
        return this.toStart;
    }

    public Integer getStartCooldown()
    {
        return this.startCooldown;
    }

    public String getMapsDirectory()
    {
        return this.mapsDirectory;
    }

    public Integer getArenas()
    {
        return this.arenas;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("gameIdentity", this.gameIdentity).append("hubId", this.hubId).append("lobbyMode", this.lobbyMode).append("mapVoting", this.mapVoting).append("deathMatch", this.deathMatch).append("isDynamic", this.isDynamic).append("slots", this.slots).append("vipSlots", this.vipSlots).append("toStart", this.toStart).append("startCooldown", this.startCooldown).append("mapsDirectory", this.mapsDirectory).append("arenas", this.arenas).toString();
    }
}
