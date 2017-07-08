package pl.arieals.api.minigame.server.gamehost.deathmatch;

import static org.bukkit.block.BlockFace.EAST;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.NORTH_EAST;
import static org.bukkit.block.BlockFace.NORTH_WEST;
import static org.bukkit.block.BlockFace.SOUTH;
import static org.bukkit.block.BlockFace.SOUTH_EAST;
import static org.bukkit.block.BlockFace.SOUTH_WEST;
import static org.bukkit.block.BlockFace.WEST;


import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BlockWalker
{
    private static final BlockFace[] facesN = new BlockFace[]{EAST, NORTH, WEST, SOUTH, SOUTH_WEST};
    private static final BlockFace[] facesW = new BlockFace[]{NORTH, WEST, SOUTH, EAST, SOUTH_EAST, /*Usuwanie ostatniego bloku*/NORTH_EAST};
    private static final BlockFace[] facesS = new BlockFace[]{WEST, SOUTH, EAST, NORTH, NORTH_EAST};
    private static final BlockFace[] facesE = new BlockFace[]{SOUTH, EAST, NORTH, WEST, NORTH_WEST};
    private final Location  current;
    private       BlockFace currentFace = WEST;

    public BlockWalker(final Location current)
    {
        this.current = current;
    }

    private BlockFace[] facesToCheck()
    {
        switch (this.currentFace)
        {
            case NORTH:
                return facesN;
            case SOUTH_WEST:
                return facesW;

            case WEST:
                return facesW;
            case SOUTH_EAST:
                return facesW;

            case SOUTH:
                return facesS;
            case NORTH_EAST:
                return facesS;

            case EAST:
                return facesE;
            case NORTH_WEST:
                return facesE;
        }
        throw new AssertionError();
    }

    public Block next()
    {
        final Block currentBlock = this.current.getBlock();
        for (final BlockFace face : this.facesToCheck())
        {
            final Block relative = currentBlock.getRelative(face);

            if (! relative.isEmpty())
            {
                relative.getLocation(this.current);
                this.currentFace = face;
                return relative;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("current", this.current).append("currentFace", this.currentFace).toString();
    }
}
