package pl.north93.zgame.datashare.sharedimpl.basemcdata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.server.v1_10_R1.EntityLiving;
import net.minecraft.server.v1_10_R1.InventoryEnderChest;
import net.minecraft.server.v1_10_R1.MobEffect;
import net.minecraft.server.v1_10_R1.MobEffectList;
import net.minecraft.server.v1_10_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.NBTTagList;
import net.minecraft.server.v1_10_R1.ServerStatisticManager;
import net.minecraft.server.v1_10_R1.Statistic;
import net.minecraft.server.v1_10_R1.StatisticManager;
import net.minecraft.server.v1_10_R1.StatisticWrapper;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.component.annotations.SkipInjections;

@SkipInjections // this class causes InternalError o_O sun.instrument.InstrumentationImpl/retransformClasses0@-2
public final class VersionDepend
{
    public static byte[] serializePlayerInventory(final Player player)
    {
        final NBTTagList tagList = new NBTTagList();
        ((CraftInventoryPlayer) player.getInventory()).getInventory().a(tagList);
        final NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.set("", tagList);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try
        {
            NBTCompressedStreamTools.a(tagCompound, outputStream);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    public static void deserializePlayerInventory(final Player player, final byte[] inventory)
    {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(inventory);
        final NBTTagCompound compound;
        try
        {
             compound = NBTCompressedStreamTools.a(inputStream);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            return;
        }

        final NBTTagList tagList = (NBTTagList) compound.get("");
        ((CraftInventoryPlayer) player.getInventory()).getInventory().b(tagList);
    }

    public static byte[] serializePlayerEnderchest(final Player player)
    {
        final NBTTagList tagList = ((InventoryEnderChest) ((CraftInventory) player.getEnderChest()).getInventory()).h();
        final NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.set("", tagList);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try
        {
            NBTCompressedStreamTools.a(tagCompound, outputStream);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    public static void deserializePlayerEnderchest(final Player player, final byte[] enderchest)
    {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(enderchest);
        final NBTTagCompound compound;
        try
        {
            compound = NBTCompressedStreamTools.a(inputStream);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            return;
        }

        final NBTTagList tagList = (NBTTagList) compound.get("");
        ((InventoryEnderChest) ((CraftInventory) player.getEnderChest()).getInventory()).a(tagList);
    }

    public static byte[] serializePlayerPotions(final Player player)
    {
        final EntityLiving entityLiving = ((CraftPlayer) player).getHandle();
        final Map<MobEffectList, MobEffect> effects = entityLiving.effects;

        if(! effects.isEmpty())
        {
            final NBTTagCompound compound = new NBTTagCompound();
            final NBTTagList nbttaglist = new NBTTagList();

            for (final MobEffect mobeffect : effects.values())
            {
                nbttaglist.add(mobeffect.a(new NBTTagCompound()));
            }
            compound.set("", nbttaglist);

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try
            {
                NBTCompressedStreamTools.a(compound, outputStream);
            }
            catch (final IOException e)
            {
                e.printStackTrace();
            }

            return outputStream.toByteArray();
        }
        else
        {
            return new byte[0];
        }
    }

    public static void deserializePlayerPotions(final Player player, final byte[] potions)
    {
        final EntityLiving entityLiving = ((CraftPlayer) player).getHandle();
        entityLiving.removeAllEffects();
        if (potions.length == 0)
        {
            return;
        }

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(potions);
        final NBTTagCompound compound;
        try
        {
            compound = NBTCompressedStreamTools.a(inputStream);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            return;
        }

        final NBTTagList tagList = (NBTTagList) compound.get("");
        for (int i = 0; i < tagList.size(); ++i)
        {
            final MobEffect mobeffect = MobEffect.b(tagList.get(i));
            if (mobeffect != null)
            {
                entityLiving.addEffect(mobeffect);
            }
        }
    }

    private static final Field StatisticManager_a;
    static
    {
        try
        {
            StatisticManager_a = StatisticManager.class.getDeclaredField("a");
            StatisticManager_a.setAccessible(true);
        }
        catch (final NoSuchFieldException e)
        {
            throw new InternalError(e);
        }
    }

    public static String serializePlayerStatistics(final Player player)
    {
        final ServerStatisticManager statisticManager = ((CraftPlayer) player).getHandle().getStatisticManager();
        final String statistics;
        try
        {
            //noinspection unchecked
            statistics = ServerStatisticManager.a((Map<Statistic, StatisticWrapper>) StatisticManager_a.get(statisticManager));
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }

        return statistics;
    }

    public static void deserializePlayerStatistics(final Player player, final String statistics)
    {
        final ServerStatisticManager statisticManager = ((CraftPlayer) player).getHandle().getStatisticManager();
        final Map<Statistic, StatisticWrapper> statsMap;
        try
        {
            //noinspection unchecked
            statsMap = (Map<Statistic, StatisticWrapper>) StatisticManager_a.get(statisticManager);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }

        statsMap.clear();
        statsMap.putAll(statisticManager.a(statistics));
    }
}
