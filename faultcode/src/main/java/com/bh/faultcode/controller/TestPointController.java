package com.bh.faultcode.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bh.faultcode.service.EquipmentService;
import com.bh.faultcode.service.EquipmentServiceImpl;
import com.bh.faultcode.service.TestPointService;
import com.bh.faultcode.service.TestPointServiceImpl;
import com.bh.faultcode.utils.HttpClient;
import com.bh.faultcode.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@Slf4j
public class TestPointController {
    @Autowired
    TestPointService testPointService = new TestPointServiceImpl();
    @Autowired
    EquipmentService equipmentService = new EquipmentServiceImpl();

    public static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private JSONArray equipmentsXDJsonArray = null;
    private JSONArray equipmentsDSJsonArray = null;
    private JSONArray pointJsonArray = null;
    private JSONArray alarmPointJsonArray = new JSONArray();
    //访问的api
    public String api = "";
    //记录测点上一次传的code值
    Map<String,String> preCode = new HashMap<>();


    HttpClient httpClient = new HttpClient();

    @RequestMapping("/123")
    private String getInfo() {

        Map<String, JSONArray> maps = equipmentService.getEquipment(api);
        //迅达的获得故障值的方法，以测点信息为参数传入
        //获得迅达设备信息
        equipmentsXDJsonArray = maps.get("equipmentsXDJsonArray");                 //获得的迅达设备信息json数组
//        System.out.println( "设备迅达数量  "+equipmentsXDJsonArray.size() );
        if( equipmentsXDJsonArray != null ){
            //获得测点信息
            pointJsonArray = testPointService.getTestPoint(equipmentsXDJsonArray, api);
            if( testPointService.getXDFault(pointJsonArray) != null){
                for (Object jsonObject : testPointService.getXDFault(pointJsonArray)) {
                    alarmPointJsonArray.add( (JSONObject)jsonObject );
                }
            }

        }

        //蒂森的获得故障值的方法，以测点信息为参数传入
        //获得蒂森设备信息
        equipmentsDSJsonArray = maps.get("equipmentsDSJsonArray");                 //获得的蒂森设备信息json数组
//        System.out.println( "设备蒂森数量  "+equipmentsDSJsonArray.size() );
        if( equipmentsDSJsonArray != null ){
            //获得测点信息
            pointJsonArray = testPointService.getTestPoint(equipmentsDSJsonArray, api);
            if( testPointService.getXDFault(pointJsonArray) != null ) {
                for (Object jsonObject : testPointService.getDSFault(pointJsonArray)) {
                    alarmPointJsonArray.add( (JSONObject)jsonObject );
                }
            }
        }

        return pointJsonArray.toString();
    }

    private void sendInfo() {
        for (Object jsonObject : alarmPointJsonArray) {
//            System.out.println( jsonObject );
            JSONObject jo = (JSONObject) jsonObject;
            Map<String, String> params = new HashMap<String, String>();
            params.put("code", jo.get("code").toString());
            params.put("companyId", jo.get("companyId").toString());
            params.put("factoryId", jo.get("factoryId").toString());
            params.put("unitId", jo.get("unitId").toString());
            params.put("equipmentId", jo.get("equipmentId").toString());
            params.put("type", jo.get("type").toString());
            params.put("updateTime", jo.get("updateTime").toString());
            String equipmentId = jo.get("equipmentId").toString();
            //采集间隔时间大于12小时则正常发送
            //  43200000 / 1000 / 60 / 60 = 12
            //作为设备和code唯一标识
            String equipment_code = jo.get("equipmentId").toString()+ jo.get("code").toString();
            String nowCode = jo.get("code").toString();
//            System.out.println( equipment_code );
            if( preCode.get( equipment_code )!= null ){
                String precode1 = preCode.get( equipment_code );
                if( nowCode.equals( precode1) == false ){
                    //如果本次的code值和上一次发的不相同
                    //更新code记忆
                    //需要发送
                    preCode.put( equipment_code , nowCode );
                    System.out.println("发送的信息：" + params);
                    String url1 = api.substring(0, api.lastIndexOf('/'));
                    url1 = url1.substring(0, url1.lastIndexOf('/'));
                    url1 = url1 + "/point/pushDTUInfo";
                    String result = HttpUtils.sendPost(url1, params);
                    try {
                        result = new String(result.getBytes(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    System.out.println("接收的信息：" +result.substring( 8,11 ));
                    if( result.substring( 8,11 ) == "200" ){
                        logger.info("发送：{}",jsonObject );
                        logger.info("接收：{}",result );
                    }

                }
                //如果相同不做操作
            }else{
                //第一次发送，无记录时
                //更新code记忆
                preCode.put(equipment_code , nowCode );
                System.out.println("发送的信息：" + params);
                String url1 = api.substring(0, api.lastIndexOf('/'));
                url1 = url1.substring(0, url1.lastIndexOf('/'));
                url1 = url1 + "/point/pushDTUInfo";
                String result = HttpUtils.sendPost(url1, params);
                try {
                    result = new String(result.getBytes(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println("接收的信息：" +result.substring( 8,11 ));
                if( result.substring( 8,11 ).equals( "200" ) ){
                    logger.info("发送：{}",jsonObject );
                    logger.info("接收：{}",result );
                }
            }
        }
    }


    public static void main(String[] args) {
        logger.info(" 软件启动时间");
        TestPointController testPointController = new TestPointController();
        File file = new File(System.getProperty("user.dir") + "\\StartTime.txt");
        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter( file );
            fileWriter.append("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String proFilePath = null;
        proFilePath = System.getProperty("user.dir") + "\\application.properties";
        System.out.println(proFilePath);
        Properties properties = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream(new File(proFilePath));
            //Properties加载数据流对象
            try {
                properties.load(inputStream);
            } catch ( IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            logger.error("找不到配置文件！！！{}", e );
        }

        String IP = properties.getProperty("IP");
        String PORT = properties.getProperty("PORT");
        if( PORT.equals("") )
            testPointController.api = "http://"+IP+"/node/info";
        else
            testPointController.api = "http://"+IP+":"+PORT+"/node/info";
        String url = testPointController.api;
        System.out.println( url );
        while ( true ){
            System.out.println("访问中。。。。。");
            testPointController.getInfo();
            testPointController.sendInfo();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
