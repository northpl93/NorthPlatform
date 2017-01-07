package pl.north93.zgame.api.global.redis.messaging.templates;

import java.lang.reflect.Array;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class ArrayTemplate implements Template<Object[]>
{
    private final Class<?> arrayClass;
    private final Template inArrayTemplate;

    public ArrayTemplate(final Class<?> arrayClass, final Template inArrayTemplate)
    {
        this.arrayClass = arrayClass;
        this.inArrayTemplate = inArrayTemplate;
    }

    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final Object[] object) throws Exception
    {
        packer.packArrayHeader(object.length);
        for (final Object element : object)
        {
            //noinspection unchecked
            this.inArrayTemplate.serializeObject(templateManager, packer, element);
        }
    }

    @Override
    public Object[] deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        final int arraySize = unpacker.unpackArrayHeader();
        final Object[] newArray = (Object[]) Array.newInstance(this.arrayClass, arraySize);
        for (int i = 0; i < arraySize; i++)
        {
            newArray[i] = this.inArrayTemplate.deserializeObject(templateManager, unpacker);
        }
        return newArray;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arrayClass", this.arrayClass).append("inArrayTemplate", this.inArrayTemplate).toString();
    }
}
