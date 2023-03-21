package com.nisum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisum.model.BearerToken;
import com.nisum.model.LoginDto;
import com.nisum.model.Phone;
import com.nisum.model.User;
import com.nisum.repository.PhoneRepository;
import com.nisum.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;

import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = NisumtestApplication.class)
@AutoConfigureMockMvc
class NisumtestApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUpTest() {
        phoneRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * Se comprueba la capa de seguridad tratando de autenticarse con un password incorrecto.
     * @throws Exception
     */
    @Test
    void integrationTestAuthenticationError() throws Exception {

        User user = generateUser();

        mvc.perform(post("/apiv1/jwt/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tokenType", is("Bearer ")));

        LoginDto loginDto = generateLoginDto();
        loginDto.setPassword("incorrect");

        mvc.perform(post("/apiv1/jwt/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().is4xxClientError())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensaje", is("Bad credentials")));
    }

    /**
     * Test de integracion completo. Desde el registro del usuario, autenticacion y obtencion de info
     * a través de la capa de seguridad.
     * @throws Exception
     */
    @Test
    void integrationTestAuthenticationOk() throws Exception {

        User user = generateUser();

        mvc.perform(post("/apiv1/jwt/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tokenType", is("Bearer ")));

        LoginDto loginDto = generateLoginDto();

        var resultActions = mvc.perform(post("/apiv1/jwt/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tokenType", is("Bearer ")));


        resultActions.andDo(result -> {
            var token = objectMapper.readValue(result.getResponse().getContentAsString(), BearerToken.class);

            mvc.perform(get("/apiv1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token.getAccessToken()))
                    .andExpect(status().isOk())
                    .andExpect(content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].email", is("linkstereo@gmail.com")));
        });
    }

    private User generateUser(){
        User user = new User();
        user.setName("Andres Marin Castelblanco");
        user.setEmail("linkstereo@gmail.com");
        user.setPassword("hardpass1234");

        var phones = new HashSet<Phone>();
        var phone1 = new Phone();
        phone1.setNumber("56953185");
        phone1.setCityCode("11");
        phone1.setCountryCode("54");
        phones.add(phone1);
        user.setPhones(phones);

        return user;
    }

    private LoginDto generateLoginDto(){
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("linkstereo@gmail.com");
        loginDto.setPassword("hardpass1234");

        return loginDto;
    }


}
