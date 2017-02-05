package pl.north93.zgame.api.global.update.client.impl.fileopscheduler;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FileOpScheduler extends Thread
{
    private final List<IFileOperation> operations;

    public FileOpScheduler()
    {
        super("NorthAPI/FileOpScheduler");
        this.operations = new ArrayList<>();
    }

    @Override
    public void run()
    {
        final Iterator<IFileOperation> operationIterator = this.operations.iterator();
        while (operationIterator.hasNext())
        {
            final IFileOperation op = operationIterator.next();
            operationIterator.remove();
            System.out.println(op);
            op.doAction();
        }
    }

    public void addAction(final IFileOperation fileOperation)
    {
        this.operations.add(fileOperation);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("operations", this.operations).toString();
    }
}
