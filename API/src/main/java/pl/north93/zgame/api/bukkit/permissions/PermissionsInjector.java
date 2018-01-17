package pl.north93.zgame.api.bukkit.permissions;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.ServerOperator;

public final class PermissionsInjector
{
    private static final Class<?> humanEntityClass = clazz("org.bukkit.craftbukkit.v1_12_R1.entity.CraftHumanEntity");
    private static final Field    permField;
    private static final Field    opableField;

    static
    {
        try
        {
            permField = humanEntityClass.getDeclaredField("perm");
            permField.setAccessible(true);

            final Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(permField, permField.getModifiers() & ~ Modifier.FINAL);

            opableField = PermissibleBase.class.getDeclaredField("opable");
            opableField.setAccessible(true);
        }
        catch (final Exception e)
        {
            throw new RuntimeException("Error in PermissionsInjector", e);
        }
    }

    private static Class<?> clazz(final String name)
    {
        try
        {
            return Class.forName(name);
        }
        catch (final ClassNotFoundException e)
        {
            throw new RuntimeException("Error in PermissionsInjector", e);
        }
    }

    public static void inject(final Player player)
    {
        try
        {
            final PermissibleBase oldPerm = (PermissibleBase) permField.get(player);
            final ServerOperator  operator = (ServerOperator) opableField.get(oldPerm);
            permField.set(player, new NorthPermissibleBase(oldPerm, operator));
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException("Error in PermissionsInjector", e);
        }
    }
}
