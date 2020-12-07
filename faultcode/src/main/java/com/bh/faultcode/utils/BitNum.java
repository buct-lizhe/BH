package com.bh.faultcode.utils;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class BitNum {

    CsvUtils csvUtils = new CsvUtils();
    String codeValue = "";

    public Map<String, Object> getXDCode(int num, int storage) throws FileNotFoundException {
        //获得代码和唯一键值的映射
        HashMap<String, String> hashMapCodes = csvUtils.getXDCodes();
        //二进制某位所对应的代码值
        String key = "0123456789AbCdEF";
        //第一步将整数转化为16位二进制小数
        int n = num;
        //System.out.println(n);
        String s = "";
        while (n != 0) {
            if ((n & 1) == 1) {
                s = "1" + s;
            } else {
                s = "0" + s;
            }
            n = n >> 1;
        }
        //System.out.println(s.length() + "   字符串长度");
        if (s.length() < 16) {
            int len = s.length();
            for (int i = 0; i < 16 - len; i++) {
                s = "0" + s;
            }
        }
        //System.out.println(s);
        String codes[] = new String[100];
        int count = 0;

        //对二进制小数的有1的位进行统计
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '1') {
                //获得三位代码中的第3位
                String s1 = key.charAt(16 - i - 1) + "";
                //获得三位代码中的第2位
                String s2 = key.charAt(storage - 40001) + "";
                String code = "E" + s2 + s1;
                if (hashMapCodes.get(code) != null) {
                    codeValue = hashMapCodes.get(code);
                    if (hashMapCodes.get(code).length() != 3) {
                        for (int j = 0; j < 3 - hashMapCodes.get(code).length(); j++) {
                            codeValue = "0" + codeValue;
                        }
                    }
                    //故障唯一值，为四位字符串，第一位代表厂家，第2，3，4位代表故障值
                    codes[count++] = "1" + codeValue;
                    //System.out.println(code + "  " + hashMapCodes.get(code));
                }
            }
        }
        //System.out.println( "返回值  "+str );
        //System.out.println( str.length );
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("codes", codes);
        map.put("length", count);
        return map;
    }

    public static void main(String[] args) {
        List<String> storageLoc = new ArrayList<>();
        for (int i = 40001; i < 40017; i++ ) {
            storageLoc.add(i+"");
        }
        BitNum bitNum = new BitNum();
        try {

            Map<String, Object> xdCode = bitNum.getXDCode(599, 40007);
            System.out.println( xdCode.get("length") );
            String[] codes  = (String[]) xdCode.get("codes");
            for( int i = 0; i < (int)xdCode.get("length"); i++){
                System.out.println( codes[i] );
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}