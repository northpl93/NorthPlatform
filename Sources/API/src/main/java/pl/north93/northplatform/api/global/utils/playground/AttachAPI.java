package pl.north93.northplatform.api.global.utils.playground;

/**
 *         AttachAPI.ensureToolsJar();
 *          new AttachTest();
 *
 * Zabawy NorthPL93 i GotoFinal w podpinanie debugera do JVMki
 * zeby naprawic znikajace breakpointy przy redefiniowaniu.
 *
 * Ten pliczek wczytuje tools.jar, reszta w AttachTest.java
 * https://stackoverflow.com/a/44660342/5155332
 */
class AttachAPI {

    private static boolean started;

//    static {
//        try {
//            String javaHome = System.getProperty("java.home");
//            String toolsJarURL = "file:" + javaHome + "/../lib/tools.jar";
//
//            // Make addURL public
//            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
//            method.setAccessible(true);
//
//            URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
//            if (sysloader.getResourceAsStream("/com/sun/tools/attach/VirtualMachine.class") == null) {
//                method.invoke(sysloader, (Object) new URL(toolsJarURL));
//                Thread.currentThread().getContextClassLoader().loadClass("com.sun.tools.attach.VirtualMachine");
//                Thread.currentThread().getContextClassLoader().loadClass("com.sun.tools.attach.AttachNotSupportedException");
//            }
//
//        } catch (Exception e) {
//            System.out.println("Java home points to " + System.getProperty("java.home") + " make sure it is not a JRE path");
//            System.out.println("Failed to add tools.jar to classpath");
//            e.printStackTrace();
//        }
//        started = true;
//    };

    public static void ensureToolsJar() {
        if (!started) {
            System.out.println("Attach API not initialized");
        }
    }
}
