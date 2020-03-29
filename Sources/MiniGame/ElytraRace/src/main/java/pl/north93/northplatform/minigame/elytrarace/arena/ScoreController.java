package pl.north93.northplatform.minigame.elytrarace.arena;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.EntityFallingBlock;
import net.minecraft.server.v1_12_R1.EnumSkyBlock;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.entityhider.IEntityHider;
import pl.north93.northplatform.api.bukkit.utils.nms.FastBlockOp;
import pl.north93.northplatform.api.bukkit.utils.nms.NorthFallingBlock;
import pl.north93.northplatform.api.bukkit.utils.region.Cuboid;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.minigame.elytrarace.cfg.Score;

public class ScoreController
{
    @Inject
    private IEntityHider entityHider;
    private final LocalArena   arena;
    private final Score score; // score point associated with this controller
    private final List<Entity> normalBlocks;
    private final List<Entity> grayedBlocks;

    public ScoreController(final LocalArena arena, final Score score)
    {
        this.arena = arena;
        this.score = score;
        this.normalBlocks = new ArrayList<>();
        this.grayedBlocks = new ArrayList<>();
    }

    public void setup()
    {
        final Cuboid cuboid = this.score.getArea().toCuboid(this.arena.getWorld().getCurrentWorld());

        final CraftWorld world = (CraftWorld) cuboid.getWorld();
        for (final Block block : cuboid)
        {
            final Material type = block.getType();
            if (type == Material.AIR || ! type.isSolid())
            {
                continue; // air or non-solid
            }

            {
                final EntityFallingBlock fallingBlock = NorthFallingBlock.createDerped(block.getLocation(), type, block.getData());
                world.addEntity(fallingBlock, CreatureSpawnEvent.SpawnReason.CUSTOM);

                this.normalBlocks.add(fallingBlock.getBukkitEntity());
            }

            {
                final EntityFallingBlock fallingBlock = NorthFallingBlock.createDerped(block.getLocation(), Material.WOOL, (byte) 7);
                world.addEntity(fallingBlock, CreatureSpawnEvent.SpawnReason.CUSTOM);

                this.grayedBlocks.add(fallingBlock.getBukkitEntity());
            }

            FastBlockOp.setType(block, Material.AIR, (byte)0);

            final Chunk chunk = ((CraftChunk) block.getChunk()).getHandle();
            // metoda ustawiajaca oswietlenie. Latwo znalezc bo przyjmuje EnumSkyBlock, lokacje i poziom swiatla
            chunk.a(EnumSkyBlock.BLOCK, new BlockPosition(block.getX(), block.getY(), block.getZ()), 15);
        }
    }

    public void makeGray(final Player player)
    {
        this.entityHider.hideEntities(player, this.normalBlocks);
        this.entityHider.showEntities(player, this.grayedBlocks);
    }

    public void makeNormal(final Player player)
    {
        this.entityHider.hideEntities(player, this.grayedBlocks);
        this.entityHider.showEntities(player, this.normalBlocks);
    }

    public void playBreakAnimation(final Player player)
    {
        for (final Entity entity : this.normalBlocks)
        {
            final FallingBlock block = (FallingBlock) entity;

            player.spawnParticle(Particle.BLOCK_CRACK, block.getLocation(), 10, block.getMaterial().getNewData(block.getBlockData()));
        }
    }

    public void cleanup()
    {
        this.normalBlocks.forEach(Entity::remove);
        this.normalBlocks.clear();
        this.grayedBlocks.forEach(Entity::remove);
        this.grayedBlocks.clear();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arena", this.arena).append("score", this.score).append("normalBlocks", this.normalBlocks).append("grayedBlocks", this.grayedBlocks).toString();
    }
}
