package pl.north93.northplatform.features.global;

import org.apache.commons.lang3.RandomStringUtils;

import pl.north93.northplatform.api.global.redis.observable.IObservationManager;
import pl.north93.northplatform.api.global.redis.observable.Value;

public class Benchmark
{
    //@Bean
    public Benchmark(final IObservationManager observationManager)
    {
        for (int i = 0; i < 14; i++)
        {
            final Value<String> value = observationManager.get(String.class, "benchmark-value-" + i);
            new WorkerThread(value).start();
        }
    }
}

class WorkerThread extends Thread
{
    private final Value<String> value;

    public WorkerThread(final Value<String> value)
    {
        this.value = value;
        this.setPriority(Thread.MAX_PRIORITY);
        this.setDaemon(true);
    }

    @Override
    public void run()
    {
        long second = System.currentTimeMillis();
        long operations = 0;

        for (int i = 0; i < 1_000_000; i++)
        {
            this.value.update(string -> {
                // lock -> update -> unlock
                // use random value to ensure that Value caching don't break the benchmark
                return RandomStringUtils.random(8);
            });

            operations++;

            if (System.currentTimeMillis() - second >= 1000)
            {
                second = System.currentTimeMillis();
                System.out.println("UPDATES PER SECOND: " + operations);
                operations = 0;
            }
        }
    }
}