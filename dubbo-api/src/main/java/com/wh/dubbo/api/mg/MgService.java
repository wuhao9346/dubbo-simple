package com.wh.dubbo.api.mg;

import java.util.List;
import java.util.Map;

/**
 * 药库服务
 */
public interface MgService {
    /**
     * 获取药品列表
     *
     * @return 药品列表
     */
    List<Map<String, Object>> getDrugList();
}
