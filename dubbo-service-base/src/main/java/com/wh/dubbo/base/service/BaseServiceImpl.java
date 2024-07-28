package com.wh.dubbo.base.service;

import com.wh.dubbo.api.base.BaseService;
import com.wh.dubbo.api.base.NoticeService;
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