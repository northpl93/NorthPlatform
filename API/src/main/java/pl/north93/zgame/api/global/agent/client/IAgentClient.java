package pl.north93.zgame.api.global.agent.client;

public interface IAgentClient
{
    void connect();

    void redefineClass(String className, byte[] newBytes);
}
