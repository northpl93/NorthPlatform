package pl.north93.zgame.skyblock.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.ArrayListTemplate;
import pl.north93.zgame.skyblock.api.utils.Coords2D;

/**
 * Klasa przechowująca informacje o wyspie.
 */
public final class IslandData
{
    private UUID         islandId;
    private UUID         ownerId;
    private UUID         serverId;
    private String       islandType;
    private String       name;
    private Boolean      acceptingVisits;
    private Coords2D     islandLocation;
    private HomeLocation homeLocation;
    private NorthBiome   biome;
    @MsgPackCustomTemplate(ArrayListTemplate.class)
    private List<UUID>   invitations;
    @MsgPackCustomTemplate(ArrayListTemplate.class)
    private List<UUID>   membersUuid;

    public IslandData()
    {
        this.membersUuid = new ArrayList<>(0);
        this.invitations = new ArrayList<>(0);
    }

    public UUID getIslandId()
    {
        return this.islandId;
    }

    public void setIslandId(final UUID islandId)
    {
        this.islandId = islandId;
    }

    public UUID getOwnerId()
    {
        return this.ownerId;
    }

    public void setOwnerId(final UUID ownerId)
    {
        this.ownerId = ownerId;
    }

    public UUID getServerId()
    {
        return this.serverId;
    }

    public void setServerId(final UUID serverId)
    {
        this.serverId = serverId;
    }

    public String getIslandType()
    {
        return this.islandType;
    }

    public void setIslandType(final String islandType)
    {
        this.islandType = islandType;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public Boolean getAcceptingVisits()
    {
        return this.acceptingVisits;
    }

    public void setAcceptingVisits(final Boolean acceptingVisits)
    {
        this.acceptingVisits = acceptingVisits;
    }

    public Coords2D getIslandLocation()
    {
        return this.islandLocation;
    }

    public void setIslandLocation(final Coords2D islandLocation)
    {
        this.islandLocation = islandLocation;
    }

    public HomeLocation getHomeLocation()
    {
        return this.homeLocation;
    }

    public void setHomeLocation(final HomeLocation homeLocation)
    {
        this.homeLocation = homeLocation;
    }

    public NorthBiome getBiome()
    {
        return this.biome;
    }

    public void setBiome(final NorthBiome biome)
    {
        this.biome = biome;
    }

    public List<UUID> getInvitations()
    {
        return this.invitations;
    }

    public void setInvitations(final List<UUID> invitations)
    {
        this.invitations = invitations;
    }

    public void setMembers(final List<UUID> membersUuid)
    {
        this.membersUuid = membersUuid;
    }

    public List<UUID> getMembersUuid()
    {
        return Lists.newCopyOnWriteArrayList(this.membersUuid);
    }

    public void addMember(final UUID memberUuid)
    {
        this.membersUuid.add(memberUuid);
    }

    public void removeMember(final UUID memberUuid)
    {
        this.membersUuid.remove(memberUuid);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("islandId", this.islandId).append("ownerId", this.ownerId).append("serverId", this.serverId).append("islandType", this.islandType).append("name", this.name).append("acceptingVisits", this.acceptingVisits).append("islandLocation", this.islandLocation).append("homeLocation", this.homeLocation).append("biome", this.biome).append("invitations", this.invitations).append("membersUuid", this.membersUuid).toString();
    }
}
