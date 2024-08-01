package com.wh.dubbo.base.service;

import com.wh.dubbo.api.base.BaseService;
import com.wh.dubbo.api.base.NoticeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@DubboService(loadbalance = "roundrobin")
public class BaseServiceImpl implements BaseService {

    public BaseServiceImpl() {
    }

    private NoticeService noticeService;

    @Resource
    public void setNoticeService(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String sayHello(String name) {
        noticeService.sendNotice("Begin sayHello:" + name);
        return "Hello " + name;
    }

}