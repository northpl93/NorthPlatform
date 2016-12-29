package pl.north93.zgame.skyblock.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.ArrayListTemplate;
import pl.north93.zgame.skyblock.api.utils.Coords2D;
import pl.north93.zgame.skyblock.api.utils.Coords3D;

/**
 * Klasa przechowująca informacje o wyspie.
 */
public final class IslandData
{
    private UUID       islandId;
    private UUID       ownerId;
    private UUID       serverId;
    private String     islandType;
    private String     name;
    private Coords2D   islandLocation;
    private Coords3D   homeLocation;
    @MsgPackCustomTemplate(ArrayListTemplate.class)
    private List<UUID> membersUuid;

    public IslandData()
    {
        this.membersUuid = new ArrayList<>(0);
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

    public Coords2D getIslandLocation()
    {
        return this.islandLocation;
    }

    public void setIslandLocation(final Coords2D islandLocation)
    {
        this.islandLocation = islandLocation;
    }

    public Coords3D getHomeLocation()
    {
        return this.homeLocation;
    }

    public void setHomeLocation(final Coords3D homeLocation)
    {
        this.homeLocation = homeLocation;
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
}
