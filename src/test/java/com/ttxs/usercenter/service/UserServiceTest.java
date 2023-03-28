package com.ttxs.usercenter.service;


import com.ttxs.usercenter.model.domain.User;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testAddUser() {
        User user = new User();
        user.setUsername("cwt");
        user.setUserAccount("123");
        user.setAvatarUrl("https://avatars.githubusercontent.com/u/104471028?v=4");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("128901226532");
        user.setEmail("cwt@cwt.com");

        boolean result = userService.save(user);
        System.out.println(user.getId());
        assertTrue(result);

    }

    @Test
    void userRegister() {
        String userAccount = "cwt";
        String userPassword = "123";
        String checkPassword = "123";
        String email = "666666@qq.com";

        long result = userService.userRegister(userAccount, userPassword, checkPassword,email);
        Assertions.assertEquals(-1, result);

        userAccount = "cw12102";
        result = userService.userRegister(userAccount, userPassword, checkPassword,email);
        Assertions.assertEquals(-1, result);

        userPassword = "1233456667";
        checkPassword = "12232345545";
        result = userService.userRegister(userAccount, userPassword, checkPassword,email);
        Assertions.assertEquals(-1, result);

        userAccount = "//??dwew";
        checkPassword = userPassword;
        result = userService.userRegister(userAccount, userPassword, checkPassword,email);
        Assertions.assertEquals(-1, result);

        userAccount = "cwt001";
        userPassword = "111111111111";
        result = userService.userRegister(userAccount, userPassword, userPassword,email);
        Assertions.assertTrue(result > 0);


    }

    @Test
    void userLogin() {
    }

    @Test
    void getSafetyUser() {
    }

    @Test
    void userLogout() {
    }

    @Test
    void searchUsersByTags() {
        List<String> tagNameList = Arrays.asList("java","python");
        List<User> userList = userService.searchUsersByTags(tagNameList);
        Assertions.assertNotNull(userList);
    }
}