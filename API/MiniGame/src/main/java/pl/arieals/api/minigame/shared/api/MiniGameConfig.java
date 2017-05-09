package pl.arieals.api.minigame.shared.api;

import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.diorite.cfg.annotations.CfgComment;

public class MiniGameConfig
{
    @CfgComment("Tryb pracy lobby")
    @XmlElement
    private LobbyMode       lobbyMode;
    @CfgComment("Ustawienia glosowania na mape")
    @XmlElement
    private MapVotingConfig mapVoting;
    @CfgComment("Czy gra jest dynamiczna (pozwalajaca na wchodzenie w trakcie gry)")
    @XmlElement
    private Boolean         isDynamic;
    @CfgComment("Maksymalna ilość graczy na arenie")
    @XmlElement
    private Integer         slots;
    @CfgComment("Sloty zarezerwowane dla VIPów (odejmowane od wartości slots)")
    @XmlElement
    private Integer         vipSlots;
    @CfgComment("Ilosc graczy wymagana do rozpoczecia gry")
    @XmlElement
    private Integer         toStart;
    @CfgComment("Konfiguracja map")
    @XmlElement
    private String          mapsDirectory;

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

    public String getMapsDirecotry()
    {
        return this.mapsDirectory;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("lobbyMode", this.lobbyMode).append("mapVoting", this.mapVoting).append("isDynamic", this.isDynamic).append("slots", this.slots).append("vipSlots", this.vipSlots).append("toStart", this.toStart).append("mapsDirectory", this.mapsDirectory).toString();
    }
}
