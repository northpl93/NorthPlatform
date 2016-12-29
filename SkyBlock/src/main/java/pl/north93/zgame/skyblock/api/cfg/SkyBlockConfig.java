package pl.north93.zgame.skyblock.api.cfg;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;

@CfgComment("Konfiguracja SkyBlocka")
public class SkyBlockConfig
{
    @CfgComment("Nazwa grupy serwerów z poczekalniami.")
    private String             lobbyServersGroup;

    @CfgComment("Unikalne identyfikatory serwerów używanych jako hosty SkyBlocka")
    private List<UUID>         skyBlockServers;

    private List<IslandConfig> islandTypes;

    public String getLobbyServersGroup()
    {
        return this.lobbyServersGroup;
    }

    public List<UUID> getSkyBlockServers()
    {
        return this.skyBlockServers;
    }

    public List<IslandConfig> getIslandTypes()
    {
        return this.islandTypes;
    }

    public IslandConfig getIslandType(final String name)
    {
        for (final IslandConfig islandType : this.islandTypes)
        {
            if (islandType.getName().equals(name))
            {
                return islandType;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("skyBlockServers", this.skyBlockServers).append("islandTypes", this.islandTypes).toString();
    }
}
