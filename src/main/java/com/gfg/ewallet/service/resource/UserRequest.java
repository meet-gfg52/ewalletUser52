package com.gfg.ewallet.service.resource;

import com.gfg.ewallet.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    public String userName;
    public String email;
    public String phoneNumber;
    public String password;

    public User toUser(){
        return User.builder().userName(userName).email(email).passwordHash(password).phoneNumber(phoneNumber).build();
    }

}
