package com.gfg.ewallet.service.impl;


import com.gfg.ewallet.EwalletUserException;
import com.gfg.ewallet.domain.User;
import com.gfg.ewallet.repository.UserRepository;
import com.gfg.ewallet.service.UserService;
import com.gfg.ewallet.service.resource.UserRequest;
import com.gfg.ewallet.service.resource.UserResponse;
import com.gfg.ewallet.service.resource.WalletResponse;
import lombok.extern.log4j.Log4j;
import org.hibernate.DuplicateMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Value("${user.create.topic}")
    private String userCreateTopic;

    @Value("${user.delete.topic}")
    private String userDeleteTopic;

    @Autowired
    RestTemplate restTemplate;

    @Value("${balance.url}")
    private String walletUri;

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public void addUser(UserRequest userRequest) {
        try {
            User user = userRequest.toUser();
            String hash = encoder.encode(user.getPasswordHash());
            user.setPasswordHash(hash);
            //logic to check if username is available;
            userRepository.save(user);
            kafkaTemplate.send(userCreateTopic, String.valueOf(user.getId()));
        } catch (DuplicateMappingException ex) {
            throw new EwalletUserException("EWALLET_USER_NOT_PROCESSED", "Please try again in sometime!");
        } catch (KafkaException ex) {
            throw new EwalletUserException("EWALLET_WALLET_NOT_CREATED", "Please try again in sometime!");
        } catch (Exception ex) {
            throw new EwalletUserException("EWALLET_SERVICE_EXCEPTION", "Please try again in sometime!");
        }
    }

    @Override
    public UserResponse getUser(String userId) {
        UserResponse userResponse = new UserResponse();
        try {
            Optional<User> optionalUser = userRepository.findById(Long.valueOf(userId));
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                userResponse.setUser(user);
                String url=walletUri+user.getId();
                ResponseEntity<WalletResponse> response = restTemplate.getForEntity(url, WalletResponse.class);
                if (!response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))) {
                    userResponse.setBalance("BALANCE_FAILURE");
                } else {
                    WalletResponse walletResponse = response.getBody();
                    userResponse.setBalance(String.valueOf(walletResponse.getBalance()));
                }
            }
        } catch (Exception ex) {
            throw new EwalletUserException("EWALLET_SERVICE_EXCEPTION", "Please try again in sometime!");
        }
        return userResponse;
    }

    @Override
    public void deleteUser(String userId) {
        try {
            userRepository.deleteById(Long.valueOf(userId));
            kafkaTemplate.send(userDeleteTopic, userId);
        } catch (DuplicateMappingException ex) {
            throw new EwalletUserException("EWALLET_USER_NOT_DELTED", "Please try again in sometime!");
        } catch (KafkaException ex) {
            throw new EwalletUserException("EWALLET_WALLET_NOT_DISABLED", "Please try again in sometime!");
        } catch (Exception ex) {
            throw new EwalletUserException("EWALLET_SERVICE_EXCEPTION", "Please try again in sometime!");
        }
    }

    @Override
    public UserResponse updateUser(UserRequest userRequest, String userId) {

        UserResponse response = null;
        try {
            Optional<User> optionalUser = userRepository.findById(Long.valueOf(userId));
            if (optionalUser.isEmpty()) {
                logger.error("No User found, throw Exception here");
            } else {
                User user = userRequest.toUser();
                String hash = encoder.encode(user.getPasswordHash());
                user.setPasswordHash(hash);
                //logic to check if username is available;
                userRepository.save(user);
                response.setUser(user);
            }

        } catch (DuplicateMappingException ex) {
            throw new EwalletUserException("EWALLET_USER_NOT_PROCESSED", "Please try again in sometime!");
        } catch (KafkaException ex) {
            throw new EwalletUserException("EWALLET_WALLET_NOT_CREATED", "Please try again in sometime!");
        } catch (Exception ex) {
            throw new EwalletUserException("EWALLET_SERVICE_EXCEPTION", "Please try again in sometime!");
        }
        return response;
    }
}
