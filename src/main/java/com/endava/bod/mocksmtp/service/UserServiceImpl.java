package com.endava.bod.mocksmtp.service;

import com.endava.bod.mocksmtp.domain.Activation;
import com.endava.bod.mocksmtp.domain.User;
import com.endava.bod.mocksmtp.util.UserUtil;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserService{

    private static final ConcurrentHashMap<String,User> usersDB = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String,Activation> activationsDB = new ConcurrentHashMap<>();

    @Value("${smtp.host}")
    private String host;

    @Value("${smtp.port}")
    private String port;

    @Value("${smtp.activation.from}")
    private String from;

    @Value("${smtp.activation.subject}")
    private String subject;

    @Value("${smtp.activation.message}")
    private String activationMessage;

    @Autowired
    private UserUtil userUtil;

    @Override
    public Try<Boolean> create(User user) {
        System.out.println(String.format("User: %s, email: %s, pass: %s", user.getUser(),user.getEmail(),user.getPass()));
        return Option.of(usersDB.get(user.getUser()))
                .map(u -> Try.<Tuple2<User,Activation>>failure(new RuntimeException("The user already exists "+user.getUser())))
                .getOrElse(Try.of(()-> saveUser(user)))
                .flatMap(tuple -> sendActivationEmail(tuple._1,tuple._2));
    }

    @Override
    public Try<Boolean> activate(Activation activation) {
        return Option.of(activationsDB.get(activation.getUser()))
                .map(a -> a.getCode().equalsIgnoreCase(activation.getCode()))
                .toTry(()-> new RuntimeException("Activation code not valid or doesn't exists."));
    }

    private Tuple2<User,Activation> saveUser(User user) {
        User newUser = new User();
        newUser.setUser(user.getUser());
        newUser.setEmail(user.getEmail());
        newUser.setPass(userUtil.generateDefaultPassword(user));
        newUser.setActivated(false);
        usersDB.put(newUser.getUser(), newUser);

        Activation activation = new Activation(userUtil.generateActivationCode(newUser),newUser.getUser());
        activationsDB.put(newUser.getUser(),activation);
        return Tuple.of(newUser,activation);
    }

    private Try<Boolean> sendActivationEmail(User user, Activation activation) {
        String to = user.getEmail();
        String content = activationMessage
                .replace("{{link}}",userUtil.generateActivationLink(activation))
                .replace("{{password}}",user.getPass());

        return Try.of(() -> getActivationMessage(from,to,subject,content))
                .andThenTry(Transport::send)
                .map(message -> true);
    }

    private MimeMessage getActivationMessage(String from, String to, String subject, String content) throws MessagingException {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);
        MimeMessage message = new MimeMessage(Session.getDefaultInstance(properties));
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(content);
        return message;
    }
}
