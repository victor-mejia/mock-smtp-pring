package com.endava.bod.mocksmtp.controller;

import com.endava.bod.mocksmtp.Application;
import com.endava.bod.mocksmtp.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.Stream;
import io.vavr.control.Try;
import io.vavr.test.Arbitrary;
import io.vavr.test.Gen;
import io.vavr.test.Property;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class UserIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private Wiser wiser;

    @Value("${smtp.activation.from}")
    private String from;

    @Value("${smtp.activation.subject}")
    private String subject;

    @Value("${smtp.activation.message}")
    private String activationMessage;

    @Before
    public void setUp(){
        wiser = new Wiser();
        wiser.start();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() {
        wiser.stop();
    }

    @Test
    public void createUserTest() throws Exception {
        User newUser = new User("user1","user1@test.com");
        performCreateUser(newUser)
                .andExpect(status().is2xxSuccessful());
        // assert
        WiserAssertions.assertReceivedMessage(wiser)
                .from(from)
                .to(newUser.getEmail())
                .withSubject(subject);
    }

    @Test
    public void everyNewUserGetsActivationEmail(){
        Function<String,User> userGen = user ->  new User(user,user+"@test.com");
        Set<String> users = new HashSet<>();
        Arbitrary<User> newUsers = Arbitrary.string(Gen.choose('a','z'))
                .map(userGen)
                .filter(u -> !u.getUser().isEmpty() && !users.contains(u.getUser()))
                .peek(u -> performCreateUser(u))
                .peek(u -> users.add(u.getUser()));

        Property.def("All new users get a confirmation email")
                .forAll(newUsers)
                .suchThat(this::hasActivationEmail)
                .check(100,1000)
                .assertIsSatisfied();
    }

    private ResultActions performCreateUser(User u) {
        return Try.of(()-> mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(getUserJson(u))))
                .getOrElseThrow(e -> new RuntimeException(e));
    }

    private String getUserJson(User user) {
        ObjectMapper mapper = new ObjectMapper();
        return Try.of(()->mapper.writeValueAsString(user)).getOrElseThrow(e -> new RuntimeException(e));
    }

    private Boolean hasActivationEmail(User user){
        Predicate<WiserMessage> fromActivationAccount = m -> m.getEnvelopeSender().equalsIgnoreCase(from);
        Predicate<WiserMessage> hasMessage = m -> m.getEnvelopeReceiver().equalsIgnoreCase(user.getEmail());
        Predicate<WiserMessage> hasActSubject =
                m -> Try.of(() -> m.getMimeMessage().getSubject())
                        .map(s -> s.equalsIgnoreCase(subject))
                        .getOrElse(false);
        Predicate<WiserMessage> isActivationMessage =
                m -> Try.of(()-> m.getMimeMessage().getContent().toString())
                        .map(content -> content.contains("/api/users/"+user.getUser()))
                        .getOrElse(false);

        return Stream.ofAll(wiser.getMessages())
                .find(fromActivationAccount.and(hasMessage).and(hasActSubject).and(isActivationMessage))
                .map(m -> true)
                .getOrElse(false);
    }
}
