package pl.grzeslowski.colibra

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

@SpringBootApplication
class ColibraApplication

fun main(args: Array<String>) {
    runApplication<ColibraApplication>(*args)
}

@Component
class RuIt : CommandLineRunner {
    override fun run(vararg args: String?) {
        while (true) {
            Thread.sleep(1_000)
        }
    }

}