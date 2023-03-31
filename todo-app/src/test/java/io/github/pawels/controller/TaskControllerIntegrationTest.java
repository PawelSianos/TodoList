package io.github.pawels.controller;

import io.github.pawels.model.Task;
import io.github.pawels.model.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private TaskRepository repo;


    @Test
    void httpGet_returnsGivenTask() throws Exception {
        // given
        int id = repo.save(new Task("foo", LocalDateTime.now())).getId();
        
        // when + then
        mockMvc.perform(get("/tasks/" + id))
                .andExpect(status().is2xxSuccessful());
    }
    
}
