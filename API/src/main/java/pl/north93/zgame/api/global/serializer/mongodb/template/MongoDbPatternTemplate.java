package pl.north93.zgame.api.global.serializer.mongodb.template;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.BsonRegularExpression;
import org.bson.BsonWriter;

import pl.north93.zgame.api.global.serializer.mongodb.MongoDbDeserializationContext;
import pl.north93.zgame.api.global.serializer.mongodb.MongoDbSerializationContext;
import pl.north93.zgame.api.global.serializer.platform.FieldInfo;
import pl.north93.zgame.api.global.serializer.platform.template.Template;

public class MongoDbPatternTemplate implements Template<Pattern, MongoDbSerializationContext, MongoDbDeserializationContext>
{
    @Override
    public void serialise(final MongoDbSerializationContext context, final FieldInfo field, final Pattern object) throws Exception
    {
        final BsonWriter writer = context.getWriter();
        context.writeNameIfNeeded(field);

        writer.writeRegularExpression(new BsonRegularExpression(object.pattern(), getOptionsAsString(object)));
    }

    @SuppressWarnings("MagicConstant")
    @Override
    public Pattern deserialize(final MongoDbDeserializationContext context, final FieldInfo field) throws Exception
    {
        final BsonRegularExpression regularExpression = context.readRegularExpression(field);
        return Pattern.compile(regularExpression.getPattern(), getOptionsAsInt(regularExpression));
    }

    private static String getOptionsAsString(final Pattern pattern)
    {
        int flags = pattern.flags();
        final StringBuilder buf = new StringBuilder();

        for (final RegexFlag flag : RegexFlag.values())
        {
            if ((pattern.flags() & flag.javaFlag) > 0)
            {
                buf.append(flag.flagChar);
                flags -= flag.javaFlag;
            }
        }

        if (flags > 0)
        {
            throw new IllegalArgumentException("some flags could not be recognized.");
        }

        return buf.toString();
    }

    private static int getOptionsAsInt(final BsonRegularExpression regularExpression)
    {
        int optionsInt = 0;

        String optionsString = regularExpression.getOptions();
        if (optionsString == null || optionsString.length() == 0)
        {
            return optionsInt;
        }

        optionsString = optionsString.toLowerCase();

        for (int i = 0; i < optionsString.length(); i++)
        {
            final RegexFlag flag = RegexFlag.getByCharacter(optionsString.charAt(i));
            if (flag != null)
            {
                optionsInt |= flag.javaFlag;
                if (flag.unsupported != null)
                {
                    // TODO: deal with logging
                    // warnUnsupportedRegex( flag.unsupported );
                }
            }
            else
            {
                // TODO: throw a better exception here
                throw new IllegalArgumentException("unrecognized flag [" + optionsString.charAt(i) + "] " + (int) optionsString.charAt(i));
            }
        }
        return optionsInt;
    }


    private static final int GLOBAL_FLAG = 256;

    private enum RegexFlag
    {
        CANON_EQ(Pattern.CANON_EQ, 'c', "Pattern.CANON_EQ"),
        UNIX_LINES(Pattern.UNIX_LINES, 'd', "Pattern.UNIX_LINES"),
        GLOBAL(GLOBAL_FLAG, 'g', null),
        CASE_INSENSITIVE(Pattern.CASE_INSENSITIVE, 'i', null),
        MULTILINE(Pattern.MULTILINE, 'm', null),
        DOTALL(Pattern.DOTALL, 's', "Pattern.DOTALL"),
        LITERAL(Pattern.LITERAL, 't', "Pattern.LITERAL"),
        UNICODE_CASE(Pattern.UNICODE_CASE, 'u', "Pattern.UNICODE_CASE"),
        COMMENTS(Pattern.COMMENTS, 'x', null);

        private static final Map<Character, RegexFlag> BY_CHARACTER = new HashMap<>();

        private final int    javaFlag;
        private final char   flagChar;
        private final String unsupported;

        static
        {
            for (final RegexFlag flag : values())
            {
                BY_CHARACTER.put(flag.flagChar, flag);
            }
        }

        public static RegexFlag getByCharacter(final char ch)
        {
            return BY_CHARACTER.get(ch);
        }

        RegexFlag(final int f, final char ch, final String u)
        {
            this.javaFlag = f;
            this.flagChar = ch;
            this.unsupported = u;
        }
    }
}
