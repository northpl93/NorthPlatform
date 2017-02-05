package pl.north93.zgame.api.global.update.client.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;
import pl.north93.zgame.api.global.update.api.IUpdateApiRpc;
import pl.north93.zgame.api.global.update.api.UpdateFile;
import pl.north93.zgame.api.global.update.client.api.IUpdateClient;
import pl.north93.zgame.api.global.update.client.impl.fileopscheduler.FileOpScheduler;

public class UpdateManager extends Component implements IUpdateClient
{
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager     rpcManager;
    private Logger          logger;
    private UpdateInfoImpl  updateInfo;
    private IUpdateApiRpc   updateApi;
    private FileOpScheduler fileOpScheduler = new FileOpScheduler();

    @Override
    protected void enableComponent()
    {
        this.logger.info("Starting North Platform updating system...");
        Runtime.getRuntime().addShutdownHook(this.fileOpScheduler);
        this.updateApi = this.rpcManager.createRpcProxy(IUpdateApiRpc.class, Targets.networkController());
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public void checkForUpdate()
    {
        this.logger.info("Checking for updates...");
        final UpdateFile[] filesFor = this.updateApi.getFilesFor(this.getApiCore().getPlatform(), this.getApiCore().getId());
        if (filesFor.length == 0)
        {
            this.logger.info("Received files list length is 0. Update check completed.");
            return; // no files to download
        }
        final File root = this.getApiCore().getRootDirectory();
        final Collection<UpdateFile> files = new ArrayList<>();
        for (final UpdateFile updateFile : filesFor)
        {
            final File fileToCheck = new File(root, updateFile.getName());
            final String md5;
            try
            {
                md5 = Files.hash(fileToCheck, Hashing.md5()).toString();
            }
            catch (final IOException e)
            {
                this.logger.warning("Exception occurred while checking file md5 (" + fileToCheck.getAbsolutePath() + ") (" + e.getMessage() + ")");
                continue;
            }

            if (updateFile.getMd5().equals(md5))
            {
                continue; // file is up-to-date
            }

            files.add(updateFile);
        }

        this.updateInfo = new UpdateInfoImpl(files);
        this.logger.info("Completed checking for updates!");
    }

    @Override
    public UpdateInfoImpl getUpdateInfo()
    {
        return this.updateInfo;
    }

    @Override
    public void beginUpdate()
    {
        this.logger.info("Starting update...");
        if (this.updateInfo == null)
        {
            return;
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
