package pl.north93.zgame.skyblock.server.world;

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
        final int locX = chunkX * 16 + world.random.nextInt(16);
        final int locZ = chunkZ * 16 + world.random.nextInt(16);

        for (int i = 128; i < 256; ++i)
        {


        }

        /*int i1 = MathHelper.c(chunk.e(new BlockPosition(locX, 0, locZ)) + 1, 16);
        int j1 = world.random.nextInt(i1 > 0?i1:chunk.g() + 16 - 1);*/

        //final int j1 =

        return chunk.getWorld().getHighestBlockYAt(new BlockPosition(locX,0, locZ));
    }
}
