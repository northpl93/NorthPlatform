package pl.north93.zgame.api.global.utils;

import com.google.common.base.Preconditions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleCallback implements ICallback
{
    private volatile boolean completed;
    protected Runnable task;
    
    @Override
    public final boolean isComplete()
    {
        return completed;
    }
    
    @Override
    public synchronized final void onComplete(Runnable task)
    {
        Preconditions.checkArgument(task != null, "Task is null");
        Preconditions.checkState(this.task == null, "Cannot call onComplete() twice.");
        this.task = task;
        
        if ( completed )
        {
            runTask();
        }
    }
    
    protected void runTask()
    {
        try
        {
            this.task.run();
        }
        catch ( Throwable e )
        {
            log.error("A task in callback throws an exception", e);
        }
    }
    
    public synchronized final void callComplete()
    {
        if ( completed )
        {
            return;
        }
        
        completed = true;
        if ( task != null )
        {
            runTask();
        }
    }
}
