package com.zy.qnl.modules.member.service;

import com.baomidou.mybatisplus.service.IService;
import com.zy.qnl.common.utils.PageUtils;
import com.zy.qnl.modules.member.entity.UserEntity;

import java.util.Map;

/**
 * 用户
 *
 */
public interface UserService extends IService<UserEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据用户名，查询系统用户
     */
    UserEntity queryByUserName(String username);

    /**
     * 根据ID，查询系统用户
     */
    UserEntity queryByUserId(Integer userId);
}

