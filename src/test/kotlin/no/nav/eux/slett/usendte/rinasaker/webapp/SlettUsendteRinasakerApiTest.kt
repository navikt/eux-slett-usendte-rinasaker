package no.nav.eux.slett.usendte.rinasaker.webapp

import no.nav.eux.slett.usendte.rinasaker.Application
import no.nav.eux.slett.usendte.rinasaker.kafka.model.case.KafkaRinaCase
import no.nav.eux.slett.usendte.rinasaker.kafka.model.case.KafkaRinaCasePayload
import no.nav.eux.slett.usendte.rinasaker.kafka.model.case.KafkaRinaCaseRestCase
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.lang.Thread.sleep


@ActiveProfiles("test")
@SpringBootTest(
    classes = [Application::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@EnableMockOAuth2Server
@Testcontainers
class SlettUsendteRinasakerApiTest {

    companion object {

        @Container
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer(
            "postgres:15-alpine"
        )

        @Container
        val kafka = KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.6.1")
        )

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

    val <T> T.httpEntity: HttpEntity<T>
        get() = httpEntity(mockOAuth2Server)

    fun httpEntity() = voidHttpEntity(mockOAuth2Server)

    @Autowired
    lateinit var mockOAuth2Server: MockOAuth2Server

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Test
    fun heyo() {
        println("heyo")
        assertThat(kafka.isRunning).isTrue
        assertThat(postgres.isRunning).isTrue
        val event = KafkaRinaCase("a", KafkaRinaCasePayload(KafkaRinaCaseRestCase(1, "2")))
        kafkaTemplate.send("eessibasis.eux-rina-case-events-v1", "a", event)
        sleep(1000)
    }
}
