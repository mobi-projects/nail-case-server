package com.nailcase.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void accessPublicEndpointsWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/swagger-ui/index.html"))
			.andExpect(status().isOk());
		mockMvc.perform(get("/swagger"))
			.andExpect(status().is3xxRedirection());

	}
}
