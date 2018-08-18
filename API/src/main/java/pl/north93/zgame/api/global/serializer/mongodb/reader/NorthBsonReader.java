package pl.north93.zgame.api.global.serializer.mongodb.reader;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonRegularExpression;
import org.bson.BsonType;

public class NorthBsonReader implements ITypesReader
{
    private final Deque<NorthReadingContext> contextStack = new ArrayDeque<>();

    public NorthBsonReader(final BsonDocument document)
    {
        this.contextStack.add(new DocumentNorthReadingContext(document));
    }

    public void enterObject(final String name)
    {
        if (this.isRootObject(name))
        {
            return; // root object
        }

        final NorthReadingContext currentContext = this.getCurrentContext();
        this.contextStack.add(currentContext.enterObject(name));
    }

    public void exitObject(final String name)
    {
        final NorthReadingContext context = this.contextStack.removeLast();
    }

    public void enterArray(final String name)
    {
        final NorthReadingContext currentContext = this.getCurrentContext();
        this.contextStack.add(currentContext.enterArray(name));
    }

    public void exitArray(final String name)
    {
        final NorthReadingContext context = this.contextStack.removeLast();
    }

    public boolean hasMore()
    {
        return this.getCurrentContext().hasMore();
    }

    @Override
    public boolean containsKey(final String name)
    {
        return this.getCurrentContext().containsKey(name);
    }

    @Override
    public Collection<String> getKeys()
    {
        return this.getCurrentContext().getKeys();
    }

    @Override
    public BsonType readType(final String name)
    {
        if (this.isRootObject(name))
        {
            return BsonType.DOCUMENT;
        }

        return this.getCurrentContext().readType(name);
    }

    @Override
    public String readString(final String name)
    {
        return this.getCurrentContext().readString(name);
    }

    @Override
    public boolean readBoolean(final String name)
    {
        return this.getCurrentContext().readBoolean(name);
    }

    @Override
    public int readInt32(final String name)
    {
        return this.getCurrentContext().readInt32(name);
    }

    @Override
    public double readDouble(final String name)
    {
        return this.getCurrentContext().readDouble(name);
    }

    @Override
    public long readInt64(final String name)
    {
        return this.getCurrentContext().readInt64(name);
    }

    @Override
    public BsonBinary readBinary(final String name)
    {
        return this.getCurrentContext().readBinary(name);
    }

    @Override
    public BsonRegularExpression readRegularExpression(final String name)
    {
        return this.getCurrentContext().readRegularExpression(name);
    }

    private NorthReadingContext getCurrentContext()
    {
        return this.contextStack.getLast();
    }

    private boolean isRootObject(final String name)
    {
        return name == null && this.contextStack.size() == 1;
    }
}
