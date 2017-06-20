package pl.arieals.api.minigame.shared.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "minigame")
@XmlAccessorType(XmlAccessType.FIELD)
public class MiniGameConfig
{
    @XmlElement
    private String          miniGameId; // unikalna nazwa minigry
    @XmlElement
    private LobbyMode       lobbyMode; // Tryb pracy lobby
    @XmlElement
    private MapVotingConfig mapVoting; // Ustawienia glosowania na mape
    @XmlElement
    private Boolean         isDynamic; // Czy gra jest dynamiczna (pozwalajaca na wchodzenie w trakcie gry)
    @XmlElement
    private Integer         slots; // Maksymalna ilość graczy na arenie
    @XmlElement
    private Integer         vipSlots; // Sloty zarezerwowane dla VIPów (odejmowane od wartości slots)
    @XmlElement
    private Integer         toStart; // Ilosc graczy wymagana do rozpoczecia gry
    @XmlElement
    private String          mapsDirectory; // Katalog z konfiguracją map
    @XmlElement
    private Integer         arenas; // Liczba aren uruchamianych na serwerze hostującym minigrę

    public String getMiniGameId()
    {
        return this.miniGameId;
    }

    public LobbyMode getLobbyMode()
    {
        return this.lobbyMode;
    }

    public MapVotingConfig getMapVoting()
    {
        return this.mapVoting;
    }

    public Boolean isDynamic()
    {
        return this.isDynamic;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("miniGameId", this.miniGameId).append("lobbyMode", this.lobbyMode).append("mapVoting", this.mapVoting).append("isDynamic", this.isDynamic).append("slots", this.slots).append("vipSlots", this.vipSlots).append("toStart", this.toStart).append("mapsDirectory", this.mapsDirectory).append("arenas", this.arenas).toString();
    }
}
