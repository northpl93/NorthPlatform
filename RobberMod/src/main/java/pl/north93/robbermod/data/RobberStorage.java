package pl.north93.robbermod.data;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;

public class RobberStorage implements Capability.IStorage<IRobberData>
{
    @Override
    public NBTBase writeNBT(final Capability<IRobberData> capability, final IRobberData instance, final EnumFacing side)
    {
        return new NBTTagInt(instance.getRobberCount());
    }

    @Override
    public void readNBT(final Capability<IRobberData> capability, final IRobberData instance, final EnumFacing side, final NBTBase nbt)
    {
        instance.setRobberCount(((NBTPrimitive) nbt).getInt());
    }
}
