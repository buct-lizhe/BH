package com.bh.faultcode.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bh.faultcode.utils.BitNum;
import com.bh.faultcode.utils.HttpClient;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TestPointServiceImpl implements TestPointService {


    HttpClient httpclient = new HttpClient();

    //用于求二进制1对应代码的工具
    BitNum bitNum = new BitNum();

    /**
     * 获取所要的测点信息
     * @param equipmentsJsonArray
     * @param api
     * @return
     */
    @Override
    public JSONArray getTestPoint(JSONArray equipmentsJsonArray, String api ) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        String url = api.substring(0, api.lastIndexOf('/'));
        url = url.substring(0, url.lastIndexOf('/')) + "/point/";
        //System.out.println( url );
        JSONArray pointsJsonArray = new JSONArray();
        for (Object jsonObject : equipmentsJsonArray) {
            String equipmentUuid = (String) ((JSONObject) jsonObject).get("equipmentUuid");
            String url1 = url + equipmentUuid + "/info";
            String info = httpclient.Client(url1, HttpMethod.GET, params);           // 通过http请求从url获取数据
//            System.out.println( info );

            JSONObject jo = JSONObject.parseObject(info);                    // 将得到的字符串格式化为json
            JSONArray PointInfo = (JSONArray) jo.get("data");                // 获得父节点下的data数组,测点信息
            //转换成指定格式的测点json对象数组
            if( PointInfo == null ){
                continue;
            }
            for (int i = 0; i < PointInfo.size(); i++) {
//                System.out.println("数量= "+PointInfo.size() );
                JSONObject jsonObj = new JSONObject();
                //获取测点信息
                jsonObj.put("channelType", (Integer) PointInfo.getJSONObject(i).get("channelType"));
                jsonObj.put("factoryId", (String) PointInfo.getJSONObject(i).get("factoryId"));
                jsonObj.put("factoryName", (String) PointInfo.getJSONObject(i).get("factoryName"));
                jsonObj.put("groupId", (String) PointInfo.getJSONObject(i).get("groupId"));
                jsonObj.put("groupName", (String) PointInfo.getJSONObject(i).get("groupName"));
                jsonObj.put("equipmentId", (String) PointInfo.getJSONObject(i).get("equipmentId"));
                jsonObj.put("equipmentName", (String) PointInfo.getJSONObject(i).get("equipmentName"));
                jsonObj.put("equipmentUuid", (String) PointInfo.getJSONObject(i).get("equipmentUuid"));
                jsonObj.put("pointUuid", (String) PointInfo.getJSONObject(i).get("pointUuid"));
                jsonObj.put("pointId", (String) PointInfo.getJSONObject(i).get("pointId"));
                jsonObj.put("pointName", (String) PointInfo.getJSONObject(i).get("pointName"));
                jsonObj.put("unit", (String) PointInfo.getJSONObject(i).get("unit"));
                jsonObj.put("hh", (Number) PointInfo.getJSONObject(i).get("hh"));
                jsonObj.put("hl", (Number) PointInfo.getJSONObject(i).get("hl"));
                jsonObj.put("lh", (Number) PointInfo.getJSONObject(i).get("lh"));
                jsonObj.put("ll", (Number) PointInfo.getJSONObject(i).get("ll"));
                jsonObj.put("type", (Number) PointInfo.getJSONObject(i).get("type"));
                jsonObj.put("updateTime", (Long) PointInfo.getJSONObject(i).get("updateTime"));
                jsonObj.put("value", (Number) PointInfo.getJSONObject(i).get("value"));
                jsonObj.put("rev", (Number) PointInfo.getJSONObject(i).get("rev"));
                jsonObj.put("revType", (Number) PointInfo.getJSONObject(i).get("revType"));
                jsonObj.put("alarmAlias", (String) PointInfo.getJSONObject(i).get("alarmAlias"));
                jsonObj.put("alarmId", (Integer) PointInfo.getJSONObject(i).get("alarmId"));
                jsonObj.put("alarmLevel", (Integer) PointInfo.getJSONObject(i).get("alarmLevel"));
                jsonObj.put("alarmStatus", (Integer) PointInfo.getJSONObject(i).get("alarmStatus"));
                jsonObj.put("alarmType", (Integer) PointInfo.getJSONObject(i).get("alarmType"));
                jsonObj.put("companyId", (String) PointInfo.getJSONObject(i).get("companyId"));
                jsonObj.put("companyName", (String) PointInfo.getJSONObject(i).get("companyName"));
                jsonObj.put("dataType", (Integer) PointInfo.getJSONObject(i).get("dataType"));
                jsonObj.put("eventType", (Integer) PointInfo.getJSONObject(i).get("eventType"));
                jsonObj.put("nodeType", (Integer) PointInfo.getJSONObject(i).get("nodeType"));
                jsonObj.put("ifAlarm", (Integer) PointInfo.getJSONObject(i).get("ifAlarm"));
                jsonObj.put("locName", (String) PointInfo.getJSONObject(i).get("locName"));
                jsonObj.put("sectionName", (String) PointInfo.getJSONObject(i).get("sectionName"));
                jsonObj.put("unitId", (String) PointInfo.getJSONObject(i).get("unitId"));
                jsonObj.put("unitName", (String) PointInfo.getJSONObject(i).get("unitName"));
                pointsJsonArray.add(jsonObj);
            }
        }
        return pointsJsonArray;
    }

    /**
     * 针对于迅达的获取故障的方法
     * @param pointsJsonArray
     * @return
     */
    @Override
    public JSONArray getXDFault(JSONArray pointsJsonArray ) {

        JSONArray alarmReasonJsonArray = new JSONArray();
        //寄存器地址和测点名称映射
        List<String> storageLoc = new ArrayList<>();
        for (int i = 40001; i < 40017; i++ ) {
            storageLoc.add(i+"");
        }
        //System.out.println(storageLoc);
        //获取故障的测点信息
        for (int i = 0; i < pointsJsonArray.size(); i++) {
            String pointId = (String) pointsJsonArray.getJSONObject(i).get("pointId");
            //测点ID为需要的时进行处理
            if (storageLoc.indexOf( pointId ) != -1 ) {
                int value = ((Number) pointsJsonArray.getJSONObject(i).get("value")).intValue();
//                value = 127;
//                System.out.println( value + "  最开始value值" );
                //二进制位上必有1
                if (value != 0) {
//                    System.out.println( value + "  非0的value  " );
                    if( value < 0 ){
                        value = -value;
                    }
                    Map<String, Object> map = null;
                    try {
                        map = bitNum.getXDCode(value, Integer.parseInt( pointId ) );
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //信息发送所需要的属性
                    String companyId = (String) pointsJsonArray.getJSONObject(i).get("companyId");
                    String equipmentId = (String) pointsJsonArray.getJSONObject(i).get("equipmentId");
                    String factoryId = (String) pointsJsonArray.getJSONObject(i).get("factoryId");
//                     Integer type =  (Integer)pointsJsonArray.getJSONObject(i).get("type");  // 接收的信息：{"code":400,"message":"类型为1或者2","data":null}???
                    Integer type = 2;
                    String unitId = (String) pointsJsonArray.getJSONObject(i).get("unitId");
                    Long updateTime = (Long) pointsJsonArray.getJSONObject(i).get("updateTime");

                    //得到所需要的code值，也就是该测点的所有故障code
                    String[] codes = (String[]) map.get("codes");
                    int length = (int) map.get("length");

                    for (int j = 0; j < length; j++) {
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("code", codes[j]);
                        jsonObj.put("companyId", companyId);
                        jsonObj.put("equipmentId", equipmentId);
                        jsonObj.put("factoryId", factoryId);
                        jsonObj.put("type", type);
                        jsonObj.put("unitId", unitId);
                        jsonObj.put("updateTime", updateTime);
                        //将测点故障处理后加入所需要的json对象数组中
                        alarmReasonJsonArray.add(jsonObj);
                    }
                }
            }
        }
        return alarmReasonJsonArray;
    }

    @Override
    public JSONArray getDSFault(JSONArray pointsJsonArray) {
        JSONArray alarmReasonJsonArray = new JSONArray();
        //获取故障的测点信息
        for (int i = 0; i < pointsJsonArray.size(); i++) {
            String pointId = (String) pointsJsonArray.getJSONObject(i).get("pointId");
            //测点ID为需要的时进行处理
            if ( pointId.equals("40002")) {
                int value = ((Number) pointsJsonArray.getJSONObject(i).get("value")).intValue();
//                value = 599;
//                二进制位上必有1
                if (value != 0) {
                    //System.out.println( value + "  非0的value  " );
                    Map<String, Object> map = null;
                    //信息发送所需要的属性
                    String companyId = (String) pointsJsonArray.getJSONObject(i).get("companyId");
                    String equipmentId = (String) pointsJsonArray.getJSONObject(i).get("equipmentId");
                    String factoryId = (String) pointsJsonArray.getJSONObject(i).get("factoryId");
//                    Integer type =  (Integer)pointsJsonArray.getJSONObject(i).get("type");  // 接收的信息：{"code":400,"message":"类型为1或者2","data":null}???
                    Integer type = 2;
                    String unitId = (String) pointsJsonArray.getJSONObject(i).get("unitId");
                    Long updateTime = (Long) pointsJsonArray.getJSONObject(i).get("updateTime");
                    //此时的code值直接为value,无需操作直接发送
                    int  isNegative = 0;
                    if( value < 0 ){
                        value = -value;
                        isNegative = 1;
                    }
                    String code = value+"";
                    String codeValue = code;
//                    System.out.println( "code=  "+code );
                    if ( code.length() != 3 ) {
                        for (int j = 0; j < 3 - code.length(); j++) {
                            codeValue = "0" + codeValue;
                    }
                    }
                    codeValue = "2"+codeValue;
                    if( isNegative == 1 ){
                        codeValue = "-"+codeValue;
                    }
//                    System.out.println( "codeValue=  "+codeValue );
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("code", codeValue);
                    jsonObj.put("companyId", companyId);
                    jsonObj.put("equipmentId", equipmentId);
                    jsonObj.put("factoryId", factoryId);
                    jsonObj.put("type", type);
                    jsonObj.put("unitId", unitId);
                    jsonObj.put("updateTime", updateTime);
                    //将测点故障处理后加入所需要的json对象数组中
                    alarmReasonJsonArray.add(jsonObj);
                }
            }
        }
        return alarmReasonJsonArray;
    }

    public static void main(String[] args) {

    }

}