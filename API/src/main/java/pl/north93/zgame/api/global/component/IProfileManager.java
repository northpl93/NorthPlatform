package pl.north93.zgame.api.global.component;

import java.util.Collection;

/**
 * Menadzer profili umozliwia okreslenie czy dane fragmenty kodu
 * powinny byc wykonywane w zaleznosci od zewnetrznych czynnikow.
 *
 * @see pl.north93.zgame.api.global.component.annotations.bean.Profile
 */
public interface IProfileManager
{
    void addProfile(String name);

    Collection<String> getActiveProfiles();

    boolean isActive(String name);
}
