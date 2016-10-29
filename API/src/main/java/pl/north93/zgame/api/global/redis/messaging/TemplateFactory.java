package pl.north93.zgame.api.global.redis.messaging;

public interface TemplateFactory
{
    <T> Template<T> createTemplate(TemplateManager messagePack, Class<T> clazz);
}
