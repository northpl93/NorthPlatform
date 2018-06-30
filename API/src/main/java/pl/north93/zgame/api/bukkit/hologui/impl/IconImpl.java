package pl.north93.zgame.api.bukkit.hologui.impl;

import java.util.Collections;
import java.util.Set;

import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.WorldServer;

import com.google.common.base.Preconditions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.entityhider.EntityVisibility;
import pl.north93.zgame.api.bukkit.entityhider.IEntityHider;
import pl.north93.zgame.api.bukkit.hologui.IIcon;
import pl.north93.zgame.api.bukkit.hologui.IconNameLocation;
import pl.north93.zgame.api.bukkit.hologui.IconPosition;
import pl.north93.zgame.api.bukkit.hologui.hologram.IHologram;
import pl.north93.zgame.api.bukkit.hologui.hologram.PlayerVisibility;
import pl.north93.zgame.api.bukkit.hologui.hologram.impl.HologramFactory;
import pl.north93.zgame.api.bukkit.hologui.hologram.message.LegacyHologramLines;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.TranslatableString;

class IconImpl implements IIcon
{
    @Inject
    private static IEntityHider         entityHider;
    private final  HoloContextImpl      holoContext;
    private        IconPosition         position;
    private        ItemStack            itemStack;
    private        IconNameLocation     iconNameLocation;
    private        boolean              small;
    private        TranslatableString[] name;
    // dane implementacyjne
    private        ArmorStand           armorStand;
    private        IHologram            hologram;

    public IconImpl(final HoloContextImpl holoContext)
    {
        this.holoContext = holoContext;
        this.iconNameLocation = IconNameLocation.BELOW;
        this.small = true; // by default
    }

    @Override
    public HoloContextImpl getHoloContext()
    {
        return this.holoContext;
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

    @Override
    public boolean isSmall()
    {
        return this.small;
    }

    @Override
    public void setSmall(final boolean small)
    {
        this.small = small;
        this.updateSize();
    }

    @Override
    public void setNameLocation(final IconNameLocation location)
    {
        this.iconNameLocation = location;
        this.updateName();
    }

    @Override
    public TranslatableString[] getDisplayName()
    {
        return this.name;
    }

    @Override
    public void setDisplayName(final TranslatableString... name)
    {
        this.name = name;
        this.updateName();
    }

    @Override
    public ArmorStand getBackingArmorStand()
    {
        return this.armorStand;
    }

    // aktualizuje item na glowie
    private void updateType()
    {
        if (this.armorStand != null)
        {
            this.armorStand.setHelmet(this.itemStack);
        }
    }

    // aktualizuje lokalizacje armorstandu
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

    // aktualizuje parametr small
    private void updateSize()
    {
        if (this.armorStand == null)
        {
            // nic nie musimy aktualizowac
            return;
        }

        this.armorStand.setSmall(this.small);
    }

    // aktualizuje hologram z nazwa
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
                final Location location = this.iconNameLocation.calculate(this);

                this.hologram = HologramFactory.create(hologramVisibility, location);
            }

            this.hologram.setMessage(new LegacyHologramLines(this.name));
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

        // ustawiamy widocznosc
        final Set<Entity> entity = Collections.singleton(this.armorStand);
        entityHider.setVisibility(this.holoContext.getPlayer(), EntityVisibility.VISIBLE, entity);
        entityHider.setVisibility(EntityVisibility.HIDDEN, entity);

        // konfigurujemy armorstand
        this.setupArmorStand();
        this.updateSize();

        // ustawia poczatkowa lokacje ArmorStanda
        this.refreshLocation();

        // aktualizuje typ itemu
        this.updateType();

        // aktualizuje nazwe
        this.updateName();

        nmsWorld.addEntity(entityArmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return this.armorStand;
    }

    public void destroy()
    {
        Preconditions.checkState(this.armorStand != null, "Icon is already destroyed or never created");

        final CraftArmorStand craftEntity = (CraftArmorStand) this.armorStand;
        craftEntity.getHandle().die();
        this.armorStand = null;

        if (this.hologram != null)
        {
            // usuwamy hologram jesli byl
            this.hologram.remove();
            this.hologram = null;
        }
    }

    /**
     * Sprawdza czy dana lokalizacja wskazuje na ta ikone.
     * https://www.spigotmc.org/threads/check-what-entity-a-player-is-looking-at.46715/#post-527815
     *
     * @param location Lokalizacja do sprawdzenia.
     * @return true jesli jest wskazywana ta ikona.
     */
    public boolean isLookingAt(final Location location)
    {
        final Location armorStandLocation = this.armorStand.getLocation();

        // przesuwamy troche centralny punkt w dol jesli armorstand jest maly
        if (this.small)
        {
            armorStandLocation.add(0, -0.4, 0);
        }

        final Vector toEntity = armorStandLocation.toVector().subtract(location.toVector());
        final Vector direction = location.getDirection();

        final double dot = toEntity.normalize().dot(direction);
        // wartosc wyznaczona metoda prob i bledow
        // im blizej 1 tym mniejszy kat miedzy graczem a itemem
        return dot >= 0.995;
    }

    // konfiguruje podstawowe opcje armor standa
    private void setupArmorStand()
    {
        this.armorStand.setAI(false);
        this.armorStand.setGravity(false);
        this.armorStand.setVisible(false);
        this.armorStand.setMarker(true);
        this.armorStand.setSilent(true);
        this.armorStand.setInvulnerable(true);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("position", this.position).append("itemStack", this.itemStack).append("iconNameLocation", this.iconNameLocation).append("name", this.name).toString();
    }
}
