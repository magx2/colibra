package pl.grzeslowski.colibra

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ColibraApplication

fun main(args: Array<String>) {
    runApplication<ColibraApplication>(*args)
}
