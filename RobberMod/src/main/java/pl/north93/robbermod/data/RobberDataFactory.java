package pl.north93.robbermod.data;

import java.util.concurrent.Callable;

public class RobberDataFactory implements Callable<IRobberData>
{
    @Override
    public IRobberData call() throws Exception
    {
        return new RobberDataImpl();
    }
}
