package com.wh.dubbo.base.service;

import com.wh.dubbo.api.base.NoticeService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(loadbalance = "shortestresponse")
public class NoticeServiceImpl implements NoticeService {

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
