package no.nav.eux.slett.usendte.rinasaker.persistence.config

import com.zaxxer.hikari.HikariConfig
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.datasource")
data class DataSourceProperties(
    var hikari: HikariConfig
)