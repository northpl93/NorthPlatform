package pl.arieals.minigame.bedwars.utils;

import static pl.arieals.minigame.bedwars.utils.DestroyBlockerTask.getMidPoint;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.EntityArmorStand;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.PacketPlayInArmAnimation;
import net.minecraft.server.v1_10_R1.PacketPlayInBlockDig;
import net.minecraft.server.v1_10_R1.PacketPlayInFlying;
import net.minecraft.server.v1_10_R1.PacketPlayInKeepAlive;
import net.minecraft.server.v1_10_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_10_R1.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_10_R1.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.packets.event.AsyncPacketInEvent;

public class DestroyBlockerTask implements Runnable, Listener
{
    private Map<Block, BlockDestroyEntry> blocks = new HashMap<>();

    //@Bean // wylaczamy, to tylko testy
    private DestroyBlockerTask(final BukkitApiCore apiCore)
    {
        Bukkit.getScheduler().runTaskTimer(apiCore.getPluginMain(), this, 1, 1);
        apiCore.registerEvents(this);
    }

    @Override
    public void run()
    {
        for (Iterator<BlockDestroyEntry> iterator = this.blocks.values().iterator(); iterator.hasNext();)
        {
            final BlockDestroyEntry destroyEntry = iterator.next();

            final Block block = destroyEntry.getBlock();
            if (block.getType() == Material.AIR)
            {
                Bukkit.broadcastMessage("block is air, invalidated");
                iterator.remove();
                if (destroyEntry.getArmorStand() != null)
                {
                    destroyEntry.getArmorStand().die();
                }
                continue;
            }

            /*if (System.currentTimeMillis() - destroyEntry.getLastHeartbeat() > 500)
            {
                Bukkit.broadcastMessage("heartbeat timed out, invalidated");
                iterator.remove();
                continue;
            }*/


            final long time = System.currentTimeMillis() - destroyEntry.getStartTime();

            final int progress = ((int)((time / 25_000D) * 10));
            if (progress > 9)
            {
                Bukkit.broadcastMessage("destroying completed, invalidating");
                iterator.remove();
                if (destroyEntry.getArmorStand() != null)
                {
                    destroyEntry.getArmorStand().die();
                }

                block.getDrops().forEach(itemStack -> block.getWorld().dropItemNaturally(block.getLocation(), itemStack));
                block.setType(Material.AIR);
                continue;
            }

            destroyEntry.updateProgress(progress);
        }
    }

    public static Location getMidPoint(Location p1, Location p2, double fraction)
    {
        System.out.println(fraction);
        return new Location(p1.getWorld(), p1.getX() + ((p2.getX() - p1.getX()) * fraction), p1.getY() + ((p2.getY() - p1.getY()) * fraction), p1.getZ() + ((p2.getZ() - p1.getZ()) * fraction));
    }

    @EventHandler
    public void onBlockDamage(final BlockDamageEvent event)
    {
        final BlockDestroyEntry destroyEntry = this.blocks.computeIfAbsent(event.getBlock(), block -> new BlockDestroyEntry(block, event.getPlayer()));
        destroyEntry.heartbeat();

        //event.setCancelled(true);
    }

    @EventHandler
    public void onArmorStandPacketHit(final AsyncPacketInEvent event)
    {
        if (event.getPacket() instanceof PacketPlayInArmAnimation)
        {
            /*for (final BlockDestroyEntry destroyEntry : this.blocks.values())
            {
                final Player playerBreaking = destroyEntry.getPlayer();

                if (event.getPlayer().equals(playerBreaking))
                {
                    destroyEntry.heartbeat();
                    return;
                }
            }*/
        }
        else if (event.getPacket() instanceof PacketPlayInBlockDig)
        {
            Bukkit.broadcastMessage("DIG:" + event.getPacket());
            Bukkit.broadcastMessage(((PacketPlayInBlockDig) event.getPacket()).c().name());
            for (final BlockDestroyEntry destroyEntry : this.blocks.values())
            {
                final Player playerBreaking = destroyEntry.getPlayer();

                if (event.getPlayer().equals(playerBreaking))
                {
                    destroyEntry.heartbeat();
                    return;
                }
            }
        }
        else if (! (event.getPacket() instanceof PacketPlayInFlying || event.getPacket() instanceof PacketPlayInKeepAlive) )
        {
            Bukkit.broadcastMessage(event.getPacket().toString());
        }
    }
}

class BlockDestroyEntry
{
    private final Block block;
    private final Player player;
    private final long startTime;
    private EntityArmorStand armorStand;
    private int armorStandTime;
    private       long lastHeartbeat;

    public BlockDestroyEntry(final Block block, final Player player)
    {
        this.block = block;
        this.player = player;
        this.startTime = System.currentTimeMillis();
    }

    public Block getBlock()
    {
        return this.block;
    }

    public Player getPlayer()
    {
        return this.player;
    }

    public long getStartTime()
    {
        return this.startTime;
    }

    public EntityArmorStand getArmorStand()
    {
        return this.armorStand;
    }

    public void setArmorStand(final EntityArmorStand armorStand)
    {
        this.armorStand = armorStand;
    }

    public long getLastHeartbeat()
    {
        return this.lastHeartbeat;
    }

    public void heartbeat()
    {
        this.lastHeartbeat = System.currentTimeMillis();
        Bukkit.broadcastMessage("received heartbeat");
    }

    public void updateProgress(final int progress)
    {
        Bukkit.broadcastMessage("updateProgress " + progress);

        final BlockPosition blockPosition = new BlockPosition(this.block.getX(), this.block.getY(), this.block.getZ());
        final PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(this.player.getEntityId(), blockPosition, progress);

        final WorldServer nmsWorld = ((CraftWorld) block.getWorld()).getHandle();

        this.armorStandTime++;

        // 1
        if (this.armorStand == null && this.armorStandTime >= 1)
        {
            final EntityArmorStand entityArmorStand = new EntityArmorStand(nmsWorld);
            this.armorStand = entityArmorStand;
            this.armorStandTime = 0;

            final Location armorStandLoc = getMidPoint(this.player.getEyeLocation(), block.getLocation(), 0.8);
            entityArmorStand.setLocation(armorStandLoc.getX() + 0.5, armorStandLoc.getY(), armorStandLoc.getZ() + 0.5, 0f, 0f);

            entityArmorStand.setInvisible(true);
            entityArmorStand.setNoGravity(true);

            nmsWorld.addEntity(entityArmorStand);
        }
        else if (this.armorStand != null && this.armorStandTime >= 20)
        {
            this.armorStand.die();
            this.armorStand = null;
            this.armorStandTime = 0;
        }

        // 2
        //if (this.armorStandTime > 5)
        //{
            //final EntityPlayer handle = ((CraftPlayer) player).getHandle();

            //final int heldItemSlot = player.getInventory().getHeldItemSlot();
            //player.getInventory().setHeldItemSlot(0);
            //player.getInventory().setHeldItemSlot(heldItemSlot);

            //final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
            //handle.playerConnection.sendPacket(new PacketPlayOutSetSlot(0, 36+player.getInventory().getHeldItemSlot(), CraftItemStack.asNMSCopy(itemInMainHand)));


            //this.armorStandTime = 0;
            //return;
        //}

        // 3
        //((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(new PacketPlayOutBlockChange(nmsWorld, blockPosition));

        ((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);
        final EntityPlayer handle = ((CraftPlayer) this.player).getHandle();
        final PacketPlayOutAnimation animation = new PacketPlayOutAnimation(handle, 0);
        handle.playerConnection.sendPacket(animation);
    }
}