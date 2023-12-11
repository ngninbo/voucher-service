package de.cofinpro.sales.voucher;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cofinpro.sales.voucher.domain.UserDeletionResponse;
import de.cofinpro.sales.voucher.domain.UserDto;
import de.cofinpro.sales.voucher.domain.VoucherServiceCustomErrorMessage;
import de.cofinpro.sales.voucher.model.Role;
import de.cofinpro.sales.voucher.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs(outputDir = "target/snippets")
class VoucherServiceApplicationTests {

    private static final String API_ENDPOINT = "/api";

    @Autowired
    private WebApplicationContext context;

    private final Resource userResource = new ClassPathResource("user/test_user.json");

    private MockMvc mockMvc;

    private User userUpdateRequest;

    private String json;

    private ObjectMapper mapper;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) throws IOException {
        mapper = new ObjectMapper();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .apply(springSecurity())
                .build();

        json = userResource.getContentAsString(Charset.defaultCharset());

        userUpdateRequest = mapper.readValue(userResource.getInputStream(), User.class);
    }

    @Test
    @DisplayName("should complete initialization")
    void contextLoads() {
    }

    @Test
    @WithMockUser(username="test", password = "pass", authorities = {"ROLE_ADMIN"})
    public void sample() throws Exception {
        this.mockMvc.perform(get(API_ENDPOINT + "/voucher"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, world!")))
                .andDo(document("voucher"));
    }

    @Test
    @DisplayName("should add, update role and remove user with success")
    void signup() throws Exception {

        var result = this.mockMvc.perform(post(API_ENDPOINT + "/user/signup")
                        .characterEncoding(Charset.defaultCharset())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("user/signup")).andReturn();

        var response = mapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

        assertThat(response.getName(), is(userUpdateRequest.getName()));
        assertThat(response.getLastname(), is(userUpdateRequest.getLastname()));
        assertThat(response.getEmail(), is(userUpdateRequest.getEmail()));
        assertThat(response.getRole(), is(Role.SUPPORT));
        assertNotNull(response.getId());

        // Adding Same User
        this.mockMvc.perform(post(API_ENDPOINT + "/user/signup")
                        .characterEncoding(Charset.defaultCharset())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User already exist!")))
                .andDo(document("user/user-exist"));

        // Change Role
        String req = new ClassPathResource("user/update_role_ok.json").getContentAsString(Charset.defaultCharset());

        result = this.mockMvc.perform(put(API_ENDPOINT + "/user/role")
                        .with(user("test").password("pass").roles("ADMIN"))
                        .characterEncoding(Charset.defaultCharset())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user/update_role_ok")).andReturn();

        var roleUpdateResponse = mapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

        assertNotNull(roleUpdateResponse);
        assertThat(roleUpdateResponse.getEmail(), is(userUpdateRequest.getEmail()));
        assertThat(roleUpdateResponse.getRole(), is(Role.SALE));

        // Remove user
        result = this.mockMvc.perform(delete(API_ENDPOINT + "/user/{email}", userUpdateRequest.getEmail())
                        .with(user("test").password("pass").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Deleted successfully!")))
                .andDo(document("user/delete")).andReturn();

        var delResponse = mapper.readValue(result.getResponse().getContentAsString(), UserDeletionResponse.class);

        assertThat(delResponse.getStatus(), is(UserDeletionResponse.DEFAULT_STATUS));
        assertThat(delResponse.getEmail(), is(userUpdateRequest.getEmail()));
    }

    @ParameterizedTest(name = "should assert response contents {1} when change role of user from {0}")
    @CsvSource(value = {"user/update_role_admin.json, User can have only one role", "user/update_role_same.json, User already has the role"})
    void changeRole(String jsonPath, String expectedMsg) throws Exception {
        String req = new ClassPathResource(jsonPath).getContentAsString(Charset.defaultCharset());

        this.mockMvc.perform(put(API_ENDPOINT + "/user/role")
                        .with(user("test").password("pass").roles("ADMIN"))
                        .characterEncoding(Charset.defaultCharset())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(expectedMsg)))
                .andDo(document(jsonPath.split("\\.")[0]));
    }

    @Test
    @DisplayName("should return 403 when user is not authorized because of invalid role while getting list of user")
    void getList_Unauthorized() throws Exception {
        mockMvc.perform(get(API_ENDPOINT + "/user"))
                .andDo(print()).andExpect(status().isUnauthorized())
                .andDo(document("user/list-unauthorized"));
    }

    @Test
    @WithMockUser(username="test", password = "pass", authorities = {"ROLE_UNKNOWN"})
    @DisplayName("should return 403 when user is not authorized because of invalid role while getting list of user")
    void getList_access_denied() throws Exception {
        mockMvc.perform(get(API_ENDPOINT + "/user"))
                .andDo(print()).andExpect(status().isForbidden())
                .andDo(document("user/list-denied"));
    }

    @Test
    @WithMockUser(username="test", password = "pass", authorities = {"ROLE_ADMIN"})
    @DisplayName("should return 200 while getting list of user")
    void getList_ok() throws Exception {
        mockMvc.perform(get(API_ENDPOINT + "/user"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("user/list-ok"));
    }

    @Test
    @DisplayName("should return 401 when user is not authorized because of invalid credentials while deleting a user")
    void deleteUser_unauthorized() throws Exception {
        this.mockMvc.perform(delete(API_ENDPOINT + "/user/{email}", "notexisting.user@company.com"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andDo(document("user/delete-unauthorized")).andReturn();
    }

    @Test
    @WithMockUser(username="test", password = "pass", authorities = {"ROLE_UNKNOWN"})
    @DisplayName("should return 403 when user is not authorized because of invalid role while deleting a user")
    void deleteUser_access_denied() throws Exception {
        this.mockMvc.perform(delete(API_ENDPOINT + "/user/{email}", "notexisting.user@company.com"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andDo(document("user/delete-denied")).andReturn();
    }

    @Test
    @WithMockUser(username="test", password = "pass", authorities = {"ROLE_ADMIN"})
    @DisplayName("should return 404 when user not found while deleting a user")
    void deleteUser_not_found() throws Exception {
        String email = userUpdateRequest.getEmail();
        var resp = this.mockMvc.perform(delete(API_ENDPOINT + "/user/{email}", email))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("user/delete-not-found")).andReturn();

        String expectedMsg = String.format("User by email %s not found", email);
        var msg = mapper.readValue(resp.getResponse().getContentAsString(), VoucherServiceCustomErrorMessage.class);
        assertEquals(HttpStatus.NOT_FOUND.value(), msg.getStatus());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), msg.getError());
        assertEquals(expectedMsg, msg.getMessage());
        assertTrue(LocalDateTime.parse(msg.getTimestamp()).isBefore(LocalDateTime.now()));
        assertTrue(msg.getPath().contains(email));
    }

    @ParameterizedTest(name = "should return bad request and response containing {1} by change role with invalid request")
    @WithMockUser(username="test", password = "pass", authorities = {"ROLE_ADMIN"})
    @CsvSource(value = {
            "user/update_role_illegal.json, Role must be SUPPORT or SALE, user/role-bad-request",
            "user/update_role_illegal-2.json, Email from given domain not allowed, user/role-bad-request-email"})
    void changeRole(String jsonPath, String expectedMsg, String outputPath) throws Exception {
        String req = new ClassPathResource(jsonPath).getContentAsString(Charset.defaultCharset());

        this.mockMvc.perform(put(API_ENDPOINT + "/user/role")
                        .characterEncoding(Charset.defaultCharset())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(expectedMsg)))
                .andDo(document(outputPath));
    }
}
