package pl.north93.northplatform.api.global.component;

import java.util.Collection;

import pl.north93.northplatform.api.global.component.annotations.bean.Profile;
import pl.north93.northplatform.api.global.component.exceptions.ProfileNotFoundException;

/**
 * Profiles manager allows to specify does the specified packages and classes should
 * be visible to the components system.
 *
 * @see Profile
 */
public interface IProfileManager
{
    void addProfile(DefinedProfile profile);

    Collection<DefinedProfile> getActiveProfiles();

    DefinedProfile getProfile(String name) throws ProfileNotFoundException;

    /**
     * Checks does the specified is active.
     *
     * @param profileName Name of a profile to check.
     * @return True if the profile is enabled, false otherwise.
     * @throws ProfileNotFoundException If a profile with the specified name doesn't exist.
     */
    boolean isProfileActive(String profileName) throws ProfileNotFoundException;

    /**
     * Checks does the specified profile is active, but doesn't throw exception
     * when profile doesn't exist.
     *
     * @param profileName Name of a profile to check.
     * @return True if the profile is enabled, false if disabled or doesn't exist.
     */
    boolean isProfileActiveIgnoringUnexisting(String profileName);
}
