package pl.north93.zgame.api.bukkit.entityhider.impl;

import net.minecraft.server.v1_10_R1.EntityTrackerEntry;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import pl.north93.zgame.api.global.agent.client.IAgentClient;

/**
 * Modyfikujemy metode c z klasy EntityTrackerEntry
 * i dodajemy tam kod delegujacy sprawdzanie
 * widocznosci entity do zewnetrznego kontrolera
 * przez funkcje zapisana w metadanych.
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
            "{" +
                    "    final java.util.List metadata = $1.getBukkitEntity().getMetadata(\"API.EntityHider/hideFunction\");\n" +
                    "    if (! metadata.isEmpty()) {\n" +
                    "        final java.util.function.Function hideFunction = (java.util.function.Function) ((org.bukkit.metadata.MetadataValue) metadata.get(0)).value();\n" +
                    "        if (((Boolean)hideFunction.apply($0.tracker)).booleanValue()) {\n" +
                    "            return false; // jesli funkcja zwraca true to ukrywamy entity\n" +
                    "        }\n" +
                    "    }\n" +
                    "    if ($0.tracker.isPassenger()) {\n" +
                    "        return isTrackedBy($0.tracker.getVehicle(), $1);\n" +
                    "    } else {\n" +
                    "        return hasPassengerInRange($0.tracker, $1) ? true : $0.isInRangeOfPlayer($1);\n" +
                    "    }" +
                    "}";
}
