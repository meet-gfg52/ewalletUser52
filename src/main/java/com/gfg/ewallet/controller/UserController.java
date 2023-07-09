package com.gfg.ewallet.controller;

import com.gfg.ewallet.service.UserService;
import com.gfg.ewallet.service.resource.UserRequest;
import com.gfg.ewallet.service.resource.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody  UserRequest userRequest){
        userService.addUser(userRequest);
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @GetMapping(value = "{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId){
        UserResponse userResponse=userService.getUser(userId);
        return new ResponseEntity<>(userResponse,HttpStatus.OK);

    }

    @PutMapping(value = "{userId}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserRequest userRequest,@PathVariable String userId){
        UserResponse response=userService.updateUser(userRequest,userId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
