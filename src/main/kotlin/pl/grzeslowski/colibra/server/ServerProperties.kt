package pl.grzeslowski.colibra.server

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("server")
class ServerProperties {
    var port: Int = -1
    var timeoutInSec: Int = -1
}