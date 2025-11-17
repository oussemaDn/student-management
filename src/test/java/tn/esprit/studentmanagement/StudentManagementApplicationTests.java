package tn.esprit.studentmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource; // Import this

@SpringBootTest
@TestPropertySource(properties = {
        // Override the default MySQL configuration with H2 in-memory
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class StudentManagementApplicationTests {

    @Test
    void contextLoads() {
        // This test now successfully loads the application context
        // using the in-memory H2 database.
    }

}