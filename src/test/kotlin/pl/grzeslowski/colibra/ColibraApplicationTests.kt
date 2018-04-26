package pl.grzeslowski.colibra

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import pl.grzeslowski.colibra.spring.testProfileName

@ExtendWith(SpringExtension::class)
@SpringBootTest
@ActiveProfiles(testProfileName)
class ColibraApplicationTests {

    @Test
    fun `should load spring boot context`() {
    }

}
