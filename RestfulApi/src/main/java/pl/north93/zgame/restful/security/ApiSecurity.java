package pl.north93.zgame.restful.security;

import static spark.Spark.before;
import static spark.Spark.halt;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.utils.ConfigUtils;

public class ApiSecurity
{
    @Inject
    private ApiCore   api;
    private ApiConfig apiConfig;

    public ApiSecurity()
    {
        this.apiConfig = ConfigUtils.loadConfig(ApiConfig.class, "rest-api.xml");
    }

    public void setupSecurity()
    {
        if (! this.apiConfig.isSecurityEnabled())
        {
            this.api.getLogger().warning("Restful API security is DISABLED! Anyone can access our API.");
            return;
        }

        before((request, response) ->
        {
            final String token = request.headers("X-API-TOKEN");
            if (this.apiConfig.getTokens().contains(token))
            {
                return;
            }
            halt(403);
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("apiConfig", this.apiConfig).toString();
    }
}
