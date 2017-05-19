package pl.north93.zgame.api.bukkit.entityhider.impl;

import net.minecraft.server.v1_10_R1.EntityTrackerEntry;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import pl.north93.zgame.api.global.agent.client.IAgentClient;

/**
 * Modyfikujemy metode c z klasy EntityTrackerEntry
 * i dodajemy tam kod sprawdzajacy czy gracz ma ukryte
 * dane entity id.
 */
class EntityTrackerEntryPatcher
{
    /*default*/ static void applyChange(final IAgentClient client) throws Exception
    {
        final ClassPool classPool = new ClassPool();
        classPool.appendClassPath(new LoaderClassPath(EntityTrackerEntry.class.getClassLoader()));
        classPool.appendClassPath(new LoaderClassPath(EntityTrackerEntryPatcher.class.getClassLoader()));

        final CtClass targetClass = classPool.getCtClass(EntityTrackerEntry.class.getName());
        final CtMethod targetMethod = targetClass.getMethod("c", "(Lnet/minecraft/server/v1_10_R1/EntityPlayer;)Z");

        targetMethod.setBody(PATCH);
        client.redefineClass("net.minecraft.server.v1_10_R1.EntityTrackerEntry", targetClass.toBytecode());
    }

    private static final String PATCH =
            "{\n" +
                    "        final java.util.List metadata = $1.getBukkitEntity().getMetadata(\"API.EntityHider/hiddenEntities\");\n" +
                    "        if (metadata.size() != 0)\n" +
                    "        {\n" +
                    "            final java.util.List hiddenEntities = (java.util.List) ((org.bukkit.metadata.MetadataValue) metadata.get(0)).value();\n" +
                    "            if (hiddenEntities.contains(java.lang.Integer.valueOf($0.tracker.getId())))\n" +
                    "            {\n" +
                    "                return false;\n" +
                    "            }\n" +
                    "        }\n" +
                    "        if ($0.tracker.isPassenger())\n" +
                    "        {\n" +
                    "            return isTrackedBy($0.tracker.getVehicle(), $1);\n" +
                    "        }\n" +
                    "        else\n" +
                    "        {\n" +
                    "            if (hasPassengerInRange($0.tracker, $1))\n" +
                    "            {\n" +
                    "                return true;\n" +
                    "            }\n" +
                    "            else\n" +
                    "            {\n" +
                    "                return this.isInRangeOfPlayer($1);\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }";
}
