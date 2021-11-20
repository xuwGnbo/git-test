package org.mall.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
public class UserController2Tests {
//   @Autowired
   private MockMvc mockMvc;

//   @Test
   public void testLogin() throws Exception {
      mockMvc.perform(post("/users/login")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
              .param("phoneNo", "15817368796")
              .param("password", "123456"))
              .andDo(print())
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.code", is(400)))
              .andExpect(jsonPath("$.user").isNotEmpty())
              .andExpect(jsonPath("$.user.nickname").isNotEmpty())
              .andExpect(jsonPath("$.user.userImageUrl").isNotEmpty())
              .andExpect(jsonPath("$.user.uid").isNotEmpty());
   }
}
