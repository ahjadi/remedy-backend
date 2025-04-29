package com.alialjadi.remedybackend

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(properties = ["server.ssl.enabled=false"])
class RemedyBackendApplicationTests {

    @Test
    fun contextLoads() {
    }

}
