package pl.north93.northplatform.api.global.component.impl.profile;

import static pl.north93.northplatform.api.global.utils.lang.CollectionUtils.findInCollection;


import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javassist.CtBehavior;
import javassist.CtClass;
import pl.north93.northplatform.api.global.HostId;
import pl.north93.northplatform.api.global.component.DefinedProfile;
import pl.north93.northplatform.api.global.component.IProfileManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Profile;
import pl.north93.northplatform.api.global.component.exceptions.ProfileNotFoundException;
import pl.north93.northplatform.api.global.component.impl.general.ComponentManagerImpl;

public class ProfileManagerImpl implements IProfileManager
{
    private final ComponentManagerImpl componentManager;
    private final PackageChecker       packageChecker;
    private final Set<DefinedProfile> profiles = new HashSet<>();

    public ProfileManagerImpl(final ComponentManagerImpl componentManager)
    {
        this.componentManager = componentManager;
        this.packageChecker = new PackageChecker(this);
        this.registerDefaultProfiles();
    }

    private void registerDefaultProfiles()
    {
        final HostId activeHost = this.componentManager.getApiCore().getHostId();

        final String name = activeHost.getHostId().toLowerCase(Locale.ROOT);
        this.addProfile(new FakeProfile(name, true));
    }

    @Override
    public void addProfile(final DefinedProfile profile)
    {
        this.profiles.add(profile);
    }

    @Override
    public Collection<DefinedProfile> getActiveProfiles()
    {
        return this.profiles.stream().filter(DefinedProfile::isEnabled).collect(Collectors.toSet());
    }

    @Override
    public DefinedProfile getProfile(final String name)
    {
        final DefinedProfile profile = findInCollection(this.profiles, DefinedProfile::getName, name);
        if (profile == null)
        {
            throw new ProfileNotFoundException(name);
        }
        return profile;
    }

    @Override
    public boolean isProfileActive(final String name)
    {
        return this.getProfile(name).isEnabled();
    }

    @Override
    public boolean isProfileActiveIgnoringUnexisting(final String profileName) throws ProfileNotFoundException
    {
        try
        {
            return this.isProfileActive(profileName);
        }
        catch (final ProfileNotFoundException e)
        {
            return false;
        }
    }

    public boolean isActive(final Class<?> clazz)
    {
        if (this.packageChecker.isPackageInactive(clazz.getClassLoader(), clazz.getPackage().getName()))
        {
            // paczka do ktorej nalezy klasa jest nieaktywna
            return false;
        }

        final Profile annotation = clazz.getAnnotation(Profile.class);
        return this.isProfileActive(annotation);
    }

    public boolean isActive(final Member member)
    {
        final AnnotatedElement annotatedMember = (AnnotatedElement) member;
        if (! this.isActive(member.getDeclaringClass()))
        {
            // klasa deklarujaca jest niekatywna
            return false;
        }

        final Profile annotation = annotatedMember.getAnnotation(Profile.class);
        return this.isProfileActive(annotation);
    }

    public boolean isActive(final ClassLoader classLoader, final CtClass ctClass) throws ClassNotFoundException
    {
        if (this.packageChecker.isPackageInactive(classLoader, ctClass.getPackageName()))
        {
            // paczka jest nieaktywna
            return false;
        }

        final Profile profile = (Profile) ctClass.getAnnotation(Profile.class);
        return this.isProfileActive(profile);
    }

    public boolean isActive(final ClassLoader classLoader, final CtBehavior ctElement) throws ClassNotFoundException
    {
        if (! this.isActive(classLoader, ctElement.getDeclaringClass()))
        {
            // klasa deklarujaca jest nieaktywna
            return false;
        }

        final Profile profile = (Profile) ctElement.getAnnotation(Profile.class);
        return this.isProfileActive(profile);
    }

    public boolean isProfileActive(final Profile profile)
    {
        if (profile == null)
        {
            return true;
        }

        final String name = profile.value();
        return profile.allowUnexistingProfile() ? this.isProfileActiveIgnoringUnexisting(name) : this.isProfileActive(name);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("profiles", this.profiles).toString();
    }
}
