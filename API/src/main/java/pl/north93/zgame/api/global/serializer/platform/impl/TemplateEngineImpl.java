package pl.north93.zgame.api.global.serializer.platform.impl;

import static java.lang.reflect.Modifier.isAbstract;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import pl.north93.zgame.api.global.serializer.platform.ClassResolver;
import pl.north93.zgame.api.global.serializer.platform.InstanceCreator;
import pl.north93.zgame.api.global.serializer.platform.TemplateFactory;
import pl.north93.zgame.api.global.serializer.platform.TypePredictor;
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
    private final TypePredictor<SerializationContext, DeserializationContext> typePredictor;
    private final InstantiationManager instantiationManager = new InstantiationManager();
    private final ReadWriteLock templatesLock = new ReentrantReadWriteLock();
    private final TemplateFactory templateFactory = new TemplateFactoryImpl();
    private final Map<TemplateFilter, Template<?, ?, ?>> templates = new TreeMap<>();

    @SuppressWarnings("unchecked")
    public TemplateEngineImpl(final ClassResolver classResolver, final TypePredictor<?, ?> typePredictor)
    {
        this.classResolver = classResolver;
        this.typePredictor = (TypePredictor<SerializationContext, DeserializationContext>) typePredictor;

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
    public Class<?> getRawClassFromType(final Type type)
    {
        if (type instanceof Class)
        {
            return (Class<?>) type;
        }
        else if (type instanceof ParameterizedType)
        {
            final ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<?>) parameterizedType.getRawType();
        }

        throw new IllegalArgumentException(type.getTypeName());
    }

    @Override
    public Type[] getTypeParameters(final Type type)
    {
        if (type instanceof Class)
        {
            final Class clazz = (Class) type;
            final Type[] types = new Type[clazz.getTypeParameters().length];
            Arrays.fill(types, Object.class);
            return types;
        }
        else if (type instanceof ParameterizedType)
        {
            final ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getActualTypeArguments();
        }

        throw new IllegalArgumentException(type.getTypeName());
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

    @Override
    public boolean isTypePredictingSupported()
    {
        return this.typePredictor != null;
    }

    @Override
    public TypePredictor<SerializationContext, DeserializationContext> getTypePredictor()
    {
        return this.typePredictor;
    }

    @Override
    public <T> InstanceCreator<T> getInstanceCreator(final Class<T> clazz)
    {
        return this.instantiationManager.getInstanceCreator(clazz);
    }

    private boolean isNeedsDynamicResolution(final Class<?> clazz)
    {
        return clazz.isInterface() || isAbstract(clazz.getModifiers()) || clazz == Object.class;
    }

    @Override
    public void register(final TemplateFilter filter, final Template<?, ?, ?> template)
    {
        final Lock writeLock = this.templatesLock.writeLock();
        try
        {
            writeLock.lock();

            // TreeMap zapewnie od razu poprawne sortowanie po priorytecie
            this.templates.put(filter, template);
        }
        finally
        {
            writeLock.unlock();
        }
    }

    @Override
    public Template<Object, SerializationContext, DeserializationContext> getTemplate(final Type type)
    {
        // iterujemy od najwyzszego priorytetu do najnizszego - TreeMap
        final Lock readLock = this.templatesLock.readLock();
        try
        {
            readLock.lock();
            for (final Map.Entry<TemplateFilter, Template<?, ?, ?>> entry : this.templates.entrySet())
            {
                final TemplateFilter filter = entry.getKey();
                if (filter.isApplicableTo(this, type))
                {
                    return this.genericCast(entry.getValue());
                }
            }
        }
        finally
        {
            readLock.unlock();
        }

        if (type instanceof Class)
        {
            final Class<?> clazz = (Class<?>) type;

            final Template<?, ?, ?> template = this.templateFactory.createTemplate(this, clazz);
            this.register(new ExactTypeIgnoreGenericFilter(clazz), template);

            return this.genericCast(template);
        }

        throw new RuntimeException(type.getTypeName());
    }

    @SuppressWarnings("unchecked")
    private Template<Object, SerializationContext, DeserializationContext> genericCast(final Template<?, ?, ?> template)
    {
        return (Template<Object, SerializationContext, DeserializationContext>) template;
    }
}
