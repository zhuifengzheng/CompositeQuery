package com.yp.utils;

import com.yp.CompositeQueryApplicationTests;
import org.junit.Test;

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
}