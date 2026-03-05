package no.nav.eux.slett.usendte.rinasaker.webapp

import no.nav.eux.slett.usendte.rinasaker.Application
import no.nav.eux.slett.usendte.rinasaker.persistence.repository.RinasakStatusRepository
import no.nav.eux.slett.usendte.rinasaker.webapp.mock.RequestBodies
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@ActiveProfiles("test")
@SpringBootTest(
    classes = [Application::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@EnableMockOAuth2Server
@Testcontainers
@AutoConfigureRestTestClient
abstract class AbstractTest {

    companion object {

        @JvmStatic
        @Container
        val postgres: PostgreSQLContainer = PostgreSQLContainer(
            "postgres:15-alpine"
        )

        @JvmStatic
        @Container
        val kafka = KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"))

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.hikari.jdbc-url", postgres::getJdbcUrl)
            registry.add("spring.datasource.hikari.username", postgres::getUsername)
            registry.add("spring.datasource.hikari.password", postgres::getPassword)
            registry.add("kafka.bootstrap-servers", kafka::getBootstrapServers)
            registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers)
        }
    }

    @Autowired
    lateinit var mockOAuth2Server: MockOAuth2Server

    @Autowired
    lateinit var restTestClient: RestTestClient

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Autowired
    lateinit var rinasakStatusRepository: RinasakStatusRepository

    @Autowired
    lateinit var requestBodies: RequestBodies
}
