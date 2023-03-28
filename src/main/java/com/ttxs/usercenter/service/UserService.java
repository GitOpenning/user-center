package com.ttxs.usercenter.service;

import com.ttxs.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Admin
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2023-02-26 19:12:01
 */
public interface UserService extends IService<User> {
    /**
     * 用户登录
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @return 返回用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword,String email);

    /**
     * 用户登录
     * @param userAccount 账户
     * @param userPassword 密码
     * @return 返回用户对象（用户信息）
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser 未脱敏对象
     * @return 脱敏后的对象
     */
    User getSafetyUser(User originUser);

    /**
     *用户注销
     * @param request 获取session
     * @return 1
     */
    int userLogout(HttpServletRequest request);

    /**
     * 全局根据标签搜索用户
     *
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);
}
