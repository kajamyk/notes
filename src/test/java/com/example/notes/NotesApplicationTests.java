package com.example.notes;

import com.example.notes.core.Registration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NotesApplicationTests {

    @Test
    void contextLoads() {
        Registration registration = new Registration();
        System.out.println(registration.isPasswordEntropySufficient("qwertyhgfd]'.,s'/.a"));
    }

}
