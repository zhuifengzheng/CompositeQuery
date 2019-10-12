package com.yp.utils;

import com.yp.CompositeQueryApplicationTests;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CommonUtilTest extends CompositeQueryApplicationTests {

    private String inputVO = "PERSON_NAME:personName:List<String>,PERSON_AGE:personAge:Integer";
    @Test
    public void parseInputVO() {
        String[] split = inputVO.split(",");
        Map<String, String> innerMap = new HashMap<>();
        Map<String, Map<String, String>> outMap = new HashMap<>();
        for (String str : split){
            String[] splitIn = str.split(":");
            //构造数据
            innerMap.put(splitIn[0],splitIn[2]);
            outMap.put(splitIn[1], innerMap);
        }
        System.out.println(outMap.get("personName"));
    }

    @Test
    public void parseTableName() {
        String tableName = "MT_USER_TABLE";
        String[] split = tableName.toLowerCase().split("_");
        StringBuilder finallyTableName = new StringBuilder();
        for(int i=0; i<split.length; i++){
            if(i == 0){
                finallyTableName.append(split[0]);
            }else{
                finallyTableName.append(split[i].substring(0,1).toUpperCase()).append(split[i].substring(1,split[i].length()));
            }

        }
        try {
            System.out.println(new File("").getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(finallyTableName.toString());
    }
}