package com.gfg.ewallet.service.resource;

import com.gfg.ewallet.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserResponse {

    private String userName;
    private String email;
    private String phoneNumber;
    private String balance;

    public void setUser(User user){
        this.userName=user.getUserName();
        this.email=user.getEmail();
        this.phoneNumber=user.getPhoneNumber();
    }

}
