package de.cofinpro.sales.voucher.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.cofinpro.sales.voucher.model.User;
import de.cofinpro.sales.voucher.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final Resource userResource = new ClassPathResource("test_users.json");

    private final UserService userService;

    @Autowired
    public DataLoader(UserService userService) {
        super();
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            List<User> users = mapper.readValue(userResource.getInputStream(), new TypeReference<>() {});

            users.stream()
                    .filter(user -> userService.findByEmail(user.getEmail()).isEmpty())
                    .forEach(userService::create);

        } catch (Exception e) {
            log.error("An exception occurred while adding test user: {}", e.getMessage());
        }
    }
}
