package pl.north93.zgame.skyblock.api.cfg;

import static org.diorite.cfg.annotations.CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgCollectionStyle;
import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.defaults.CfgBooleanDefault;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;
import org.diorite.cfg.annotations.defaults.CfgLongDefault;
import org.diorite.cfg.annotations.defaults.CfgStringDefault;

import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.HashMapTemplate;
import pl.north93.zgame.skyblock.api.HomeLocation;

@CfgComment("Konfiguracja SkyBlocka")
public class SkyBlockConfig
{
    @CfgComment("Czy na dole wyspy generować wełne oznaczajaca teren wyspy")
    @CfgBooleanDefault(false)
    private Boolean             placeDebugWool;

    @CfgComment("Czas w ms jaki gracz musi odczekac przed ponownym stworzeniem wyspy/regeneracja. Liczony od utworzenia/regeneracji")
    @CfgLongDefault(60 * 60 * 1000)
    private Long                islandGenerateCooldown;

    @CfgComment("Nazwa grupy serwerów z poczekalniami.")
    @CfgStringDefault("default")
    private String              lobbyServersGroup;

    @CfgComment("Unikalne identyfikatory serwerów używanych jako hosty SkyBlocka")
    @CfgDelegateDefault("getDefaultSkyBlockServers")
    @CfgCollectionStyle(ALWAYS_NEW_LINE)
    private List<String>        skyBlockServers;

    @CfgDelegateDefault("getDefaultIslandTypes")
    private List<IslandConfig>  islandTypes;

    @CfgComment("Punkty naliczane za bloki. Jako nazwe bloku podawac ta z Material.java")
    @CfgDelegateDefault("getDefaultBlockValues")
    @MsgPackCustomTemplate(HashMapTemplate.class)
    private Map<String, Double> blockValues;

    private static List<IslandConfig> getDefaultIslandTypes()
    {
        //noinspection ArraysAsListWithZeroOrOneArgument
        return Arrays.asList(new IslandConfig("Testowa", "nazwaPlikuWFolderzeSchematics", new HomeLocation(10, 10, 10, 0, 0), 50, 16));
    }

    private static List<String> getDefaultSkyBlockServers()
    {
        //noinspection ArraysAsListWithZeroOrOneArgument
        return Arrays.asList("a89498a5-4571-41ab-a471-d78e325aeaba");
    }

    private static Map<String, Double> getDefaultBlockValues()
    {
        final Map<String, Double> map = new HashMap<>();
        map.put("DEFAULT", 1.0);
        map.put("GRASS", 2.5);
        map.put("DIRT", 1.5);
        map.put("STONE:1", 0.95);
        return map;
    }

    public Boolean getPlaceDebugWool()
    {
        return this.placeDebugWool;
    }

    public Long getIslandGenerateCooldown()
    {
        return this.islandGenerateCooldown;
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

    public Map<String, Double> getBlockValues()
    {
        return this.blockValues;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("placeDebugWool", this.placeDebugWool).append("islandGenerateCooldown", this.islandGenerateCooldown).append("lobbyServersGroup", this.lobbyServersGroup).append("skyBlockServers", this.skyBlockServers).append("islandTypes", this.islandTypes).toString();
    }
}
