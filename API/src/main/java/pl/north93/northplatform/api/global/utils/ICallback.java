package pl.north93.northplatform.api.global.utils;

public interface ICallback
{
    boolean isComplete();
    
    void onComplete(Runnable task);
}
