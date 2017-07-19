package pl.north93.zgame.api.global.messages;

import java.util.Locale;
import java.util.Objects;

import pl.north93.zgame.api.global.utils.Vars;

class ConstantTranslatableString extends TranslatableString
{
    private final String fixedValue;
    
    ConstantTranslatableString(String fixedValue)
    {
        this.fixedValue = fixedValue;
    }
    
    @Override
    public String getValue(Locale locale, Vars<Object> params)
    {
        return fixedValue;
    }

    @Override
    public int hashCode()
    {
        return fixedValue.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if ( this == obj )
        {
            return true;
        }
        
        if ( obj == null || obj.getClass() != this.getClass() )
        {
            return false;
        }
        
        ConstantTranslatableString other = (ConstantTranslatableString) obj;
        return Objects.equals(other.fixedValue, this.fixedValue);
    }

    @Override
    public String toString()
    {
        return "ConstantTranslatableString [fixedValue=" + fixedValue + "]";
    }
}
