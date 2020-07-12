package com.zy.qnl.modules.member;

import com.zy.qnl.common.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

/**
 * 登录相关
 *
 */
@RestController
public class MemberController {

    @GetMapping("/pc/getMe")
    public Map<String, Object> getMe()throws IOException {
        return R.ok();
    }
}
