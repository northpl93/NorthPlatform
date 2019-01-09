package pl.north93.northplatform.api.global.serializer.mongodb.template;

import java.lang.reflect.Type;
import java.util.Map;

import org.bson.Document;

import pl.north93.northplatform.api.global.serializer.platform.template.TemplateEngine;

public class MongoDbDocumentTemplate extends MongoDbMapTemplate
{
    @Override
    protected Type getValueGenericType(final TemplateEngine templateEngine, final Type type)
    {
        // Document nie posiada generic type wiec musimy to poprawic na Object
        return Object.class;
    }

    @Override
    protected Map<String, Object> instantiateMap(final TemplateEngine engine, final Type type)
    {
        // recznie tworzymy nowa instancje Document
        return new Document();
    }
}
