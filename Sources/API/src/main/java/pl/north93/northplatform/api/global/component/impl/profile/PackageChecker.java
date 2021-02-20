package pl.north93.northplatform.api.global.component.impl.profile;

import java.util.Map;
import java.util.WeakHashMap;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pl.north93.northplatform.api.global.component.annotations.bean.Profile;
import pl.north93.northplatform.api.global.component.exceptions.ProfileNotFoundException;

/**
 * Klasa sprawdzajaa czy dana paczka jest aktywna.
 * Zapewnia transparentne cachowanie.
 */
@ToString(of = "cache")
class PackageChecker
{
    private final ProfileManagerImpl profileManager;
    private final Map<PackageCacheKey, Boolean> cache = new WeakHashMap<>(100);

    public PackageChecker(final ProfileManagerImpl profileManager)
    {
        this.profileManager = profileManager;
    }

    /**
     * Sprawdza czy podana paczka w danym ClassLoaderze jest aktywna.
     *
     * @param classLoader ClassLoader w ktorym znajduje sie paczka.
     * @param pack Nazwa paczki do sprawdzenia.
     *
     * @throws ProfileNotFoundException Gdy profil zdefiniowany w adnotacji nie istnieje.
     * @return Czy dana paczka jest aktywna.
     */
    public boolean isPackageInactive(final ClassLoader classLoader, final String pack)
    {
        if (pack == null)
        {
            return false;
        }

        final Boolean result = this.cache.computeIfAbsent(new PackageCacheKey(classLoader, pack), this::computeIsPackageInactive);
        if (result == null)
        {
            return false;
        }

        return result;
    }

    private Boolean computeIsPackageInactive(final PackageCacheKey cacheKey)
    {
        final String packageName = cacheKey.getKey();

        final int lastDot = packageName.lastIndexOf('.');
        if (lastDot != - 1)
        {
            final String parentPackage = packageName.substring(0, lastDot);
            if (this.isPackageInactive(cacheKey.getClassLoader(), parentPackage))
            {
                // jesli paczka-rodzic jest nieaktywna to ta tez nie jest
                return true;
            }
        }

        try
        {
            final Class<?> packageInfo = Class.forName(packageName + ".package-info", false, cacheKey.getClassLoader());

            final Profile profile = packageInfo.getAnnotation(Profile.class);
            return ! this.profileManager.isProfileActive(profile);
        }
        catch (final ClassNotFoundException e)
        {
            // brak package-info; dana paczka jest aktywna
            return false;
        }
    }
}

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
class PackageCacheKey
{
    private final ClassLoader classLoader;
    private final String key;
}