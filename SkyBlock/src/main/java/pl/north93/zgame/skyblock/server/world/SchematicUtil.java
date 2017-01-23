package pl.north93.zgame.skyblock.server.world;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.WorldData;

import org.bukkit.Location;

public class SchematicUtil
{
    private static final WorldEdit API = WorldEdit.getInstance();

    public static void pasteSchematic(final Location location, final File schematic, final String formatName)
    {
        final World world = new BukkitWorld(location.getWorld());
        final WorldData worldData = world.getWorldData();
        final EditSession editSession = API.getEditSessionFactory().getEditSession(world, Integer.MAX_VALUE);

        final ClipboardHolder clipboardHolder;
        try (final FileInputStream fis = new FileInputStream(schematic); final BufferedInputStream bis = new BufferedInputStream(fis))
        {
            final ClipboardReader reader = ClipboardFormat.findByAlias(formatName).getReader(bis);
            clipboardHolder = new ClipboardHolder(reader.read(worldData), worldData);
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Failed to paste island", e);
        }

        final Vector to = new Vector(location.getX(), location.getY(), location.getZ());
        final Operation operation = clipboardHolder.createPaste(editSession, worldData).to(to).ignoreAirBlocks(true).build();
        try
        {
            Operations.completeLegacy(operation);
        }
        catch (final MaxChangedBlocksException e)
        {
            throw new RuntimeException("Failed to paste island", e);
        }
    }
}
