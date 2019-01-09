package pl.north93.northplatform.daemon.servers;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.apache.commons.io.filefilter.DirectoryFileFilter;

import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class FilesManager
{
    @Inject
    private       ApiCore apiCore;
    private final File    workspace;
    private final File    engines;
    private final File    patterns;

    @Bean
    private FilesManager()
    {
        this.workspace = this.apiCore.getFile("workspace");
        this.engines = this.apiCore.getFile("engines");
        this.patterns = this.apiCore.getFile("patterns");
    }

    public File getWorkspace(final UUID serverId)
    {
        final File workspace = new File(this.workspace, serverId.toString());
        if (! workspace.mkdirs())
        {
            throw new IllegalStateException("Failed to create workspace");
        }

        return workspace;
    }

    public Collection<File> getAllWorkspaces()
    {
        final FileFilter filter = DirectoryFileFilter.INSTANCE;
        final File[] files = this.workspace.listFiles(filter);

        return files == null ? Collections.emptyList() : Arrays.asList(files);
    }

    public File getEngineFile(final String engineName)
    {
        return new File(this.engines, engineName);
    }

    public File getPattern(final String patternId)
    {
        return new File(this.patterns, patternId);
    }
}
