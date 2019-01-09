package pl.north93.northplatform.api.global.network.players;

/**
 * Wyjątek rzucany podczas ładowania gracza w offline mode z bazy danych w
 * momencie gdy wielkość liter nicku w bazie nie zgadza się z wielkością
 * liter w nicku według którego znaleźliśmy te dane.
 */
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
