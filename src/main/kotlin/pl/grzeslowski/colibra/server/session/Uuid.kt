package pl.grzeslowski.colibra.server.session

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import java.sql.Timestamp
import java.util.*

data class Uuid(val value: String)

data class Session(val timestamp: Long, val uuid: Uuid)

@Configuration
class UuidConfiguration {

    @Bean
    @Scope("prototype")
    fun uuid() = Uuid(UUID.randomUUID().toString())

    @Bean
    @Scope("prototype")
    fun session() = Session(System.currentTimeMillis(), Uuid(UUID.randomUUID().toString()))
}