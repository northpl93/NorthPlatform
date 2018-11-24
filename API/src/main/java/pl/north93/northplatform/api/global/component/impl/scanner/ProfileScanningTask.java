package pl.north93.northplatform.api.global.component.impl.scanner;

import java.lang.reflect.Constructor;

import javassist.CtClass;
import pl.north93.northplatform.api.global.component.DefinedProfile;
import pl.north93.northplatform.api.global.component.impl.context.AbstractBeanContext;
import pl.north93.northplatform.api.global.component.impl.general.SmartExecutor;
import pl.north93.northplatform.api.global.component.impl.profile.ProfileManagerImpl;

// zbiera klasy rozszerzajace profile
class ProfileScanningTask extends AbstractScanningTask
{
    public ProfileScanningTask(final ClassloaderScanningTask classloaderScanner, final Class<?> clazz, final CtClass ctClass, final AbstractBeanContext beanContext)
    {
        super(classloaderScanner, clazz, ctClass, beanContext);
    }

    @Override
    boolean tryComplete0()
    {
        if (! DefinedProfile.class.isAssignableFrom(this.clazz))
        {
            // nic wiecej nie robimy
            return true;
        }

        final DefinedProfile profile;
        try
        {
            final Constructor<?> constructor = this.clazz.getDeclaredConstructors()[0];
            profile = (DefinedProfile) SmartExecutor.execute(constructor, this.beanContext, null);
        }
        catch (final Exception e)
        {
            this.lastCause = e;
            return false;
        }

        final ProfileManagerImpl profileManager = this.classloaderScanner.getManager().getProfileManager();
        profileManager.addProfile(profile);
        return true;
    }
}
