package pl.north93.zgame.api.global.update.client.impl.fileopscheduler;

import java.io.File;
import java.io.IOException;

public class RunScript implements IFileOperation
{
    private final File script;

    public RunScript(final File script)
    {
        this.script = script;
    }

    @Override
    public boolean doAction()
    {
        try
        {
            final Process exec = Runtime.getRuntime().exec(this.script.getAbsolutePath(), new String[]{}, this.script.getParentFile());
            return exec.isAlive();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String toString()
    {
        return "Run Script [" + this.script + "]";
    }
}
