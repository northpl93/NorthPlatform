package pl.north93.northplatform.api.global.serializer.platform;

import pl.north93.northplatform.api.global.serializer.platform.template.Template;
import pl.north93.northplatform.api.global.serializer.platform.template.TemplateEngine;

public interface TemplateFactory
{
    <T> Template<T, ?, ?> createTemplate(TemplateEngine templateEngine, Class<T> clazz);
}
