package pl.north93.zgame.api.bukkit.protocol.impl.emulation;

import java.util.TreeMap;
import java.util.stream.Collectors;

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
        this.oldId = block.getTypeId() << 4 | block.getData() & 0xF;

        final StringBuilder builder = new StringBuilder();
        builder.append(minecraftId);
        if (properties.isEmpty())
        {
            this.fixedId = builder.toString();
            return;
        }

        builder.append("[");
        final String serialisedProperties = properties.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(","));
        builder.append(serialisedProperties);
        builder.append("]");

        this.fixedId = builder.toString();
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
}
