package com.zy.qnl.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zy.qnl.modules.sys.entity.SysUserTokenEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户Token
 *
 */
@Mapper
public interface SysUserTokenDao extends BaseMapper<SysUserTokenEntity> {

    SysUserTokenEntity queryByToken(String token);
	
}
