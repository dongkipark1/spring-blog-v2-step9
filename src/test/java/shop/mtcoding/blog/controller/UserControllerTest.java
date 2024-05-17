package shop.mtcoding.blog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import shop.mtcoding.blog.user.UserRequest;

/**
 *  1. 통합 테스트 (스프링의 모든 bin을 IoC에 등록하고 테스트 하는 것)
 *  2. 배포직전 최종 테스트
 */

@AutoConfigureMockMvc // MockMvc IoC 로드
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // 1. 가짜 객체 띄우기 (모든 빈 IoC 로드)
public class UserControllerTest {

    private ObjectMapper om = new ObjectMapper(); // 모든 메서드를 전역적으로 쓴다.

    @Autowired
    private MockMvc mvc;

    @Test
    public void Join_test() throws Exception {
        //given
        UserRequest.JoinDTO reqDTO = new UserRequest.JoinDTO();
        reqDTO.setUsername("haha");
        reqDTO.setPassword("1234");
        reqDTO.setEmail("haha@nate.com");
        // 통합테스트로 객체로 만들어서 reqDTO를 메시지 컨버팅해서 json으로 던짐
        String reqBody = om.writeValueAsString(reqDTO);
//        System.out.println("reqBody: "+reqBody);

        //when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders.post("/join")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON) // contenttype이 확인이 되어야 json으로 컨버팅 가능

        ); // 이걸 만으로는 테스트 확인이 안됨 (resultaction 추가)

        //eye
        String respBody = actions.andReturn().getResponse().getContentAsString();
//        int statusCode = actions.andReturn().getResponse().getStatus();
        System.out.println("respBody: "+ respBody);
//        System.out.println("statusCode: "+ statusCode);

        //then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200)); // assertion 확인
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공")); // assertion 확인
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.id").value(4)); // assertion 확인
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.username").value("haha")); // assertion 확인
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.email").value("haha@nate.com")); // assertion 확인
    }

    @Test
    public void join_username_same_fail_test() throws Exception {
        // given
        UserRequest.JoinDTO reqDTO = new UserRequest.JoinDTO();
        reqDTO.setUsername("ssar");
        reqDTO.setPassword("1234");
        reqDTO.setEmail("ssar@nate.com");

        String reqBody = om.writeValueAsString(reqDTO);
        //System.out.println("reqBody : "+reqBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders.post("/join")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // eye
        String respBody = actions.andReturn().getResponse().getContentAsString();
        //int statusCode = actions.andReturn().getResponse().getStatus();
        //System.out.println("respBody : "+respBody);
        //System.out.println("statusCode : "+statusCode);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("중복된 유저네임입니다"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body").isEmpty());
    }

    // {"status":400,"msg":"영문/숫자 2~20자 이내로 작성해주세요 : username","body":null}

    @Test
    public void join_username_valid_fail_test() throws Exception {
        // given
        UserRequest.JoinDTO reqDTO = new UserRequest.JoinDTO();
        reqDTO.setUsername("김완준");
        reqDTO.setPassword("1234");
        reqDTO.setEmail("ssar@nate.com");

        String reqBody = om.writeValueAsString(reqDTO);
        //System.out.println("reqBody : "+reqBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders.post("/join")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // eye
        String respBody = actions.andReturn().getResponse().getContentAsString();
        //int statusCode = actions.andReturn().getResponse().getStatus();
        //System.out.println("respBody : "+respBody);
        //System.out.println("statusCode : "+statusCode);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("영문/숫자 2~20자 이내로 작성해주세요 : username"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body").isEmpty());
    }

    @Test
    public void login_success_test() throws Exception {
        // given
        UserRequest.LoginDTO reqDTO = new UserRequest.LoginDTO();
        reqDTO.setUsername("ssar");
        reqDTO.setPassword("1234");

        String reqBody = om.writeValueAsString(reqDTO);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders.post("/login")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        String respBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println("respBody : "+respBody);

        // then
        actions.andExpect(MockMvcResultMatchers.status().isOk()); // header 검증

        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body").isEmpty());

    }
}
