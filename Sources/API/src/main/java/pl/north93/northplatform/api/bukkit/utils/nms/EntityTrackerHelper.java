package pl.north93.northplatform.api.bukkit.utils.nms;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EntityTrackerEntry;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Player;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

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
     * Converts specified bukkit's Entity instance into Entity from nms.
     * Takes care about handling INorthPlayer.
     *
     * @param bukkitEntity Bukkit's object that represents an entity.
     * @return NMS's object that represents an entity.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Entity> T toNmsEntity(final org.bukkit.entity.Entity bukkitEntity)
    {
        if (bukkitEntity instanceof Player)
        {
            return (T) INorthPlayer.asCraftPlayer((Player) bukkitEntity).getHandle();
        }
        
        final CraftEntity craftEntity = (CraftEntity) bukkitEntity;
        return (T) craftEntity.getHandle();
    }

    /**
     * Converts specified bukkit's Player instance into EntityPlayer from nms.
     * Takes care about handling INorthPlayer.
     *
     * @param bukkitPlayer Bukkit's object that represents a player.
     * @return NMS's object that represents a player.
     */
    public static EntityPlayer toNmsPlayer(final Player bukkitPlayer)
    {
        return INorthPlayer.asCraftPlayer(bukkitPlayer).getHandle();
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
}
