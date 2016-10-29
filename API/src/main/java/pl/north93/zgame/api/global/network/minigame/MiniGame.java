package pl.north93.zgame.api.global.network.minigame;

import org.diorite.cfg.annotations.CfgComment;

public class MiniGame
{
    @CfgComment("Nazwa systemowa minigry")
    private String  systemName;
    @CfgComment("Nazwa grupy serwerów dla tej minigry (skonfigurowane w serversgroups.yml)")
    private String  serversGroupName;
    @CfgComment("Nazwa wyświetlana")
    private String  displayName;
    @CfgComment("Maksymalna ilość graczy na arenie")
    private Integer maxPlayers;
    @CfgComment("Ilość slotów przeznaczona na graczy VIP")
    private Integer vipSlots;
    @CfgComment("Minimalna ilość graczy wymagana do startu areny")
    private Integer minPlayersToStart;
    @CfgComment("Pozwolenie na zespoły. DISALLOW_TEAM, ALLOW_TEAM, REQUIRE_TEAM")
    private Teaming teaming;
    @CfgComment("Minimalny rozmiar zespołu")
    private Integer minTeamSize;
    @CfgComment("Maksymalny rozmiar zespołu")
    private Integer maxTeamSize;

    public String getSystemName()
    {
        return this.systemName;
    }

    public void setSystemName(final String systemName)
    {
        this.systemName = systemName;
    }

    public String getServersGroupName()
    {
        return this.serversGroupName;
    }

    public void setServersGroupName(final String serversGroupName)
    {
        this.serversGroupName = serversGroupName;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public void setDisplayName(final String displayName)
    {
        this.displayName = displayName;
    }

    public Integer getMaxPlayers()
    {
        return this.maxPlayers;
    }

    public void setMaxPlayers(final Integer maxPlayers)
    {
        this.maxPlayers = maxPlayers;
    }

    public Integer getVipSlots()
    {
        return this.vipSlots;
    }

    public void setVipSlots(final Integer vipSlots)
    {
        this.vipSlots = vipSlots;
    }

    public Integer getMinPlayersToStart()
    {
        return this.minPlayersToStart;
    }

    public void setMinPlayersToStart(final Integer minPlayersToStart)
    {
        this.minPlayersToStart = minPlayersToStart;
    }

    public Teaming getTeaming()
    {
        return this.teaming;
    }

    public void setTeaming(final Teaming teaming)
    {
        this.teaming = teaming;
    }

    public Integer getMinTeamSize()
    {
        return this.minTeamSize;
    }

    public void setMinTeamSize(final Integer minTeamSize)
    {
        this.minTeamSize = minTeamSize;
    }

    public Integer getMaxTeamSize()
    {
        return this.maxTeamSize;
    }

    public void setMaxTeamSize(final Integer maxTeamSize)
    {
        this.maxTeamSize = maxTeamSize;
    }
}
