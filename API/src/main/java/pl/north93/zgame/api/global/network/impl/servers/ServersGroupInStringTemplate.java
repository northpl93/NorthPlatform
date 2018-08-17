package pl.north93.zgame.api.global.network.impl.servers;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.server.IServersManager;
import pl.north93.zgame.api.global.network.server.group.IServersGroup;
import pl.north93.zgame.api.global.serializer.platform.FieldInfo;
import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.Template;

public class ServersGroupInStringTemplate implements Template<IServersGroup, SerializationContext, DeserializationContext>
{
    @Inject
    private IServersManager serversManager;

    @Override
    public void serialise(final SerializationContext context, final FieldInfo field, final IServersGroup object) throws Exception
    {
        context.writeString(field, object.getName());
    }

    @Override
    public IServersGroup deserialize(final DeserializationContext context, final FieldInfo field) throws Exception
    {
        final String groupName = context.readString(field);
        return this.serversManager.getServersGroup(groupName);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
