package pl.north93.northplatform.api.global.component.exceptions;

/**
 * Wyjatek rzucany gdy nie udalo sie znalezc profilu o podanej nazwie.
 */
public class ProfileNotFoundException extends RuntimeException
{
    public ProfileNotFoundException(final String profileName)
    {
        super("Not found profile " + profileName);
    }
}
