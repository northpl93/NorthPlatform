package pl.north93.zgame.controller.servers.scaler;

import static java.text.MessageFormat.format;


import javax.annotation.Nonnull;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.controller.servers.cfg.rules.RuleEntryConfig;
import pl.north93.zgame.controller.servers.cfg.rules.RulesConfig;
import pl.north93.zgame.controller.servers.groups.LocalGroupsManager;
import pl.north93.zgame.controller.servers.groups.LocalManagedServersGroup;
import pl.north93.zgame.controller.servers.scaler.value.IScalingValue;

public class RulesProcessor
{
    @Inject
    private Logger logger;
    @Inject
    private LocalGroupsManager localGroupsManager;

    @Bean
    private RulesProcessor()
    {
    }

    public void generateDecisionAndApply(final LocalManagedServersGroup serversGroup)
    {
        final ScalerDecision scalerDecision = this.generateDecision(serversGroup);

        if (scalerDecision != ScalerDecision.DO_NOTHING)
        {
            // nie spamimy konsoli jak nic nie robimy
            this.logger.log(Level.INFO, "Applying decision {0} for group {1}", new Object[]{scalerDecision, serversGroup.getName()});
        }

        scalerDecision.apply(serversGroup);
    }

    public @Nonnull ScalerDecision generateDecision(final LocalManagedServersGroup serversGroup)
    {
        final RulesConfig rules = serversGroup.getConfig().getRules();
        final ValueFetchCache values = new ValueFetchCache(serversGroup);

        ScalerDecision decision = ScalerDecision.DO_NOTHING;
        for (final RuleEntryConfig rule : rules.getRules())
        {
            final double value = values.getValue(rule.getValueId());
            final double comparedValue = rule.getComparedValue();

            if (! rule.getCondition().apply(value, comparedValue))
            {
                continue;
            }

            decision = ScalerDecision.fromConfig(rule.getAction());
        }

        if (this.checkIsDecisionValid(values, rules, decision))
        {
            // jak decyzja jest poprawna (sa odpowiednie ilosci serwerow) to ja zwracamy
            return decision;
        }

        // decyzja niepoprawna wiec tym razem nic nie robimy
        return ScalerDecision.DO_NOTHING;
    }

    private boolean checkIsDecisionValid(final ValueFetchCache cache, final RulesConfig rulesConfig, final ScalerDecision decision)
    {
        switch (decision)
        {
            case ADD_SERVER:
                return cache.getValue("serversCount") < rulesConfig.getMaxServers();
            case REMOVE_SERVER:
                return cache.getValue("serversCount") > rulesConfig.getMinServers();
            case DO_NOTHING:
                return true;
        }

        throw new IllegalArgumentException(decision.name());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

class ValueFetchCache
{
    @Inject
    private LocalGroupsManager localGroupsManager;
    private final LocalManagedServersGroup serversGroup;
    private final HashMap<String, Double> cache = new HashMap<>(4);

    ValueFetchCache(final LocalManagedServersGroup serversGroup)
    {
        this.serversGroup = serversGroup;
    }

    public double getValue(final String id)
    {
        return this.cache.computeIfAbsent(id, this::compute);
    }

    private double compute(final String id)
    {
        final IScalingValue scalingValue = this.localGroupsManager.getScalingValue(id);

        if (scalingValue == null)
        {
            throw new IllegalArgumentException(format("Scaling value with id {0} doesnt exists.", id));
        }

        return scalingValue.calculate(this.serversGroup);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("cache", this.cache).toString();
    }
}