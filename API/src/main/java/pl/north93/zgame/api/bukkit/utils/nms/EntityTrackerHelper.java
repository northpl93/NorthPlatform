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
