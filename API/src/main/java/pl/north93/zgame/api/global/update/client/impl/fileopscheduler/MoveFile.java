package pl.north93.zgame.api.global.update.client.impl.fileopscheduler;

import java.io.File;

public class MoveFile implements IFileOperation
{
    private final File from;
    private final File to;

    public MoveFile(final File from, final File to)
    {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean doAction()
    {
        return ! (this.to.exists() && ! this.to.delete()) && this.from.renameTo(this.to);
    }

    @Override
    public String toString()
    {
        return "Move [" + this.from.getPath() + " -> " + this.to.getPath() + "]";
    }
}
