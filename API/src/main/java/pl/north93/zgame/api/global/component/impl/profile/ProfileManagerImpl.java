package pl.north93.zgame.api.global.component.impl.profile;

import static pl.north93.zgame.api.global.utils.lang.CollectionUtils.findInCollection;


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
import pl.north93.zgame.api.global.Platform;
import pl.north93.zgame.api.global.component.DefinedProfile;
import pl.north93.zgame.api.global.component.IProfileManager;
import pl.north93.zgame.api.global.component.annotations.bean.Profile;
import pl.north93.zgame.api.global.component.exceptions.ProfileNotFoundException;
import pl.north93.zgame.api.global.component.impl.general.ComponentManagerImpl;
import pl.north93.zgame.api.global.utils.lang.ClassUtils;

public class ProfileManagerImpl implements IProfileManager
{
    private final ComponentManagerImpl componentManager;
    private final Set<DefinedProfile> profiles = new HashSet<>();

    public ProfileManagerImpl(final ComponentManagerImpl componentManager)
    {
        this.componentManager = componentManager;
        this.registerDefaultProfiles();
    }

    private void registerDefaultProfiles()
    {
        final Platform activePlatform = this.componentManager.getApiCore().getPlatform();
        for (final Platform platform : Platform.values())
        {
            final String name = platform.name().toLowerCase(Locale.ROOT);
            this.addProfile(new FakeProfile(name, activePlatform == platform));
        }
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
    public boolean isActive(final String name)
    {
        return this.getProfile(name).isEnabled();
    }

    public boolean isPackageActive(final ClassLoader classLoader, final String pack)
    {
        if (pack == null)
        {
            return true;
        }
        final Boolean result = ClassUtils.walkPackageInfo(classLoader, pack, packageInfo ->
        {
            final Profile profile = packageInfo.getAnnotation(Profile.class);
            if (profile != null && ! this.isActive(profile.value()))
            {
                return false;
            }

            return null; // kontynuujemy
        });

        if (result == null)
        {
            return true;
        }

        return result;
    }

    public boolean isActive(final Class<?> clazz)
    {
        if (! this.isPackageActive(clazz.getClassLoader(), clazz.getPackage().getName()))
        {
            // paczka do ktorej nalezy klasa jest nieaktywna
            return false;
        }

        final Profile annotation = clazz.getAnnotation(Profile.class);
        return annotation == null || this.isActive(annotation.value());
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
        return annotation == null || this.isActive(annotation.value());
    }

    public boolean isActive(final ClassLoader classLoader, final CtClass ctClass) throws ClassNotFoundException
    {
        if (! this.isPackageActive(classLoader, ctClass.getPackageName()))
        {
            // paczka jest nieaktywna
            return false;
        }

        final Profile profile = (Profile) ctClass.getAnnotation(Profile.class);
        return profile == null || this.isActive(profile.value());
    }

    public boolean isActive(final ClassLoader classLoader, final CtBehavior ctElement) throws ClassNotFoundException
    {
        if (! this.isActive(classLoader, ctElement.getDeclaringClass()))
        {
            // klasa deklarujaca jest nieaktywna
            return false;
        }

        final Profile profile = (Profile) ctElement.getAnnotation(Profile.class);
        return profile == null || this.isActive(profile.value());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("profiles", this.profiles).toString();
    }
}
