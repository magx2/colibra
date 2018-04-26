package pl.grzeslowski.colibra

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import pl.grzeslowski.colibra.spring.NotTestProfile
import java.util.concurrent.TimeUnit

@SpringBootApplication
class ColibraApplication

fun main(args: Array<String>) {
    runApplication<ColibraApplication>(*args)
}

@NotTestProfile
@Component
class RuIt : CommandLineRunner {
    private val log = LoggerFactory.getLogger(RuIt::class.java)
    override fun run(vararg args: String?) {
        TimeUnit.MINUTES.sleep(10)
        log.info("Closing...")
    }

}