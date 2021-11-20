package org.mall.controller;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mall.dto.UserLoginDTO;
import org.mall.dto.UserOnlineDTO;
import org.mall.dto.UserRegisterDTO;
import org.mall.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class UserControllerTests {
   @Autowired
   TestRestTemplate restTemplate;

   @Test
   public void testDoLogin1() {
      // 用户登录测试用例1
      // 期望: 登录成功
      UserOnlineDTO userOnlineDTO = sendUserLoginPost("/user/login",
              formLogin(UserLoginDTO.builder().phoneNo("15817368796").password("123456").build()),
              ResultCode.SUCCESS, UserOnlineDTO.class);
      // 进一步校验数据正确性: 用户昵称是否正确
      assertThat(userOnlineDTO.getNickname()).isEqualTo("admin-001");
      // TODO: 能否检查SESSION是否设置
   }

   @Test
   public void testDoLogin2() {
      // 用户登录测试用例2
      // 期望: 登录失败, 手机号和密码校验错误
      List errorMsgs = sendUserLoginPost("/user/login",
              formLogin(UserLoginDTO.builder().phoneNo("000").password(null).build()),
              ResultCode.VALIDATE_FAILED, List.class);
      // 简单判断错误数量是否正确: 应当产生2个错误
      assertThat(errorMsgs.size()).isEqualTo(2);
   }

   ////////////////////////////////////////////////////////////////

   /**
    * 通过DTO对象构造表单信息(用户登录)
    * @param userLoginDTO 用户登录信息的数据传输对象
    * @return 登录表单信息
    */
   private MultiValueMap<String, Object> formLogin(UserLoginDTO userLoginDTO) {
      MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
      postParameters.add("phoneNo", userLoginDTO.getPhoneNo());
      postParameters.add("password", userLoginDTO.getPassword());
      return postParameters;
   }

   /**
    * 通过DTO对象构造表单信息(用户注册)
    * @param userRegisterDTO 户登录信息的数据传输对象
    * @return 注册表单信息
    */
   private MultiValueMap<String, Object> formRegister(UserRegisterDTO userRegisterDTO) {
      MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
      postParameters.add("nickname", userRegisterDTO.getNickname());
      postParameters.add("userName", userRegisterDTO.getUserName());
      postParameters.add("schoolCardID", userRegisterDTO.getSchoolCardID());
      postParameters.add("phoneNo", userRegisterDTO.getPhoneNo());
      postParameters.add("password", userRegisterDTO.getPassword());
      postParameters.add("wechatID", userRegisterDTO.getWechatID());
      postParameters.add("email", userRegisterDTO.getEmail());
      return postParameters;
   }

   /**
    * HTTP POST 发送用户请求, 并接收响应数据, 同时判断响应数据是否符合要求
    * (包括响应码是否正确, data数据类型是否正确; 其他数据细节可以通过返回的数据进一步分别校验)
    * 请求格式: application/x-www-form-urlencoded
    * 返回数据格式: application/json
    *
    * @param postParameters 表单信息
    * @param expectedResultCode 期望的响应码
    * @param expectedDataType 期望的响应数据类型(如果出现错误(如参数错误), 则应当为List.class, 因为可能存在多个错误)
    * @param <T> 响应数据类型
    * @return 非空的data数据, 在函数内已对数据判空
    */
   private <T> T sendUserLoginPost(String url, MultiValueMap<String, Object> postParameters,
                                   ResultCode expectedResultCode, Class<T> expectedDataType) {
      // 设置HTTP请求内容的格式: x-www-form-urlencoded
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      // headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);

      // 发送POST请求, 并接收返回的JSON字符串
      ResponseEntity<String> entity = this.restTemplate.postForEntity(url,
              new HttpEntity<>(postParameters, headers), String.class);

      // 能够响应成功, 说明URL正确并且服务端状态良好
      // 注: Controller成功返回的HttpStatus总是200 (不包括服务端启动失败、异常未处理、手动修改Http响应状态等情况)
      assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
      // 提取并解析JSON数据（要求不为空）
      String jsonResult = entity.getBody();
      log.info(jsonResult); // for DEBUG
      assertThat(jsonResult).isNotEmpty();
      ReadContext context = JsonPath.parse(jsonResult);
      // 响应码code要符合期望
      assertThat(context.read("$.code", Integer.class)).isEqualTo(expectedResultCode.getCode());
      // 数据data类型要符合期望
      T result = context.read("$.data", expectedDataType);
      assertThat(result).isNotNull();
      // 返回数据data, 以便于进一步判断结果是否符合要求
      return result;
   }
}
