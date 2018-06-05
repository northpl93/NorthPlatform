package pl.north93.groovyscript.api.source;

import java.io.File;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.reflections.DioriteReflectionUtils;
import org.diorite.commons.reflections.MethodInvoker;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import pl.north93.zgame.api.global.utils.lang.SneakyThrow;

public class DirectoryScriptSource implements IScriptSource
{
    private final File file;

    public DirectoryScriptSource(final File file)
    {
        Preconditions.checkState(file.isDirectory());
        this.file = file;
    }

    @Override
    public void setup(final GroovyClassLoader loader)
    {
        this.doRecursiveLoading(loader, this.file);
    }

    private void loadScript(final GroovyClassLoader loader, final File scriptFile)
    {
        final GroovyCodeSource codeSource = SneakyThrow.sneaky(() -> new GroovyCodeSource(scriptFile, "UTF-8"));
        if (codeSource == null)
        {
            return;
        }

        final Class clazz = loader.parseClass(codeSource);

        final MethodInvoker main = DioriteReflectionUtils.getMethod(clazz, "main", String[].class);
        main.invoke(null, (Object) null);
    }

    private void doRecursiveLoading(final GroovyClassLoader loader, final File directory)
    {
        final File[] files = directory.listFiles();
        if (files == null)
        {
            return;
        }

        for (final File child : files)
        {
            if (child.isDirectory())
            {
                this.doRecursiveLoading(loader, child);
                continue;
            }

            this.loadScript(loader, child);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("file", this.file).toString();
    }
}
