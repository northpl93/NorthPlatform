package pl.north93.zgame.api.global.component.impl.profile;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javassist.CtBehavior;
import pl.north93.zgame.api.global.component.IProfileManager;
import pl.north93.zgame.api.global.component.annotations.bean.Profile;
import pl.north93.zgame.api.global.component.impl.general.ComponentManagerImpl;

public class ProfileManagerImpl implements IProfileManager
{
    private final ComponentManagerImpl componentManager;
    private final Set<String> profiles = new HashSet<>();

    public ProfileManagerImpl(final ComponentManagerImpl componentManager)
    {
        this.componentManager = componentManager;
        this.registerDefaultProfiles();
    }

    private void registerDefaultProfiles()
    {
        final String platformName = this.componentManager.getApiCore().getPlatform().name().toLowerCase(Locale.ROOT);
        this.addProfile(platformName);
    }

    @Override
    public void addProfile(final String name)
    {
        this.profiles.add(name);
    }

    @Override
    public Collection<String> getActiveProfiles()
    {
        return Collections.unmodifiableCollection(this.profiles);
    }

    @Override
    public boolean isActive(final String name)
    {
        return this.profiles.contains(name);
    }

    public boolean isActive(final AnnotatedElement annotatedElement)
    {
        final Profile annotation = annotatedElement.getAnnotation(Profile.class);
        return annotation == null || this.isActive(annotation.value());
    }

    public boolean isActive(final CtBehavior ctElement) throws ClassNotFoundException
    {
        if (! ctElement.hasAnnotation(Profile.class))
        {
            return true;
        }

        final Profile profile = (Profile) ctElement.getAnnotation(Profile.class);
        return this.isActive(profile.value());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("profiles", this.profiles).toString();
    }
}
