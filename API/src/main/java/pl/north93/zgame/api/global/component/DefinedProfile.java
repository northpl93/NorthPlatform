package pl.north93.zgame.api.global.component;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.SkipInjections;

/**
 * Klasa reprezentujaca profil.
 * Pelna lista profili musi byc znana, inaczej zostanie rzucony wyjatek.
 * Profil moze byc wlaczony lub wylaczony.
 */
@SkipInjections
public abstract class DefinedProfile
{
    private final String name;

    public DefinedProfile(final String name)
    {
        this.name = name;
    }

    /**
     * Zwraca nazwe profilu. Kazdy profil musi miec unikalna nazwe.
     *
     * @return nazwa profilu.
     */
    public final String getName()
    {
        return this.name;
    }

    /**
     * Czy ten profil jest wlaczony.
     *
     * @return czy profil jest wlaczony.
     */
    public abstract boolean isEnabled();

    @Override
    public final boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }

        final DefinedProfile profile = (DefinedProfile) o;
        return this.name.equals(profile.name);
    }

    @Override
    public final int hashCode()
    {
        return this.name.hashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).toString();
    }
}
