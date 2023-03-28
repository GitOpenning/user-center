package com.ttxs.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ttxs.usercenter.common.BaseResponse;
import com.ttxs.usercenter.common.ErrorCode;
import com.ttxs.usercenter.common.ResultUtils;
import com.ttxs.usercenter.exception.BusinessException;
import com.ttxs.usercenter.model.domain.User;
import com.ttxs.usercenter.model.domain.request.UserLoginRequest;
import com.ttxs.usercenter.model.domain.request.UserRegisterRequest;
import com.ttxs.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ttxs.usercenter.constant.UserConstant.*;

/**
 * 用户接口
 * RestController 相应的返回类型均为：json
 * RequestMapper 请求路径
 */

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;


//    //用户注册
//    @PostMapping("/register")
//    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
//        if (userRegisterRequest == null) {
//            throw new BusinessException(ErrorCode.NULL_ERROR);
//        }
//        String userAccount = userRegisterRequest.getUserAccount();
//        String userPassword = userRegisterRequest.getUserPassword();
//        String checkPassword = userRegisterRequest.getCheckPassword();
//        String email = userRegisterRequest.getEmail();
//        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,email)) {
//            throw new BusinessException(ErrorCode.NULL_ERROR);
//        }
//        long result =  userService.userRegister(userAccount, userPassword, checkPassword,email);
//        return ResultUtils.success(result);
//    }

    //用户注册
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestParam Map<String, String> params) {
        if (params == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = params.get("userAccount");
        String userPassword = params.get("userPassword");
        String checkPassword = params.get("checkPassword");
        String email = params.get("email");
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,email)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long result =  userService.userRegister(userAccount, userPassword, checkPassword,email);
        return ResultUtils.success(result);
    }

//    //用户登录
//    @PostMapping("/login")
//    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
//        if (userLoginRequest == null) {
//            throw new BusinessException(ErrorCode.NULL_ERROR);
//        }
//        String userAccount = userLoginRequest.getUserAccount();
//        String userPassword = userLoginRequest.getUserPassword();
//        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
//            throw new BusinessException(ErrorCode.NULL_ERROR);
//        }
//        User result= userService.userLogin(userAccount, userPassword, request);
//        return ResultUtils.success(result);
//    }

//用户登录
@PostMapping("/login")
public BaseResponse<User> userLogin(@RequestParam Map<String, String> params, HttpServletRequest request) {
    if (params == null) {
        throw new BusinessException(ErrorCode.NULL_ERROR);
    }
    String userAccount =params.get("userAccount");
    String userPassword =params.get("userPassword");
    if (StringUtils.isAnyBlank(userAccount, userPassword)) {
        throw new BusinessException(ErrorCode.NULL_ERROR);
    }
    User result= userService.userLogin(userAccount, userPassword, request);
    return ResultUtils.success(result);
}

    //用户注销
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Integer result =  userService.userLogout(request);
        return ResultUtils.success(result);
    }

    //当前用户信息
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userState = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userState;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser =  userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    //查询用户 鉴权
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        // 鉴权  仅管理员可查询
        if (notAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        //防止返回密码
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    //删除用户 鉴权
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        //鉴权 仅管理员可以删除
        if (notAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result = userService.removeById(id);  //逻辑删除
        return ResultUtils.success(result);
    }

    //鉴权  是管理员则返回true
    private boolean notAdmin(HttpServletRequest request) {
        Object userState = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userState;
        return user == null || user.getUserRole() == DEFAULT_ROLE;
    }

}
