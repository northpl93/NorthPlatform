package pl.north93.northplatform.api.global.component.impl.profile;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.DefinedProfile;
import pl.north93.northplatform.api.global.component.annotations.SkipInjections;

@SkipInjections // nie skanujemy tej klasy zeby nie zarejestrowalo jej jako profil
class FakeProfile extends DefinedProfile
{
    private final boolean enabled;

    public FakeProfile(final String name, final boolean enabled)
    {
        super(name);
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled()
    {
        return this.enabled;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("enabled", this.enabled).toString();
    }
}
