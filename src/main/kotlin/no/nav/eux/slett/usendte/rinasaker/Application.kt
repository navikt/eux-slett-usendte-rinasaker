package no.nav.eux.slett.usendte.rinasaker

import com.zaxxer.hikari.HikariDataSource
import no.nav.eux.logging.RequestIdMdcFilter
import no.nav.eux.slett.usendte.rinasaker.kafka.config.KafkaSslProperties
import no.nav.eux.slett.usendte.rinasaker.persistence.config.DataSourceProperties
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@EnableJwtTokenValidation(
    ignore = [
        "org.springframework",
        "org.springdoc"
    ]
)
@SpringBootApplication
@EnableConfigurationProperties(
    KafkaSslProperties::class,
    DataSourceProperties::class
)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@Configuration
class ApplicationConfig(
    val dataSourceProperties: DataSourceProperties
) {

    @Bean
    fun hikariDataSource() = HikariDataSource(dataSourceProperties.hikari)

    @Bean
    fun requestIdMdcFilter() = RequestIdMdcFilter()

}
