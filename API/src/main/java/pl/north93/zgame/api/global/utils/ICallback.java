package pl.north93.zgame.api.global.utils;

public interface ICallback
{
    boolean isComplete();
    
    void onComplete(Runnable task);
}
