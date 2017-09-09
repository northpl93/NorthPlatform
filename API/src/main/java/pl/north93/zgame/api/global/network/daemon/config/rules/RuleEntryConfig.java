package pl.north93.zgame.api.global.network.daemon.config.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "value")
@XmlAccessorType(XmlAccessType.FIELD)
public class RuleEntryConfig
{
    @XmlAttribute(name = "id")
    private String    valueId;
    @XmlAttribute(name = "is")
    private Condition condition;
    @XmlAttribute(name = "than")
    private Double    comparedValue;
    @XmlAttribute(name = "then")
    private Action    action;

    public String getValueId()
    {
        return this.valueId;
    }

    public Condition getCondition()
    {
        return this.condition;
    }

    public double getComparedValue()
    {
        return this.comparedValue;
    }

    public Action getAction()
    {
        return this.action;
    }

    public enum Condition
    {
        SMALLER
                {
                    @Override
                    public boolean apply(final double a, final double b) // value is SMALLER than b
                    {
                        return a < b;
                    }
                },
        GREATER
                {
                    @Override
                    public boolean apply(final double a, final double b) // value is GREATER than b
                    {
                        return a > b;
                    }
                };

        public abstract boolean apply(double a, double b);
    }

    public enum Action
    {
        CREATE_SERVER,
        REMOVE_SERVER,
        NOTHING
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("valueId", this.valueId).append("condition", this.condition).append("comparedValue", this.comparedValue).append("action", this.action).toString();
    }
}
