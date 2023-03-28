package com.ttxs.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ttxs.usercenter.common.ErrorCode;
import com.ttxs.usercenter.exception.BusinessException;
import com.ttxs.usercenter.service.UserService;
import com.ttxs.usercenter.model.domain.User;
import com.ttxs.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.ttxs.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author Admin
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2023-02-26 19:12:01
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，用于加密
     */
    private static final String SALT = "cwt";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String email) {
        //1. 非空校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        if (userAccount.length() < 5) {
            throw new BusinessException(ErrorCode.LENGTH_ERROR);
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.LENGTH_ERROR);
        }
        //校验密码是否相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PASSWORD_CHECK);
        }
        //校验邮箱
        String regEmail="^[A-Za-z0-9]+([_\\.][A-Za-z0-9]+)*@([A-Za-z0-9\\-]+\\.)+[A-Za-z]{2,6}$";
        Matcher emailMatcher = Pattern.compile(regEmail).matcher(email);
        if (!emailMatcher.matches()){
            throw new BusinessException(ErrorCode.EMAIL_ERROR);
        }
        //账户不能有特殊字符
        String regEx = ".*[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\\\]+.*";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.matches()) {
            throw new BusinessException(ErrorCode.ACCOUNT_ERROR);
        }

        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);

        if (count > 0) {
            throw new BusinessException(ErrorCode.REPEAT_ACCOUNT);
        }

        //2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUsername(userAccount);
        user.setUserPassword(encryptPassword);
        user.setEmail(email);

        //Long
        if (!this.save(user)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1. 非空校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        if (userAccount.length() < 5) {
            throw new BusinessException(ErrorCode.LENGTH_ERROR);
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.LENGTH_ERROR);
        }

        //账户不能有特殊字符
        String regEx = ".*[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\\\]+.*";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.matches()) {
            throw new BusinessException(ErrorCode.ACCOUNT_ERROR);
        }

        //2. 加密

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3. 查询数据库，匹配数据 采用mybatis plus
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在
        if (user == null) {
            log.info("user login failed, userAccount and userPassword cannot match");
            throw new BusinessException(ErrorCode.NO_ACCOUNT);
        }

        //4 用户脱敏
        User safetyUser = getSafetyUser(user);

        //5. 记录用户登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);


        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param originUser 未脱敏前的对象
     * @return 脱敏后的对象
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setTags(originUser.getTags());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());

        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUpdateTime(originUser.getUpdateTime());
        // safetyUser.setIsDelete(originUser.getIsDelete()); // 当 isDelete = 0 时，查不到（已配置）
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 根据标签查询用户（使用内存）
     * @param tagNameList 标签名称列表
     * @return 返回符合的用户列表
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //1 先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();

        //2 在内存中判断是否包含符合要求的标签
        // stream --> parallelStream 可实现并行流 但使用同一个线程池，存在诸多隐患
        return userList.stream().filter(user -> {
            String tagNames = user.getTags();
            if (StringUtils.isAnyBlank(tagNames)){
                return false;
            }
            //O(1)
            Set<String> tempTagNameSet =  gson.fromJson(tagNames,new TypeToken<Set<String>>(){}.getType());
            //判空
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)){
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());

    }

    /**
     * 根据标签查询用户（使用SQL）
     * @param tagNameList 标签名称列表
     * @return 返回含义标签的用户列表
     */

    @Deprecated
    private List<User> searchUsersByTagsBySQL(List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //拼接 like查询
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags",tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }


}




