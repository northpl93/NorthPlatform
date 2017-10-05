package pl.north93.zgame.api.bukkit.hologui.impl;

import net.minecraft.server.v1_10_R1.EntityArmorStand;
import net.minecraft.server.v1_10_R1.WorldServer;

import com.google.common.base.Preconditions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import pl.north93.zgame.api.bukkit.hologui.IIcon;
import pl.north93.zgame.api.bukkit.hologui.IconPosition;
import pl.north93.zgame.api.global.messages.TranslatableString;

public class IconImpl implements IIcon
{
    private final HoloContextImpl holoContext;
    private IconPosition       position;
    private ItemStack          itemStack;
    private ArmorStand         armorStand;
    private TranslatableString name;

    public IconImpl(final HoloContextImpl holoContext)
    {
        this.holoContext = holoContext;
    }

    @Override
    public ItemStack getItem()
    {
        return this.itemStack;
    }

    @Override
    public void setType(final ItemStack stack)
    {
        Preconditions.checkNotNull(stack, "ItemStack can't be null");
        this.itemStack = stack;
        this.updateType();
    }

    private void updateType()
    {
        if (this.armorStand != null)
        {
            this.armorStand.setHelmet(this.itemStack);
        }
    }

    @Override
    public IconPosition getPosition()
    {
        return this.position;
    }

    @Override
    public void setPosition(final IconPosition position)
    {
        Preconditions.checkNotNull(position, "Position can't be null");
        this.position = position;
        this.refreshLocation();
    }

    public void refreshLocation()
    {
        if (this.armorStand != null)
        {
            final Location location = this.position.calculateTarget(this.holoContext.getCenter());
            this.armorStand.teleport(location);
        }
    }

    @Override
    public TranslatableString getDisplayName()
    {
        return this.name;
    }

    @Override
    public void setDisplayName(final TranslatableString name)
    {
        this.name = name;
        this.updateName();
    }

    private void updateName()
    {
        if (this.armorStand != null && this.name != null)
        {
            final String translatedName = this.name.getValue(this.holoContext.getPlayer());
            this.armorStand.setCustomName(translatedName);
            this.armorStand.setCustomNameVisible(true);
        }
    }

    public void create()
    {
        final World world = this.holoContext.getCenter().getWorld();
        final WorldServer nmsWorld = ((CraftWorld) world).getHandle();

        final EntityArmorStand entityArmorStand = new EntityArmorStand(nmsWorld);
        this.armorStand = (ArmorStand) entityArmorStand.getBukkitEntity();

        // ustawia poczatkowa lokacje ArmorStanda
        final Location location = this.position.calculateTarget(this.holoContext.getCenter());
        entityArmorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        // aktualizuje typ itemu
        this.updateType();

        // aktualizuje nazwe
        this.updateName();

        this.armorStand.setAI(false);
        this.armorStand.setGravity(false);
        this.armorStand.setVisible(false);
        this.armorStand.setSmall(true);

        nmsWorld.addEntity(entityArmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public void destroy()
    {
        if (this.armorStand == null)
        {
            return;
        }

        final CraftArmorStand craftEntity = (CraftArmorStand) this.armorStand;
        craftEntity.getHandle().die();
        this.armorStand = null;
    }

    /**
     * Sprawdza czy dane entity jest ta ikona.
     *
     * @param entity Entity do sprawdzenia.
     * @return True jesli entity z argumenty jest ta ikona.
     */
    public boolean isValidClick(final Entity entity)
    {
        return entity == this.armorStand;
    }
}
