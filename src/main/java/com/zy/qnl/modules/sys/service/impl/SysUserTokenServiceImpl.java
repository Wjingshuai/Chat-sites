package com.zy.qnl.modules.sys.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zy.qnl.common.utils.R;
import com.zy.qnl.common.utils.RedisUtils;
import com.zy.qnl.modules.sys.dao.SysUserTokenDao;
import com.zy.qnl.modules.sys.entity.SysUserTokenEntity;
import com.zy.qnl.modules.sys.oauth2.TokenGenerator;
import com.zy.qnl.modules.sys.service.SysUserTokenService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service("sysUserTokenService")
public class SysUserTokenServiceImpl extends ServiceImpl<SysUserTokenDao, SysUserTokenEntity> implements SysUserTokenService {
	//12小时后过期
	private final static int EXPIRE = 3600 * 12;
	@Autowired
	private RedisUtils redisUtils;
	private final static Gson gson = new Gson();
	@Override
	public R createToken(long userId) {
		//生成一个token
		String token = TokenGenerator.generateValue();

		//当前时间
		Date now = new Date();
		//过期时间
		Date expireTime = new Date(now.getTime() + EXPIRE * 1000);

		//判断是否生成过token
		//SysUserTokenEntity tokenEntity = this.selectById(userId);
		SysUserTokenEntity tokenEntity = gson.fromJson(redisUtils.get(String.valueOf(userId)),SysUserTokenEntity.class);
		if(tokenEntity == null){
			tokenEntity = new SysUserTokenEntity();
			tokenEntity.setUserId(userId);
			tokenEntity.setToken(token);
			tokenEntity.setUpdateTime(now);
			tokenEntity.setExpireTime(expireTime);

			//保存token
			//this.insert(tokenEntity);
			redisUtils.set(String.valueOf(userId),tokenEntity,EXPIRE * 1000);
		}else{
			tokenEntity.setToken(token);
			tokenEntity.setUpdateTime(now);
			tokenEntity.setExpireTime(expireTime);

			//更新token
			//this.updateById(tokenEntity);
			redisUtils.set(String.valueOf(userId),tokenEntity,EXPIRE * 1000);
		}

		R r = R.ok().put("token", token).put("expire", EXPIRE);

		return r;
	}

	@Override
	public void logout(long userId) {
		//删除token
		redisUtils.delete(String.valueOf(userId));
	}
}
