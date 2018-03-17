package pl.north93.zgame.api.global.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.common.base.Preconditions;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import pl.north93.zgame.api.global.utils.Vars;

class ComplexTranslatableString extends TranslatableString
{
    private final TranslatableString string1;
    private final TranslatableString string2;
    
    ComplexTranslatableString(TranslatableString string1, TranslatableString string2)
    {
        Preconditions.checkState(string1 != null);
        Preconditions.checkState(string2 != null);
        
        this.string1 = string1;
        this.string2 = string2;
    }
    
    @Override
    public BaseComponent getValue(Locale locale, Vars<Object> params)
    {
        return inOrder().stream().map(translatableString -> translatableString.getValue(locale, params)).reduce(new TextComponent(), (l, r) -> {
            l.addExtra(r);
            return l;
        });
    }
    
    private List<TranslatableString> inOrder()
    {
        List<TranslatableString> list = new ArrayList<>();
        inOrder(list);
        return list;
    }
    
    private void inOrder(List<TranslatableString> list)
    {
        if ( string1 instanceof ComplexTranslatableString )
        {
            ((ComplexTranslatableString) string1).inOrder(list);
        }
        else
        {
            list.add(string1);
        }
        
        if ( string2 instanceof ComplexTranslatableString )
        {
            ((ComplexTranslatableString) string2).inOrder(list);
        }
        else
        {
            list.add(string2);
        }
    }
    
    @Override
    public int hashCode()
    {
        return inOrder().hashCode();
    }
    
    @Override
    public boolean equals(Object object)
    {
        if ( this ==  object )
        {
            return true;
        }
        if ( object == null || object.getClass() != this.getClass() )
        {
            return false;
        }
        
        ComplexTranslatableString other = (ComplexTranslatableString) object;
        return this.inOrder().equals(other.inOrder());
    }

    @Override
    public String toString()
    {
        return "ComplexTranslatableString [inOrder=" + inOrder() + "]";
    }
}
