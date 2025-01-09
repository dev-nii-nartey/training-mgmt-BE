package com.specializationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@ActiveProfiles("test") // Optional: Use a separate application-test.properties for test configurations
class SpecializationServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
