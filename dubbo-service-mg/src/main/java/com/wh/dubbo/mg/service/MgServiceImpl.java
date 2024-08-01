package com.wh.dubbo.mg.service;

import com.wh.dubbo.api.base.BaseService;
import com.wh.dubbo.api.base.NoticeService;
import com.wh.dubbo.api.mg.MgService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService(loadbalance = "leastactive")
public class MgServiceImpl implements MgService {

    @DubboReference
    private BaseService baseService;
    @DubboReference
    private NoticeService noticeService;

    //    @GlobalTransactional
    @Override
    public List<Map<String, Object>> getDrugList() {
        noticeService.sendNotice("开始获取药品列表。");
        List<Map<String, Object>> drugList = new ArrayList<>();

        Map<String, Object> drug = new HashMap<>();
        drug.put("id", "1");
        drug.put("name", "药品1");
        drugList.add(drug);

        drug = new HashMap<>();
        drug.put("id", "2");
        drug.put("name", "药品2");
        drugList.add(drug);

        noticeService.sendNotice("药品列表组装完成");
        return drugList;

    }
}
