package pl.arieals.api.minigame.shared.api;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;

public class MiniGame
{
    @CfgComment("Tryb pracy lobby")
    private LobbyMode       lobbyMode;
    @CfgComment("Ustawienia glosowania na mape")
    private MapVotingConfig mapVoting;
    @CfgComment("Czy gra jest dynamiczna (pozwalajaca na wchodzenie w trakcie gry)")
    private Boolean         isDynamic;
    @CfgComment("Maksymalna ilość graczy na arenie")
    private Integer         slots;
    @CfgComment("Sloty zarezerwowane dla VIPów (odejmowane od wartości slots)")
    private Integer         vipSlots;
    @CfgComment("Ilosc graczy wymagana do rozpoczecia gry")
    private Integer         toStart;
    @CfgComment("Konfiguracja map")
    private List<GameMap>   gameMaps;

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

    public List<GameMap> getGameMaps()
    {
        return this.gameMaps;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("lobbyMode", this.lobbyMode).append("mapVoting", this.mapVoting).append("isDynamic", this.isDynamic).append("slots", this.slots).append("vipSlots", this.vipSlots).append("toStart", this.toStart).append("gameMaps", this.gameMaps).toString();
    }
}
