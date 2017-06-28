package pl.north93.zgame.api.bukkit.utils.nms;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

import net.minecraft.server.v1_10_R1.Entity;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.EntityTrackerEntry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class EntityTrackerHelper
{
    private static final MethodHandle entity_field_tracker;
    static
    {
        try
        {
            final Field tracker = Entity.class.getDeclaredField("tracker");
            tracker.setAccessible(true);
            entity_field_tracker = MethodHandles.lookup().unreflectGetter(tracker).asType(MethodType.methodType(EntityTrackerEntry.class, Entity.class));
        }
        catch (final NoSuchFieldException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Klasa EntityTrackerEntry sluzy do sledzenia danego entity przez liste
     * graczy bedacych w jego zasiegu. Zarzadza wysylaniem pakietow z
     * informacjami o danym entity.
     *
     * @param entity Entity z ktorego wyciagnac EntityTrackerEntry.
     * @return EntityTrackerEntry dla danego entity.
     */
    public static EntityTrackerEntry getTrackerEntry(final Entity entity)
    {
        try
        {
            return (EntityTrackerEntry) entity_field_tracker.invokeExact(entity);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    /**
     * Umozliwia proste sledzenia rozpoczecia i skonczenia trackowania
     * danego entity przez gracza.
     * Umozliwia to wykrywanie gdy gracz wchodzi w zasieg widzenia
     * danego entity.
     *
     * @param entity entity z ktorego zrobic obserwowalna mape.
     * @return Obserwowalna mapa z lista graczy trackujacych dane entity
     */
    public static ObservableMap<EntityPlayer, Boolean> observeTracker(final Entity entity)
    {
        final EntityTrackerEntry trackerEntry = getTrackerEntry(entity);
        if (trackerEntry.trackedPlayerMap instanceof ObservableMap)
        {
            return (ObservableMap<EntityPlayer, Boolean>) trackerEntry.trackedPlayerMap;
        }

        final ObservableMap<EntityPlayer, Boolean> observableMap = FXCollections.observableMap(trackerEntry.trackedPlayerMap);
        trackerEntry.trackedPlayerMap = observableMap;

        return observableMap;
    }
}
