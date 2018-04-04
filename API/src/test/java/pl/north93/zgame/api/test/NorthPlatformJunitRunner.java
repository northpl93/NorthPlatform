package pl.north93.zgame.api.test;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class NorthPlatformJunitRunner extends Runner
{
    private final Class<?> testClass;

    public NorthPlatformJunitRunner(final Class<?> testClass)
    {
        this.testClass = testClass;
    }

    @Override
    public Description getDescription()
    {
        return Description.createTestDescription(this.testClass, "North Platform Runner");
    }

    @Override
    public void run(final RunNotifier notifier)
    {
        TestApiCore.ensureEnvironment();
    }
}
