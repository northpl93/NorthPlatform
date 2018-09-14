package pl.north93.zgame.api.bukkit.protocol.impl.emulation;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

import org.bukkit.block.Block;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BlockData
{
    private final int x;
    private final int y;
    private final int z;
    private final int oldId;
    private final String fixedId;

    public BlockData(final Block block, final String minecraftId, final TreeMap<String, String> properties)
    {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();

        // generujemy stare, zdeprecjonowane ID bloku na potrzeby ViaVersion
        this.oldId = this.generateOldId(block);

        this.fixedId = this.generateFixedId(minecraftId, properties);
    }

    public void writeToNbt(final NBTTagCompound nbt)
    {
        nbt.setString("id", "viaversion-helper");
        nbt.setInt("x", this.x);
        nbt.setInt("y", this.y);
        nbt.setInt("z", this.z);
        nbt.setInt("oldId", this.oldId);
        nbt.setString("fixedId", this.fixedId);
    }

    public boolean isStillValid(final Block block)
    {
        return this.generateOldId(block) == this.oldId;
    }

    private int generateOldId(final Block block)
    {
        return block.getTypeId() << 4 | block.getData() & 0xF;
    }

    private String generateFixedId(final String minecraftId, final TreeMap<String, String> properties)
    {
        if (properties.isEmpty())
        {
            return minecraftId;
        }

        final StringBuilder builder = new StringBuilder(minecraftId);
        builder.append('[');

        final Iterator<Map.Entry<String, String>> iterator = properties.entrySet().iterator();
        while (iterator.hasNext())
        {
            final Map.Entry<String, String> entry = iterator.next();

            builder.append(entry.getKey());
            builder.append('=');
            builder.append(entry.getValue());

            if (iterator.hasNext())
            {
                builder.append(',');
            }
        }

        builder.append(']');
        return builder.toString();
    }
}
