package com.gfg.ewallet.service;

import com.gfg.ewallet.domain.User;
import com.gfg.ewallet.service.resource.UserRequest;
import com.gfg.ewallet.service.resource.UserResponse;

public interface UserService {

    public void addUser(UserRequest userRequest);

    public UserResponse getUser(String userId);

    public void deleteUser(String userId);

    public UserResponse updateUser(UserRequest userRequest,String userId);
}
