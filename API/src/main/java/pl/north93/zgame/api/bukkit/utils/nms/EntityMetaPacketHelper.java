package pl.north93.zgame.api.bukkit.utils.nms;

import net.minecraft.server.v1_10_R1.PacketDataSerializer;
import net.minecraft.server.v1_10_R1.Vector3f;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public class EntityMetaPacketHelper // klasa pomocnicza do pakietu PacketPlayOutEntityMetadata
{
    private final ByteBuf              buffer;
    private final PacketDataSerializer pds;

    public EntityMetaPacketHelper(final int entityId)
    {
        this.buffer = PooledByteBufAllocator.DEFAULT.buffer(32); // allocate new pooled bytebuf
        this.pds = new PacketDataSerializer(this.buffer);

        this.pds.writeByte(0x39); // PacketPlayOutEntityMetadata id PAMIETAC ZEBY TU ZMIENIC PRZY AKTUSLIZACJI MINECRAFTA
        this.pds.d(entityId); // writeVarInt
    }

    public void addMeta(final int metaId, final MetaType metaType, final Object value)
    {
        this.pds.writeByte(metaId);
        metaType.write(this.pds, value);
    }

    public ByteBuf complete()
    {
        this.pds.writeByte(0xff);
        return this.buffer;
    }

    public enum MetaType
    {
        VECTOR
                {
                    @Override
                    void write(final PacketDataSerializer serializer, final Object object)
                    {
                        final Vector3f vector = (Vector3f) object;

                        serializer.writeByte(7);
                        serializer.writeFloat(vector.getX());
                        serializer.writeFloat(vector.getY());
                        serializer.writeFloat(vector.getZ());
                    }
                },
        STRING
                {
                    @Override
                    void write(final PacketDataSerializer serializer, final Object object)
                    {
                        serializer.writeByte(3);
                        serializer.a((String) object);
                    }
                };


        abstract void write(PacketDataSerializer serializer, Object object);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
