package pl.north93.northplatform.api.global.component;

import java.util.Collection;

import pl.north93.northplatform.api.global.component.annotations.bean.Profile;
import pl.north93.northplatform.api.global.component.exceptions.ProfileNotFoundException;

/**
 * Menadzer profili umozliwia okreslenie czy dane fragmenty kodu
 * powinny byc wykonywane w zaleznosci od zewnetrznych czynnikow.
 *
 * @see Profile
 */
public interface IProfileManager
{
    void addProfile(DefinedProfile profile);

    Collection<DefinedProfile> getActiveProfiles();

    DefinedProfile getProfile(String name) throws ProfileNotFoundException;

    boolean isProfileActive(String profileName) throws ProfileNotFoundException;
}
