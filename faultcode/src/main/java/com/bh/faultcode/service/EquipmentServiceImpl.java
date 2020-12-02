package com.bh.faultcode.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bh.faultcode.controller.MainController;
import com.bh.faultcode.entity.Brand;
import com.bh.faultcode.utils.BrandUtils;
import com.bh.faultcode.utils.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

    public int counts = 0;
    String nodeId[] = new String[100];
    HttpClient httpclient = new HttpClient();
    BrandUtils brandUtils = new BrandUtils();
    Map<String,Integer> equipmentIsVisit = new HashMap<String, Integer>();
    List<String> brands;
    {
        try {
            brands = brandUtils.getBrands();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static final Logger logger = LoggerFactory.getLogger(MainController.class);
    @Override
    public Map<String, JSONArray> getEquipment(String api) {
        String url = api;
        //System.out.println( url );
        String url1 = url.substring(0, url.lastIndexOf('/'));
        url1 = url1.substring(0, url1.lastIndexOf('/'));
        url1 = url1 + "/equipment/node/";
        //System.out.println( url1 );
        HttpMethod method = HttpMethod.GET;
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        String info = httpclient.Client(url, method, params);           // 通过http请求从url获取数据
        //System.out.println(info);
        JSONObject jo = JSONObject.parseObject(info);                   // 将得到的字符串格式化为json
        JSONArray midInfo = (JSONArray) jo.get("data");                 // 获得父节点下的data数组,中间件信息
        JSONArray equipmentsXDJsonArray = new JSONArray();                //转换成指定格式的迅达json对象数组
        JSONArray equipmentsDSJsonArray = new JSONArray();                //转换成指定格式的蒂森json对象数组
        JSONArray equipmentsTLJsonArray = new JSONArray();                //转换成指定格式的通力json对象数组
        JSONArray equipmentsADSJDJsonArray = new JSONArray();                //转换成指定格式的奥的斯机电json对象数组
        JSONArray equipmentsXZADSJsonArray = new JSONArray();                //转换成指定格式的西子奥的斯json对象数组
        JSONArray equipmentsFSDJsonArray = new JSONArray();                //转换成指定格式的富士达son对象数组
        JSONArray equipmentsBLTJsonArray = new JSONArray();                //转换成指定格式的博林特json对象数组
        if ( midInfo == null ){
            logger.error(" 中间件信息为空 ");
        }else{
            for (int i = 0; i < midInfo.size(); i++) {                      //对midInfo数组进行迭代,获得全部的中间件nodeId
                nodeId[i] = (String) midInfo.getJSONObject(i).get("nodeId");
                url = url1 + nodeId[i] + "/info"; // 拼接获取设备信息的url
                params = new LinkedMultiValueMap<String, String>();
                info = httpclient.Client(url, method, params);              // 通过http请求从url获取数据,获得该中间件上的全部设备信息
                //System.out.println( info );
                jo = JSONObject.parseObject(info);                          // 将得到的字符串格式化为json
                JSONArray equipmentInfo = (JSONArray) jo.get("data");       // 获得父节点下的data数组,设备信
                if( equipmentInfo == null )
                    continue;
                if( equipmentInfo == null ){
                    logger.error(" 设备信息为空 ");
                }
                //System.out.println( "数量=" +counts+equipmentInfo.size());
                for (int j = 0; j < equipmentInfo.size(); j++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("equipmentUuid", (String) equipmentInfo.getJSONObject(j).get("equipmentUuid"));
                    String manufacturer  = (String) equipmentInfo.getJSONObject(j).get("manufacturer");
                    String key = (String) equipmentInfo.getJSONObject(j).get("equipmentUuid") + manufacturer;
                    if ( brands.indexOf(manufacturer) == -1  && equipmentIsVisit.get( key ) == null ) {
                        logger.info("{}不存在，判断录入是否有错",manufacturer );
                        equipmentIsVisit.put( key , 1 );
                    }
                    else {
                        if( manufacturer.equals("迅达") ){
                            equipmentsXDJsonArray.add( jsonObject );
                            continue;
                        }
                        if( manufacturer.equals("蒂森") ){
                            equipmentsDSJsonArray.add( jsonObject );
                            continue;
                        }
                        if( manufacturer.equals("通力") ){
                            equipmentsTLJsonArray.add( jsonObject );
                            continue;
                        }
                        if( manufacturer.equals("奥的斯机电") ){
                            equipmentsADSJDJsonArray.add( jsonObject );
                            continue;
                        }
                        if( manufacturer.equals("西子奥的斯") ){
                            equipmentsXZADSJsonArray.add( jsonObject );
                            continue;
                        }
                        if( manufacturer.equals("南京富士达") ){
                            equipmentsFSDJsonArray.add( jsonObject );
                            continue;
                        }
                        if( manufacturer.equals("博林特") ){
                            equipmentsBLTJsonArray.add( jsonObject );
                            continue;
                        }
                    }
//                System.out.println( equipmentsJsonArray.getJSONObject(j) );
                }
            }
        }
        HashMap<String, JSONArray> maps = new HashMap<>();
        maps.put("equipmentsXDJsonArray", equipmentsXDJsonArray);
        maps.put("equipmentsDSJsonArray", equipmentsDSJsonArray);
        maps.put("equipmentsTLJsonArray", equipmentsTLJsonArray);
        maps.put("equipmentsADSJDJsonArray", equipmentsADSJDJsonArray);
        maps.put("equipmentsXZADSJsonArray", equipmentsXZADSJsonArray);
        maps.put("equipmentsFSDJsonArray", equipmentsFSDJsonArray);
        maps.put("equipmentsBLTJsonArray", equipmentsBLTJsonArray);
        return maps;    //返回一个quipments数组，包含所有的设备信息
    }

    public static void main(String[] args) {
    }
}
