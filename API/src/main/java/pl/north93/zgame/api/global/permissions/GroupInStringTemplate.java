package pl.north93.zgame.api.global.permissions;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.serializer.platform.FieldInfo;
import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.Template;

public final class GroupInStringTemplate implements Template<Group, SerializationContext, DeserializationContext>
{
    @Inject
    private PermissionsManager permissionsManager;

    @Override
    public void serialise(final SerializationContext context, final FieldInfo field, final Group object) throws Exception
    {
        context.writeString(field, object.getName());
    }

    @Override
    public Group deserialize(final DeserializationContext context, final FieldInfo field) throws Exception
    {
        final String groupName = context.readString(field);
        return this.permissionsManager.getGroupByName(groupName);
    }
}
