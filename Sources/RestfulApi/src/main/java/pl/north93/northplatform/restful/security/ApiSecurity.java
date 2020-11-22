package pl.north93.northplatform.restful.security;

import static spark.Spark.before;
import static spark.Spark.halt;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiSecurity
{
    private final ApiConfig apiConfig;

    public ApiSecurity(final ApiConfig apiConfig)
    {
        this.apiConfig = apiConfig;
    }

    public void setupSecurity()
    {
        if (! this.apiConfig.isSecurityEnabled())
        {
            log.warn("Restful API security is DISABLED! Anyone can access our API.");
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
