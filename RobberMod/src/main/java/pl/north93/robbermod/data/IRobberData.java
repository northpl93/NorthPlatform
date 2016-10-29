package pl.north93.robbermod.data;

public interface IRobberData
{
    int getRobberCount();

    void setRobberCount(int number);

    default void incrementRobberCount()
    {
        this.setRobberCount(this.getRobberCount() + 1);
    }
}
