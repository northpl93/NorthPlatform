package pl.north93.zgame.skyblock.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.Document;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.data.StorageConnector;

public class IslandDao
{
    @InjectComponent("API.Database.StorageConnector")
    private StorageConnector storage;

    public Island convert(final Document doc)
    {
        return new Island(); // todo
    }

    public Document convert(final Island island)
    {
        return new Document();
    }

    public List<Island> getAllIslands()
    {
        return new ArrayList<>(); // todo
    }

    public List<Island> getAllIsland(final UUID serverId, final String islandType)
    {
        return new ArrayList<>(); // todo
    }
}
