package pl.north93.zgame.daemon.servers;

import java.io.File;
import java.util.UUID;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

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

    public File getEngineFile(final String engineName)
    {
        return new File(this.engines, engineName);
    }

    public File getPattern(final String patternId)
    {
        return new File(this.patterns, patternId);
    }
}
