package pl.north93.zgame.datashare.netcontroller;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.datashare.api.DataSharingGroup;
import pl.north93.zgame.datashare.api.IDataShareManager;
import pl.north93.zgame.datashare.sharedimpl.PlayerDataShareComponent;

public class BroadcastTask implements Runnable
{
    private final DataSharingGroup    group;
    @Inject
    private PlayerDataShareComponent  shared;

    public BroadcastTask(final DataSharingGroup group)
    {
        this.group = group;
    }

    @Override
    public void run()
    {
        final IDataShareManager shareManager = this.shared.getDataShareManager();
        final List<String> messages = this.group.getAnnouncer().getMessages();

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final String message = messages.get(random.nextInt(messages.size()));

        shareManager.broadcast(this.group, message);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("group", this.group).toString();
    }
}
