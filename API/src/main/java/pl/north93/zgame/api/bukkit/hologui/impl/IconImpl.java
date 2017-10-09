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
import pl.north93.zgame.api.bukkit.utils.hologram.IHologram;
import pl.north93.zgame.api.bukkit.utils.hologram.PlayerVisibility;
import pl.north93.zgame.api.bukkit.utils.hologram.TranslatableStringLine;
import pl.north93.zgame.api.global.messages.TranslatableString;

public class IconImpl implements IIcon
{
    private final HoloContextImpl holoContext;
    private IconPosition       position;
    private ItemStack          itemStack;
    private TranslatableString name;
    // dane implementacyjne
    private ArmorStand         armorStand;
    private IHologram          hologram;

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
        if (this.armorStand == null)
        {
            // nic nie musimy aktualizowac
            return;
        }

        // przelicza docelowy punkt.
        final Location location = this.position.calculateTarget(this.holoContext.getCenter());

        if (this.itemStack != null)
        {
            final Location result = ArmorStandLocationFixer.INSTANCE.fixLocation(location, this.itemStack);
            location.setY(result.getY());
        }

        final CraftArmorStand armorStand = (CraftArmorStand) this.armorStand;
        armorStand.getHandle().setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
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
        if (this.armorStand == null)
        {
            return;
        }

        if (this.name != null)
        {
            if (this.hologram == null)
            {
                final PlayerVisibility hologramVisibility = new PlayerVisibility(this.holoContext.getPlayer());
                this.hologram = IHologram.create(hologramVisibility, this.armorStand.getLocation());
            }

            this.hologram.setLine(0, new TranslatableStringLine(this.name));
        }
        else if (this.hologram != null)
        {
            this.hologram.remove();
            this.hologram = null;
        }
    }

    /**
     * Tworzy armorstanda, konfiguruje go i przypisuje do tej instancji
     * ikony.
     *
     * @return Stworzona instancja ArmorStanda.
     */
    public ArmorStand create()
    {
        Preconditions.checkState(this.armorStand == null, "Icon already created");

        final World world = this.holoContext.getCenter().getWorld();
        final WorldServer nmsWorld = ((CraftWorld) world).getHandle();

        final EntityArmorStand entityArmorStand = new EntityArmorStand(nmsWorld);
        this.armorStand = (ArmorStand) entityArmorStand.getBukkitEntity();

        // ustawia poczatkowa lokacje ArmorStanda
        this.refreshLocation();

        // aktualizuje typ itemu
        this.updateType();

        // aktualizuje nazwe
        this.updateName();

        this.armorStand.setAI(false);
        this.armorStand.setGravity(false);
        this.armorStand.setVisible(false);
        this.armorStand.setSmall(true);

        nmsWorld.addEntity(entityArmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return this.armorStand;
    }

    public void destroy()
    {
        Preconditions.checkState(this.armorStand != null, "Icon is already destroyed or never created");

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
