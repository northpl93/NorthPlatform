package pl.arieals.lobby.chest.opening;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.zgame.api.bukkit.utils.xml.XmlLocation;

@XmlRootElement(name = "hubOpening")
@XmlAccessorType(XmlAccessType.FIELD)
public final class HubOpeningConfig
{
    @XmlElement
    private String      chestType;
    @XmlElement
    private XmlLocation playerLocation;
    @XmlElement
    private XmlLocation chestLocation;

    public String getChestType()
    {
        return this.chestType;
    }

    public XmlLocation getPlayerLocation()
    {
        return this.playerLocation;
    }

    public XmlLocation getChestLocation()
    {
        return this.chestLocation;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("chestType", this.chestType).append("playerLocation", this.playerLocation).append("chestLocation", this.chestLocation).toString();
    }
}

/**
 * Prosta klasa trzymajaca cache configów.
 */
class HubOpeningConfigCache
{
    public static final HubOpeningConfigCache INSTANCE = new HubOpeningConfigCache();
    private final Map<HubWorld, HubOpeningConfig> hubs = new HashMap<>();

    /**
     * Zwraca zcachowany config otwierania skrzynek dla podanego huba.
     *
     * @param hubWorld Hub dla ktorego pobieramy config.
     * @return Konfiguracja otwierania skrzynek.
     */
    public HubOpeningConfig getConfig(final HubWorld hubWorld)
    {
        return this.hubs.computeIfAbsent(hubWorld, _hubWorld ->
        {
            final File hubOpeningFile = new File(hubWorld.getBukkitWorld().getWorldFolder(), "Hub.Opening.xml");
            if (! hubOpeningFile.exists())
            {
                throw new RuntimeException("Hub.Opening.xml doesn't exists in " + hubOpeningFile.getParent());
            }
            Bukkit.broadcastMessage(hubOpeningFile.getAbsolutePath() + " HubOpeningConfigCache");
            return JAXB.unmarshal(hubOpeningFile, HubOpeningConfig.class);
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("hubs", this.hubs).toString();
    }
}