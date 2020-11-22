package pl.north93.northplatform.api.test;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.impl.injection.Injector;

@Slf4j
public class NorthPlatformJunitExtension implements Extension, BeforeAllCallback, BeforeEachCallback, AfterAllCallback
{
    @Override
    public void beforeAll(final ExtensionContext context) throws Exception
    {
        log.info("Ensuring test platform is started");
        PlatformTestEnvironmentRunner.ensureEnvironment();
    }

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception
    {
        final Class<?> testClass = context.getRequiredTestClass();
        final Object instance = context.getTestInstance().orElseThrow(IllegalStateException::new);

        Injector.inject(instance, testClass);
        log.info("Performed test injections in class {}", testClass.getName());
    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) throws Exception
    {
        log.info("Cleaning up testing environment");
        PlatformTestEnvironmentRunner.cleanupEnvironment();
    }
}
