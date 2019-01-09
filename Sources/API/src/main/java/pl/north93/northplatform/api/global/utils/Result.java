package pl.north93.northplatform.api.global.utils;

import pl.north93.northplatform.api.global.utils.lang.CatchException;

public enum Result
{
    SUCCESS
    {
        @Override
        public Result whenSuccess(Runnable callback)
        {
            CatchException.printStackTrace(callback::run, "Callback in whenSuccess throws an exception:");
            return this;
        }
    }, 
    
    FAILTURE
    {
        @Override
        public Result whenFailture(Runnable callback)
        {
            CatchException.printStackTrace(callback::run, "Callback in whenFailture throws an exception:");
            return this;
        }
    }
    ;
    
    public Result whenSuccess(Runnable callback)
    {
        return this;
    }
    
    public Result whenFailture(Runnable callback)
    {
        return this;
    }
    
    public boolean isSuccess()
    {
        return this == SUCCESS;
    }
}