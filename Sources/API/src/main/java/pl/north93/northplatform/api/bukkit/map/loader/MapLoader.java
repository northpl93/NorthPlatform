package pl.north93.northplatform.api.bukkit.map.loader;

import javax.xml.bind.JAXB;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;

import pl.north93.northplatform.api.bukkit.map.loader.xml.MapConfig;
import pl.north93.northplatform.api.bukkit.map.loader.xml.MapsConfig;
import pl.north93.northplatform.api.bukkit.server.IWorldInitializer;
import pl.north93.northplatform.api.bukkit.map.IBoard;
import pl.north93.northplatform.api.bukkit.map.IMapManager;
import pl.north93.northplatform.api.bukkit.map.IMapRenderer;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

/**
 * Klasa odpowiadająca za automatyczne wczytywanie map-obrazów z konfiguracji
 * znajdującej się w katalogu świata podczas jego wczytywania.
 */
public class MapLoader implements IWorldInitializer
{
    @Inject
    private IMapManager mapManager;

    @Override
    public void initialiseWorld(final World world, final File directory)
    {
        final File xmlFile = new File(directory, "Maps.xml");
        if (! xmlFile.exists())
        {
            return;
        }

        final MapsConfig mapsConfig = JAXB.unmarshal(xmlFile, MapsConfig.class);
        for (final MapConfig mapConfig : mapsConfig.getMaps())
        {
            final Location left = mapConfig.getLeftCorner().toBukkit(world);
            final Location right = mapConfig.getRightCorner().toBukkit(world);

            // tworzymy nową tablicę na podstawie podanych lokalizacji rogów
            final IBoard board = this.mapManager.createBoard(left, right);

            // tworzymy renderer i ustawiamy go w danej tablicy
            final IMapRenderer renderer = mapConfig.createRenderer();
            board.setRenderer(renderer);
        }
    }
}
