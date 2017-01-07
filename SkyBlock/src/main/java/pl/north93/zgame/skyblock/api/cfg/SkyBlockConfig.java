package pl.north93.zgame.skyblock.api.cfg;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;
import org.diorite.cfg.annotations.defaults.CfgStringDefault;

import pl.north93.zgame.skyblock.api.utils.Coords3D;

@CfgComment("Konfiguracja SkyBlocka")
public class SkyBlockConfig
{
    @CfgComment("Nazwa grupy serwerów z poczekalniami.")
    @CfgStringDefault("default")
    private String             lobbyServersGroup;

    @CfgComment("Unikalne identyfikatory serwerów używanych jako hosty SkyBlocka")
    @CfgDelegateDefault("getDefaultSkyBlockServers")
    private List<String>       skyBlockServers;

    @CfgDelegateDefault("getDefaultIslandTypes")
    private List<IslandConfig> islandTypes;

    private static List<IslandConfig> getDefaultIslandTypes()
    {
        //noinspection ArraysAsListWithZeroOrOneArgument
        return Arrays.asList(new IslandConfig("Testowa", new Coords3D(5, 5, 5), 50, 16));
    }

    private static List<String> getDefaultSkyBlockServers()
    {
        //noinspection ArraysAsListWithZeroOrOneArgument
        return Arrays.asList("a89498a5-4571-41ab-a471-d78e325aeaba");
    }

    public String getLobbyServersGroup()
    {
        return this.lobbyServersGroup;
    }

    public List<String> getSkyBlockServers()
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