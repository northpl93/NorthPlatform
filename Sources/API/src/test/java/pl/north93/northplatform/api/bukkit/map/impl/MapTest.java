package pl.north93.northplatform.api.bukkit.map.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MapTest
{
    @Test
    public void mapIdCounterShouldReturnProperIds()
    {
        final PlayerMapData playerMapData = new PlayerMapData(null);

        final MapImpl map1 = Mockito.mock(MapImpl.class);
        final int map1Id = playerMapData.getMapId(map1);

        assertEquals(1, map1Id);
        assertEquals(map1Id, playerMapData.getMapId(map1));

        final MapImpl map2 = Mockito.mock(MapImpl.class);
        final int map2Id = playerMapData.getMapId(map2);

        assertEquals(2, map2Id);
        assertEquals(map2Id, playerMapData.getMapId(map2));
    }
}
