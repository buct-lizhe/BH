package com.bh.faultcode.service;

import com.alibaba.fastjson.JSONArray;

import java.util.Map;

public interface EquipmentService {
    public Map<String, JSONArray> getEquipment(String api);
}