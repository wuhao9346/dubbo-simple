package com.wh.dubbbo.demo.base.service;

import com.wh.dubbbo.demo.api.base.BaseService;
import com.wh.dubbbo.demo.api.base.NoticeService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService(loadbalance = "roundrobin")
public class BaseServiceImpl implements BaseService {
    @Resource
    private NoticeService noticeService;

    @Override
    public String sayHello(String name) {
        noticeService.sendNotice("Begin sayHello:" + name);
        return "Hello " + name;
    }

}