package com.gfg.ewallet.service.impl;


import com.gfg.ewallet.domain.User;
import com.gfg.ewallet.repository.UserRepository;
import com.gfg.ewallet.service.UserService;
import com.gfg.ewallet.service.resource.UserRequest;
import com.gfg.ewallet.service.resource.UserResponse;
import com.gfg.ewallet.service.resource.WalletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    private  String userCreateTopic;

    @Value("${user.delete.topic}")
    private  String userDeleteTopic;

    @Autowired
    RestTemplate restTemplate;

    @Value("${balance.url}")
    private String walletUri;


    @Override
    public void addUser(UserRequest userRequest) {
        User user=userRequest.toUser();
        String hash=encoder.encode(user.getPasswordHash());
        user.setPasswordHash(hash);
        //logic to check if username is available;
        userRepository.save(user);
        kafkaTemplate.send(userCreateTopic,String.valueOf(user.getId()));
    }

    @Override
    public UserResponse getUser(String userId) {
        UserResponse userResponse=null;
        Optional<User> optionalUser=userRepository.findById(Long.valueOf(userId));
        if(optionalUser.isPresent()){
            User user=optionalUser.get();
            userResponse.setUser(user);
            ResponseEntity<WalletResponse> response=restTemplate.getForEntity(walletUri,WalletResponse.class);
            if(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))){
                userResponse.setBalance("BALANCE_FAILURE");
            }
            else{
                WalletResponse walletResponse=response.getBody();
                userResponse.setBalance(String.valueOf(walletResponse.getBalance()));
            }
        }
        return userResponse;
    }

    @Override
    public void deleteUser(String userId) {

    }

    @Override
    public void updateUser(UserRequest userRequest) {

    }
}
