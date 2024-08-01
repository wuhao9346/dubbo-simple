package com.wh.dubbo.base.service;

import com.wh.dubbo.api.base.BaseService;
import com.wh.dubbo.api.base.NoticeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@DubboService(loadbalance = "shortestresponse")
public class NoticeServiceImpl implements NoticeService {

    public NoticeServiceImpl() {
    }

    private BaseService baseService;

    @Resource
    @Lazy
    public void setBaseService(BaseService baseService) {
        this.baseService = baseService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean sendNotice(String notice) {
        try {
            System.out.println("发送通知:" + notice);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
