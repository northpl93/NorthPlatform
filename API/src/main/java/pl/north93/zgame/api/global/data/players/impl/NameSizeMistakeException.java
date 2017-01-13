package pl.north93.zgame.api.global.data.players.impl;

public class NameSizeMistakeException extends Exception
{
    private final String nick;

    public NameSizeMistakeException(final String nick)
    {
        super("Failed to load offline player: nick from db doesn't match nick of joining player (" + nick + ")");
        this.nick = nick;
    }

    public String getNick()
    {
        return this.nick;
    }
}
