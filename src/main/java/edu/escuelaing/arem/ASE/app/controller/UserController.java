package edu.escuelaing.arem.ASE.app.controller;

import edu.escuelaing.arem.ASE.app.service.UserService;
import edu.escuelaing.arem.ASE.app.tools.*;

@RestController
public class UserController {

    public static UserService userService = new UserService();

    @GetMapping("/users")
    public static Response<String> getAllUsers(){ return userService.allUsers();}

    @GetMapping("/user")
    public static Response<String> getUser(@RequestParam(value = "name") String name){return userService.getUser(name);}
}
