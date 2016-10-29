package pl.north93.robbermod.data;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class RobberDataProvider implements ICapabilitySerializable<NBTBase>
{
    @CapabilityInject(IRobberData.class)
    public static final Capability<IRobberData> DATA_CAP = null;
    private IRobberData instance = DATA_CAP.getDefaultInstance();

    @Override
    public boolean hasCapability(final Capability<?> capability, @Nullable final EnumFacing facing)
    {
        return capability == DATA_CAP;
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, @Nullable final EnumFacing facing)
    {
        return capability == DATA_CAP ? DATA_CAP.<T> cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT()
    {
        return DATA_CAP.getStorage().writeNBT(DATA_CAP, this.instance, null);
    }

    @Override
    public void deserializeNBT(final NBTBase nbt)
    {
        DATA_CAP.getStorage().readNBT(DATA_CAP, this.instance, null, nbt);
    }
}
