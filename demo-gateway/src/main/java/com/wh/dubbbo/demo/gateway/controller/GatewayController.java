package com.wh.dubbbo.demo.gateway.controller;

import com.wh.dubbbo.demo.api.base.BaseService;
import com.wh.dubbbo.demo.api.mg.MgService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class GatewayController {

    @DubboReference
    private MgService mgService;
    @DubboReference
    private BaseService baseService;

    @GetMapping(value = "/test")
    public List<Map<String, Object>> testDubbo(@RequestParam("name") String name) {
        System.out.println(name);
        baseService.sayHello(name);
        return mgService.getDrugList();
    }
}
