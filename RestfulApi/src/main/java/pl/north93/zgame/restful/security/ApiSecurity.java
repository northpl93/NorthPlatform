package pl.north93.zgame.restful.security;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;
import static spark.Spark.before;
import static spark.Spark.halt;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ApiSecurity
{
    @Inject
    private ApiCore   api;
    private ApiConfig apiConfig;

    public ApiSecurity()
    {
        this.apiConfig = loadConfigFile(ApiConfig.class, this.api.getFile("rest-api.yml"));
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
