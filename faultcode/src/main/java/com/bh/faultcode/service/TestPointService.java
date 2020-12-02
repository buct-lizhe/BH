package com.bh.faultcode.service;

import com.alibaba.fastjson.JSONArray;

public interface TestPointService {
    public JSONArray getTestPoint(JSONArray equipmentsJsonArray, String api );

    JSONArray getXDFault(JSONArray pointsJsonArray );

    JSONArray getDSFault(JSONArray pointJsonArray);
}
