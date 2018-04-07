package pl.north93.zgame.api.global.redis.messaging.impl.element;

import pl.north93.zgame.api.global.redis.messaging.Template;

/**
 * Reprezentuje jedną zmienną w templatce.
 * Wszystkie wygenerowane templatki składają się z tych elementów.
 */
public interface ITemplateElement
{
    Object get(Object instance);

    void set(Object instance, Object value);

    Template getTemplate();
}
