package pl.arieals.api.minigame.shared.api;

import org.diorite.cfg.annotations.CfgComment;

public class MiniGame
{
    @CfgComment("Tryb pracy lobby")
    private LobbyMode lobbyMode;
    @CfgComment("Czy gra jest dynamiczna (pozwalajaca na wchodzenie w trakcie gry)")
    private Boolean   isDynamic;
    @CfgComment("Maksymalna ilość graczy na arenie")
    private Integer   slots;
    @CfgComment("Sloty zarezerwowane dla VIPów (odejmowane od wartości slots)")
    private Integer   vipSlots;

    public LobbyMode getLobbyMode()
    {
        return this.lobbyMode;
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
}
