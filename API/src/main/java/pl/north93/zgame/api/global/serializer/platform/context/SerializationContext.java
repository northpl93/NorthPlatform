package pl.north93.zgame.api.global.serializer.platform.context;

import pl.north93.zgame.api.global.serializer.platform.FieldInfo;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;

public abstract class SerializationContext extends Context
{
    public SerializationContext(final TemplateEngine templateEngine)
    {
        super(templateEngine);
    }

    /**
     * Zamyka proces serializowania i zwraca wynik pracy serializera.
     *
     * @return Wynik pracy serializera.
     * @throws Exception W wypadku wystapienia problemów podczas operacji.
     */
    public abstract Object finalizeAndGetResult() throws Exception;

    public abstract void writeNull(FieldInfo field) throws Exception;

    public abstract void writeString(FieldInfo field, String string) throws Exception;

    public abstract void writeBoolean(FieldInfo field, Boolean aBoolean) throws Exception;

    public abstract void writeByte(FieldInfo field, Byte aByte) throws Exception;

    public abstract void writeShort(FieldInfo field, Short aShort) throws Exception;

    public abstract void writeInteger(FieldInfo field, Integer integer) throws Exception;

    public abstract void writeFloat(FieldInfo field, Float aFloat) throws Exception;

    public abstract void writeDouble(FieldInfo field, Double aDouble) throws Exception;

    public abstract void writeLong(FieldInfo field, Long aLong) throws Exception;
}
