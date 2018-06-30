package pl.north93.zgame.api.bukkit.hologui.hologram.impl;

import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/*default*/ final class HologramArmorStand extends EntityArmorStand
{
    private final HoloLine holoLine;

    public HologramArmorStand(final World world, final HoloLine holoLine)
    {
        super(world);
        this.holoLine = holoLine;
    }

    public HologramArmorStand(final World world, final double x, final double y, final double z, final HoloLine holoLine)
    {
        this(world, holoLine);
        this.setPosition(x, y, z);
    }

    public HoloLine getHoloLine()
    {
        return this.holoLine;
    }

    @Override
    public boolean c(final NBTTagCompound nbttagcompound)
    {
        // metoda zapisująca entity do NBTTaga, BEZ sprawdzania passengera
        // false oznacza ze compound nie zostanie dodany do swiata, a wiec my tak chcemy
        return false;
    }

    @Override
    public boolean d(final NBTTagCompound nbttagcompound)
    {
        // metoda zapisująca entity do NBTTaga, Z sprawdzaniem passengera
        return false;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("holoLine", this.holoLine).toString();
    }
}