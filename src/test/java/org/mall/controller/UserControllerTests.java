package org.mall.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mall.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
// 2 RANDOM_PORT 会启动 Tomcat 模拟生产环境，接收Http请求
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// 3 引入Spring上下文，不启动Tomcat, 由 MockMVC 发送请求
// @AutoConfigureMockMvc 启动自动配置 MockMvc
// mockmvc可执行 http client 的功能
// print 打印 mock http 详细信息
// console 没有打印 Tomcat日志信息，Tomcat 不启动
//
// @SpringBootTest    // full Spring application context is started
// @AutoConfigureMockMvc
//
// 4 只引入Web 层的Spring上下文，不启动Tomcat, 由 MockMVC 发送请求
// @WebMvcTest(xxxController.class)
// ... { @Autowired xxxController, 不能注入 xxxService (不属于web层)
// 如果xxxController依赖Service, 则用@MockBean // mock 伪造一个xxxService bean
public class UserControllerTests {
   // 2 作为 Http Client
   @Autowired
   TestRestTemplate restTemplate;

   // 1
   @Autowired
   UserController userController;

   // 1
   public void testControllerExists() {
      Assert.assertNotNull(userController);
   }

   @Test
   public void testGetLoginPage() {
      //ResponseEntity<String> entity = restTemplate.getForEntity("/user/login", String.class);
      //assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
      //assertThat(entity.getBody()).isNotBlank();
      assertThat(restTemplate.getForObject("/user/login", String.class)).isNotEmpty();
   }

   @Test
   public void testGetRegisterPage() {
      assertThat(restTemplate.getForObject("/user/register", String.class)).isNotEmpty();
   }

   @Test
   public void testDoLogin1() {
      // User类需要使用 @Builder 注解
      // User user = User.builder().nickname("tester001").phoneNo("123456789012").password("1230").build();

      HttpHeaders headers = new HttpHeaders();
      headers.add("Content-Type", "application/x-www-form-urlencoded");

      MultiValueMap<String,Object> postParameters = new LinkedMultiValueMap<>();
      postParameters.add("nickname", "tester001");
      postParameters.add("phoneNo", "123456789012");
      postParameters.add("password", "1230");

      HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(postParameters, headers);

      ResponseEntity<String> entity = restTemplate.postForEntity("/user/register", httpEntity, String.class);

      System.out.println(entity.getStatusCode());
      System.out.println(entity.getHeaders());
      System.out.println(entity.getBody());
      assertThat(entity.getBody()).isNotEmpty();
//      assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.PERMANENT_REDIRECT);
   }

//   @Test
   public void testLogin() {
      ResponseEntity<String> entity;

      // 页面
      entity = restTemplate.getForEntity("/users/login", String.class);
      assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

      // 登录
      HttpHeaders headers = new HttpHeaders();
      headers.add("Content-Type", "application/x-www-form-urlencoded");
      MultiValueMap<String,Object> postParameters = new LinkedMultiValueMap<>();
      postParameters.add("phoneNo", "15817368796");
      postParameters.add("password", "123456");
      entity = restTemplate.postForEntity("/users/login", new HttpEntity<>(postParameters, headers), String.class);
      assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(entity.getBody()).isNotEmpty();
//      jsonPath("")
   }

//   @Test
   public void testRegister() {

   }
}
