package pl.north93.zgame.api.bukkit.entityhider.impl;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

import net.minecraft.server.v1_10_R1.Entity;
import net.minecraft.server.v1_10_R1.EntityTrackerEntry;

class EntityTrackerHelper
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

    /*default*/ static EntityTrackerEntry getTrackerEntry(final Entity entity)
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
}
