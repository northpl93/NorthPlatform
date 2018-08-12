package pl.north93.zgame.api.global.serializer.platform.impl;

import static java.lang.reflect.Modifier.isAbstract;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import pl.north93.zgame.api.global.serializer.platform.ClassResolver;
import pl.north93.zgame.api.global.serializer.platform.TemplateFactory;
import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.ExactTypeIgnoreGenericFilter;
import pl.north93.zgame.api.global.serializer.platform.template.Template;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateFilter;
import pl.north93.zgame.api.global.serializer.platform.template.builtin.BooleanTemplate;
import pl.north93.zgame.api.global.serializer.platform.template.builtin.ByteTemplate;
import pl.north93.zgame.api.global.serializer.platform.template.builtin.DateTemplate;
import pl.north93.zgame.api.global.serializer.platform.template.builtin.DoubleTemplate;
import pl.north93.zgame.api.global.serializer.platform.template.builtin.DurationTemplate;
import pl.north93.zgame.api.global.serializer.platform.template.builtin.DynamicTemplate;
import pl.north93.zgame.api.global.serializer.platform.template.builtin.EnumTemplate;
import pl.north93.zgame.api.global.serializer.platform.template.builtin.FloatTemplate;
import pl.north93.zgame.api.global.serializer.platform.template.builtin.InstantTemplate;
import pl.north93.zgame.api.global.serializer.platform.template.builtin.IntegerTemplate;
import pl.north93.zgame.api.global.serializer.platform.template.builtin.LongTemplate;
import pl.north93.zgame.api.global.serializer.platform.template.builtin.ShortTemplate;
import pl.north93.zgame.api.global.serializer.platform.template.builtin.StringTemplate;

/*default*/ class TemplateEngineImpl implements TemplateEngine
{
    private final ClassResolver classResolver;
    private final TemplateFactory templateFactory = new TemplateFactoryImpl();
    private final Map<TemplateFilter, Template<?, ?, ?>> templates = new TreeMap<>();

    public TemplateEngineImpl(final ClassResolver classResolver)
    {
        this.classResolver = classResolver;

        // special default types
        this.register(new DynamicTemplate.DynamicTemplateFilter(), new DynamicTemplate());
        this.register(new EnumTemplate.EnumTemplateFilter(), new EnumTemplate());

        // simple default types
        this.register(new ExactTypeIgnoreGenericFilter(String.class), new StringTemplate());
        this.register(new ExactTypeIgnoreGenericFilter(Boolean.class), new BooleanTemplate());
        this.register(new ExactTypeIgnoreGenericFilter(Byte.class), new ByteTemplate());
        this.register(new ExactTypeIgnoreGenericFilter(Short.class), new ShortTemplate());
        this.register(new ExactTypeIgnoreGenericFilter(Integer.class), new IntegerTemplate());
        this.register(new ExactTypeIgnoreGenericFilter(Float.class), new FloatTemplate());
        this.register(new ExactTypeIgnoreGenericFilter(Double.class), new DoubleTemplate());
        this.register(new ExactTypeIgnoreGenericFilter(Long.class), new LongTemplate());

        // complex default types
        this.register(new ExactTypeIgnoreGenericFilter(Date.class), new DateTemplate());
        this.register(new ExactTypeIgnoreGenericFilter(Instant.class), new InstantTemplate());
        this.register(new ExactTypeIgnoreGenericFilter(Duration.class), new DurationTemplate());
    }

    @Override
    public Class<?> findClass(final String name)
    {
        return this.classResolver.findClass(name);
    }

    @Override
    public boolean isNeedsDynamicResolution(final Type type)
    {
        if (type instanceof Class)
        {
            final Class clazz = (Class) type;
            return this.isNeedsDynamicResolution(clazz);
        }
        else if (type instanceof ParameterizedType)
        {
            final ParameterizedType parameterizedType = (ParameterizedType) type;
            final Class clazz = (Class) parameterizedType.getRawType();
            return this.isNeedsDynamicResolution(clazz);
        }

        throw new IllegalArgumentException(type.getTypeName());
    }

    private boolean isNeedsDynamicResolution(final Class<?> clazz)
    {
        return clazz.isInterface() || isAbstract(clazz.getModifiers()) || clazz == Object.class;
    }

    @Override
    public void register(final TemplateFilter filter, final Template<?, ?, ?> template)
    {
        // TreeMap zapewnie od razu poprawne sortowanie po priorytecie
        this.templates.put(filter, template);
    }

    @Override
    public Template<Object, SerializationContext, DeserializationContext> getTemplate(final Type type)
    {
        System.out.println("getTemplate :: " + type.getTypeName());
        // iterujemy od najwyzszego priorytetu do najnizszego - TreeMap
        for (final Map.Entry<TemplateFilter, Template<?, ?, ?>> entry : this.templates.entrySet())
        {
            final TemplateFilter filter = entry.getKey();
            if (filter.isApplicableTo(this, type))
            {
                return this.genericCast(entry.getValue());
            }
        }

        if (type instanceof Class)
        {
            final Class<?> clazz = (Class<?>) type;

            final Template<?, ?, ?> template = this.templateFactory.createTemplate(this, clazz);
            this.register(new ExactTypeIgnoreGenericFilter(clazz), template);

            System.out.println("generating for class " + clazz);
            return this.genericCast(template);
        }

        throw new RuntimeException(type.getTypeName());
    }

    private void generateTemplate(final Type type)
    {

    }

    @SuppressWarnings("unchecked")
    private Template<Object, SerializationContext, DeserializationContext> genericCast(final Template<?, ?, ?> template)
    {
        return (Template<Object, SerializationContext, DeserializationContext>) template;
    }
}
