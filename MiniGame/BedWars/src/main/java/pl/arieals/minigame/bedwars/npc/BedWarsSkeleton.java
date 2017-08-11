package pl.arieals.minigame.bedwars.npc;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.Map;

import net.minecraft.server.v1_10_R1.Entity;
import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.EntitySkeleton;
import net.minecraft.server.v1_10_R1.EntityTypes;
import net.minecraft.server.v1_10_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_10_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_10_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_10_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_10_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_10_R1.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.CreatureSpawnEvent;

import org.diorite.utils.reflections.DioriteReflectionUtils;

import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;

public class BedWarsSkeleton extends EntitySkeleton
{
    private final Team owner;

    static
    {
        final Map<Class<? extends Entity>, String> entityToName = DioriteReflectionUtils.<Map<Class<? extends Entity>, String>>getField(EntityTypes.class, "d").get(null);
        final Map<Class<? extends Entity>, Integer> entityToId = DioriteReflectionUtils.<Map<Class<? extends Entity>, Integer>>getField(EntityTypes.class, "f").get(null);

        entityToName.put(BedWarsSkeleton.class, "Skeleton");
        entityToId.put(BedWarsSkeleton.class, 51);
    }

    public BedWarsSkeleton(final World world, final Team owner)
    {
        super(world);
        this.owner = owner;
    }

    public static Skeleton create(final Location location, final Team ownerTeam)
    {
        final CraftWorld craftWorld = (CraftWorld) location.getWorld();

        final BedWarsSkeleton bedWarsSkeleton = new BedWarsSkeleton(craftWorld.getHandle(), ownerTeam);
        bedWarsSkeleton.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        craftWorld.addEntity(bedWarsSkeleton, CreatureSpawnEvent.SpawnReason.SPAWNER_EGG);

        return (Skeleton) bedWarsSkeleton.bukkitEntity;
    }

    // zwraca team do ktorego nalezy ten szkielet
    public Team getOwner()
    {
        return this.owner;
    }

    @Override
    protected void r() // setupAi
    {
        this.goalSelector.a(1, new PathfinderGoalFloat(this)); // plywanie(?)
        this.goalSelector.a(2, new PathfinderGoalRandomStroll(this, 1.0D)); // losowe chodzenie
        this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10)); // patrzenie na graczy, 10 pewnie odleglosc
        this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this)); // patrzenie gdziekolwiek

        // this, typ atakowanego celu, odleglosc, ?, ?, dodatkowy warunek ataku
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityPlayer.class, 32, true, false, this::attackPlayer));
    }

    // warunek ataku w PathfinderGoalNearestAttackableTarget
    private boolean attackPlayer(final EntityPlayer entityPlayer)
    {
        final Player player = entityPlayer.getBukkitEntity();
        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);

        if (playerData == null)
        {
            return false;
        }

        if (playerData.isEliminated())
        {
            // nie atakujemy gracza wyeliminowanego
            return false;
        }

        // jesli team gracza inny niz wlasciciela to atakujemy
        return playerData.getTeam() != this.owner;
    }
}
