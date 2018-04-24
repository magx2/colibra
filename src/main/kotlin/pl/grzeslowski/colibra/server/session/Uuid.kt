package pl.grzeslowski.colibra.server.session

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import java.util.*

data class Uuid(val value: String)

@Configuration
class UuidConfiguration {

    @Bean
    @Scope("prototype")
    fun uuid() = Uuid(UUID.randomUUID().toString())
}