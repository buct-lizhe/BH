package com.bh.faultcode.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BrandUtils {


    public List<String> getBrands() throws FileNotFoundException {

        HashMap<String, String> map = new HashMap<String, String>();
        String proFilePath = System.getProperty("user.dir") + "\\brands.csv";
//        System.out.println( proFilePath );
        InputStream in = new FileInputStream(new File(proFilePath));
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in, "GBK"));//这里如果csv文件编码格式是utf-8,改成utf-8即可
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String line = "";
        String everyLine = "";
        List<String> list = new ArrayList<>();
        while (true) {
            try {
                if (!((line = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            everyLine = line;
//            System.out.println(everyLine);
            list.add( everyLine );
        }
        return list;
    }

    public static void main(String[] args) throws FileNotFoundException {
        BrandUtils brandUtils = new BrandUtils();
//        System.out.println( brandUtils.getBrands()[5] );
        List<String> brands = brandUtils.getBrands();
        String manufacture = "迅达1";
        System.out.println( brands.indexOf( manufacture ) );
    }
}