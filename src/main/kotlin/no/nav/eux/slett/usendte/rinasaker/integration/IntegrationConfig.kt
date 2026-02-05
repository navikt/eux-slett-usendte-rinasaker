package no.nav.eux.slett.usendte.rinasaker.integration

import no.nav.security.token.support.client.core.ClientProperties
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient

@EnableOAuth2Client(cacheEnabled = true)
@Configuration
class IntegrationConfig {

    @Bean
    fun euxRinaTerminatorApiRestClient(
        clientConfigurationProperties: ClientConfigurationProperties,
        oAuth2AccessTokenService: OAuth2AccessTokenService
    ): RestClient {
        val clientProperties: ClientProperties = clientConfigurationProperties
            .registration["eux-rina-terminator-api-credentials"]
            ?: throw RuntimeException("could not find oauth2 client config for eux-rina-terminator-api-credentials")
        
        return RestClient.builder()
            .requestInterceptor(bearerTokenInterceptor(clientProperties, oAuth2AccessTokenService))
            .build()
    }

    fun bearerTokenInterceptor(
        clientProperties: ClientProperties,
        oAuth2AccessTokenService: OAuth2AccessTokenService
    ) = ClientHttpRequestInterceptor { request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution ->
        val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
        request.headers.setBearerAuth(response.access_token!!)
        execution.execute(request, body)
    }
}
