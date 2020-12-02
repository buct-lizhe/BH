package com.bh.faultcode.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.attribute.standard.NumberUp;


@Slf4j
public class MainController {

    public static final Logger logger = LoggerFactory.getLogger(MainController.class);
    public static void main(String[] args) {
//        String proFilePath = System.getProperty("user.dir") + "\\application.properties";
//        System.out.println(proFilePath);
//        try {
//             int a = 1 / 0;
//        }catch ( Exception e){
//            logger.error("除数不能为0 {}",e.getMessage());
//        }
        int value = ((Number)(-(-599.98))).intValue();
        System.out.println( "1111".equals("22") == false );

    }
}
