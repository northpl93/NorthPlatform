package pl.north93.zgame.skyblock.server.world;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.Chunk;
import net.minecraft.server.v1_10_R1.SpawnerCreature;
import net.minecraft.server.v1_10_R1.World;

import javassist.ClassMap;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import pl.north93.zgame.api.global.agent.client.IAgentClient;

public class MobSpawningFix
{
    public static void applyChange(final IAgentClient client) throws Exception
    {
        final ClassPool classPool = new ClassPool();
        classPool.appendClassPath(new LoaderClassPath(SpawnerCreature.class.getClassLoader()));
        classPool.appendClassPath(new LoaderClassPath(MobSpawningFix.class.getClassLoader()));

        final CtClass originalClass = classPool.getCtClass(SpawnerCreature.class.getName());
        final CtClass patchClass = classPool.getCtClass(MobSpawningFix.class.getName());

        final CtMethod originalMethod = originalClass.getMethod("getRandomPosition", "(Lnet/minecraft/server/v1_10_R1/World;II)Lnet/minecraft/server/v1_10_R1/BlockPosition;");
        final CtMethod patchMethod = patchClass.getMethod("getRandomPosition", "(Lnet/minecraft/server/v1_10_R1/World;II)Lnet/minecraft/server/v1_10_R1/BlockPosition;");


        final ClassMap classMap = new ClassMap();
        classMap.put("pl.north93.zgame.skyblock.server.world.MobSpawningFix", "net.minecraft.server.v1_10_R1.SpawnerCreature");

        originalMethod.setBody(patchMethod, classMap);
        client.redefineClass("net.minecraft.server.v1_10_R1.SpawnerCreature", originalClass.toBytecode());
    }

    private static BlockPosition getRandomPosition(final World world, final int chunkX, final int chunkZ)
    {
        final Chunk chunk = world.getChunkAt(chunkX, chunkZ);
        final int relX = world.random.nextInt(16);
        final int locX = chunkX * 16 + relX;
        final int relZ = world.random.nextInt(16);
        final int locZ = chunkZ * 16 + relZ;

        final BlockPosition.MutableBlockPosition blockPosition = new BlockPosition.MutableBlockPosition(locX, 0, locZ);
        final List<BlockPosition> candidates = new ArrayList<>(4);
        for (int i = chunk.b(relX, relZ); i >= 0; i--) //chunk.b = najwyzszy blok
        {
            blockPosition.c(locX, i, locZ); //BlockPosition.c = ustawienie lokacji
            if (chunk.getBlockData(blockPosition).getMaterial().isSolid())
            {
                candidates.add(new BlockPosition(locX, i + 1, locZ));
            }
        }

        if (candidates.isEmpty())
        {
            return blockPosition;
        }

        return candidates.get(world.random.nextInt(candidates.size()));
    }
}
