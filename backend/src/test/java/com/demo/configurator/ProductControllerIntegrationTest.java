package com.demo.configurator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ProductControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void listsSeededProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Custom Laptop Pro")));
    }

    @Test
    void productDetailIncludesOptionGroups() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("optionGroups")));
    }

    @Test
    void configuresValidSelection() throws Exception {
        String body = "{\"productId\":1,\"selectedOptionIds\":[1,4,7,10,13,16],\"sessionId\":\"test-session\"}";
        mockMvc.perform(post("/api/configurations")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("VALIDATED")));
    }

    @Test
    void rejectsIncompatibleSelection() throws Exception {
        String body = "{\"productId\":1,\"selectedOptionIds\":[1,4,7,12,13,16],\"sessionId\":\"test-session\"}";
        mockMvc.perform(post("/api/configurations")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
