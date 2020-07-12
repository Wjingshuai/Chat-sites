package com.zy.qnl.modules.member.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zy.qnl.modules.member.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户
 *
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {
    /**
     * 根据用户名，查询系统用户
     */
    UserEntity queryByUserName(String username);

    /**
     * 根据ID，查询系统用户
     */
    UserEntity queryByUserId(Integer userId);
}
