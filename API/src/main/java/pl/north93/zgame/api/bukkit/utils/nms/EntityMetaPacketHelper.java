package pl.north93.zgame.api.bukkit.utils.nms;

import net.minecraft.server.v1_12_R1.PacketDataSerializer;
import net.minecraft.server.v1_12_R1.Vector3f;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

/**
 * Klasa pomocnicza sluzaca do recznego wysylania danych
 * zawartych w pakiecie PacketPlayOutEntityMetadata.
 */
public class EntityMetaPacketHelper
{
    private final ByteBuf              buffer;
    private final PacketDataSerializer pds;

    /**
     * Tworzy nowa instancje klasy i wprowadza do bufora
     * dane ID entity.
     *
     * @param entityId ID entity ktorego dotyczy ten pakiet.
     * @see Entity#getEntityId()
     */
    public EntityMetaPacketHelper(final int entityId)
    {
        this.buffer = UnpooledByteBufAllocator.DEFAULT.buffer(32);
        this.pds = new PacketDataSerializer(this.buffer);

        this.pds.writeByte(0x39); // PacketPlayOutEntityMetadata id PAMIETAC ZEBY TU ZMIENIC PRZY AKTUSLIZACJI MINECRAFTA
        this.pds.d(entityId); // writeVarInt
    }

    /**
     * Dodaje nowe metadane do tej instancji.
     * Informacje o metadanych najlepiej czerpac z
     * http://wiki.vg/Entities#Entity_Metadata_Format
     *
     * @param metaId ID danej metadata (Index na wiki.vg)
     * @param metaType Typ metadany (Type na wiki.vg)
     * @param value Wartosc ktora ustalamy (zgodna z typem)
     */
    public void addMeta(final int metaId, final MetaType metaType, final Object value)
    {
        this.pds.writeByte(metaId);
        metaType.write(this.pds, value);
    }

    /**
     * Zapisuje na koncu bufora informacje o koncu metadanych
     * i zwraca instancje ByteBufa ktora nalezy pozniej
     * zamknac!
     * Nalezy wywolywac ta metode tylko raz, inaczej uzyskamy
     * uszkodzony pakiet.
     *
     * @return Bufor z gotowym pakietem.
     * @see ByteBuf#release()
     */
    public ByteBuf complete()
    {
        this.pds.writeByte(0xff);
        return this.buffer;
    }

    public enum MetaType
    {
        STRING
                {
                    @Override
                    void write(final PacketDataSerializer serializer, final Object object)
                    {
                        serializer.writeByte(3);
                        serializer.a((String) object);
                    }
                },
        SLOT
                {
                    @Override
                    void write(final PacketDataSerializer serializer, final Object object)
                    {
                        serializer.writeByte(5);

                        final ItemStack bukkitStack = (ItemStack) object;
                        // a(n.m.s.ItemStack)
                        serializer.a(CraftItemStack.asNMSCopy(bukkitStack));
                    }
                },
        BOOLEAN
                {
                    @Override
                    void write(final PacketDataSerializer serializer, final Object object)
                    {
                        serializer.writeByte(6);
                        serializer.writeBoolean((Boolean) object);
                    }
                },
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
                };


        abstract void write(PacketDataSerializer serializer, Object object);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
