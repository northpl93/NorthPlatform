package pl.north93.northplatform.restful.security;

import static spark.Spark.before;
import static spark.Spark.halt;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.utils.ConfigUtils;

public class ApiSecurity
{
    private final Logger logger = LoggerFactory.getLogger(ApiSecurity.class);
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
            this.logger.warn("Restful API security is DISABLED! Anyone can access our API.");
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
