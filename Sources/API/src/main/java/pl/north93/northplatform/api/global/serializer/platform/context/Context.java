package pl.north93.northplatform.api.global.serializer.platform.context;

import pl.north93.northplatform.api.global.serializer.platform.template.TemplateEngine;
import pl.north93.northplatform.api.global.serializer.platform.FieldInfo;

public abstract class Context
{
    private final TemplateEngine templateEngine;

    public Context(final TemplateEngine templateEngine)
    {
        this.templateEngine = templateEngine;
    }

    public final TemplateEngine getTemplateEngine()
    {
        return this.templateEngine;
    }

    public abstract void enterObject(FieldInfo field);

    public abstract void exitObject(FieldInfo field);
}
