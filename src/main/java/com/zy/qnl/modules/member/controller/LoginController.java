package com.zy.qnl.modules.member.controller;


import com.zy.qnl.common.annotation.Log;
import com.zy.qnl.common.utils.R;
import com.zy.qnl.modules.member.entity.UserEntity;
import com.zy.qnl.modules.member.service.UserService;
import com.zy.qnl.modules.member.vo.LoginForm;
import com.zy.qnl.modules.sys.service.SysUserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

/**
 * 会员的登录与退出
 */
@RestController
public class LoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private SysUserTokenService sysUserTokenService;
    /**
     * 登录
     */
    @Log(value = "切注解日志测试")
    @PostMapping("/pc/login")
    public Map<String, Object> login(@RequestBody LoginForm form)throws IOException {
        //用户信息
        UserEntity user = userService.queryByUserName(form.getUsername());
        //账号不存在、密码错误
//        if(user == null || !user.getPassword().equals(new Sha256Hash(form.getPassword()))) {
//            return R.error("账号或密码不正确");
//        }

        //生成token，并保存到数据库
        R r = sysUserTokenService.createToken(user.getUserId());
        return r;
    }
}
