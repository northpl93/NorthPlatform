package pl.north93.zgame.antycheat.utils.handle;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.server.v1_12_R1.AxisAlignedBB;
import net.minecraft.server.v1_12_R1.WorldServer;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import pl.north93.zgame.antycheat.utils.AABB;

public final class WorldHandle
{
    private final World world;
    private final WorldServer worldServer;

    private WorldHandle(final World world)
    {
        this.world = world;
        this.worldServer = ((CraftWorld) world).getHandle();
    }

    public static WorldHandle of(final World world)
    {
        return new WorldHandle(world);
    }

    /**
     * Zwraca liste AABB bloków kolidujących z podanym w argumencie AABB.
     *
     * @param aabb AABB z którym sprawdzamy kolizje.
     * @return Lista kolidujących AABB z tym podanym w argumencie.
     */
    public List<AABB> getCollisionBoxes(final AABB aabb)
    {
        final List<AxisAlignedBB> collisionList = this.worldServer.getCubes(null, aabb.toNms()); // getCollisionBoxes in MCP1.10, po update do 1.12 nazwa jest juz normalniejsza
        return collisionList.stream().map(AABB::new).collect(Collectors.toList());
    }

    /**
     * Sprawdza czy dany AABB koliduje z jakimś blokiem.
     *
     * @param aabb AABB z którym sprawdzamy kolizje.
     * @return True jeśli AABB koliduj z jakimkolwiek blokiem.
     */
    public boolean collidesWithAnyBlock(final AABB aabb)
    {
        return this.worldServer.b(aabb.toNms()); // collidesWithAnyBlock in MCP
    }
}
