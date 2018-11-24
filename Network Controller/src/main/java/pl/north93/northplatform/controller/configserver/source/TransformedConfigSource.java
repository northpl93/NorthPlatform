package pl.north93.northplatform.controller.configserver.source;

import java.util.function.Function;

public class TransformedConfigSource<SOURCE, TARGET> implements IConfigSource<TARGET>
{
    private final IConfigSource<SOURCE>    source;
    private final Class<TARGET>            targetClass;
    private final Function<SOURCE, TARGET> mapper;

    public TransformedConfigSource(final IConfigSource<SOURCE> source, final Class<TARGET> targetClass, final Function<SOURCE, TARGET> mapper)
    {
        this.source = source;
        this.targetClass = targetClass;
        this.mapper = mapper;
    }

    @Override
    public Class<TARGET> getType()
    {
        return this.targetClass;
    }

    @Override
    public TARGET load()
    {
        return this.mapper.apply(this.source.load());
    }
}
