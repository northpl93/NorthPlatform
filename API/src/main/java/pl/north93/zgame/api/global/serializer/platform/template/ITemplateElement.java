package pl.north93.zgame.api.global.serializer.platform.template;

import pl.north93.zgame.api.global.serializer.platform.FieldInfo;

/**
 * Reprezentuje jedną zmienną w templatce.
 * Wszystkie wygenerowane templatki składają się z tych elementów.
 */
public interface ITemplateElement
{
    Object get(Object instance);

    void set(Object instance, Object value);

    FieldInfo getFieldInfo();

    Template getTemplate();
}
