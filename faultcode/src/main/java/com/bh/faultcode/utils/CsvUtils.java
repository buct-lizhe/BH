package com.bh.faultcode.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CsvUtils {


    public HashMap<String, String> getXDCodes() throws FileNotFoundException {

        HashMap<String, String> map = new HashMap<String, String>();
        String proFilePath = System.getProperty("user.dir") + "\\codes\\xunda.csv";
        //System.out.println( proFilePath );
        InputStream in = new FileInputStream(new File(proFilePath));
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in, "utf-8"));//这里如果csv文件编码格式是utf-8,改成utf-8即可
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String line = "";
        String everyLine = "";
        List<String> allString = new ArrayList<>();
        while (true) {
            try {
                if (!((line = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            everyLine = line;
//            System.out.println(everyLine);
            String[] s = everyLine.split(",");
//            System.out.println( s[0] +" "+s[1]+" "+s[2] );
            map.put(s[1], s[0]);
            allString.add(everyLine);
        }
        return map;
    }

    public static void main(String[] args) throws FileNotFoundException {
        CsvUtils csvUtils = new CsvUtils();
        System.out.println( csvUtils.getXDCodes() );
    }
}