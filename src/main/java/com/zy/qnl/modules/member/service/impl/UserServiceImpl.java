package com.zy.qnl.modules.member.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zy.qnl.common.annotation.CacheKey;
import com.zy.qnl.common.annotation.RedisCache;
import com.zy.qnl.common.utils.PageUtils;
import com.zy.qnl.common.utils.Query;
import com.zy.qnl.modules.member.dao.UserDao;
import com.zy.qnl.modules.member.entity.UserEntity;
import com.zy.qnl.modules.member.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String username = (String) params.get("username");
        Long createUserId = (Long) params.get("createUserId");
        Page<UserEntity> page = this.selectPage(new Query<UserEntity>(params).getPage(),
                new EntityWrapper<UserEntity>().like(StringUtils.isNotBlank(username), "username", username)
                        .eq(createUserId != null, "create_user_id", createUserId));

        return new PageUtils(page);
    }

    @Override
    @RedisCache(cacheName = "测试key")
    public UserEntity queryByUserName(@CacheKey String username) {
        return baseMapper.queryByUserName(username);
    }

    @Override
    public UserEntity queryByUserId(Integer userId) {
        return baseMapper.queryByUserId(userId);
    }

}