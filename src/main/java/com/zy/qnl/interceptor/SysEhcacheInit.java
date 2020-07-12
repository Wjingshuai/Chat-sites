package com.zy.qnl.interceptor;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class SysEhcacheInit implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("SysEhcacheInit执行时启动了~~");
    }
}
