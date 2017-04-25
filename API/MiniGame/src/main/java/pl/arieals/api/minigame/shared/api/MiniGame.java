package pl.arieals.api.minigame.shared.api;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("lobbyMode", this.lobbyMode).append("isDynamic", this.isDynamic).append("slots", this.slots).append("vipSlots", this.vipSlots).toString();
    }
}
