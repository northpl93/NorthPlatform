package pl.north93.zgame.api.global.utils.test;

import static org.junit.Assert.assertEquals;


import org.junit.Test;

import pl.north93.zgame.api.global.utils.JavaArguments;

public class JavaArgumentsTest
{
    @Test
    public void startLineGeneratorTest()
    {
        final JavaArguments java = new JavaArguments();

        java.setJar("test.jar");
        java.setStartHeapSize(100);
        java.setMaxHeapSize(1_000);
        java.addJavaArg("XX:+UseG1GC");
        java.addEnvVar("northplatform.testenv", "itWorks");

        System.out.println("startLineGeneratorTest()");
        System.out.println(java.buildStartLine());
        assertEquals("java -XX:+UseG1GC -Xmx1000m -Xms100m -Dnorthplatform.testenv=itWorks -jar test.jar", java.buildStartLine());
    }
}