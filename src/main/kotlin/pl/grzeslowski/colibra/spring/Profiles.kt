package pl.grzeslowski.colibra.spring

import org.springframework.context.annotation.Profile

const val testProfileName = "test"

@Profile(testProfileName)
annotation class TestProfile

@Profile("!$testProfileName")
annotation class NotTestProfile
