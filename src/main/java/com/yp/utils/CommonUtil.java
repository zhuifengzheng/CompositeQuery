package com.yp.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author peng.yuan
 * @ClassName CommonUtil
 * @description 公共工具类
 */
public class CommonUtil {
    /**
     * 解析输入参数给mapper.xml
     * @param inputVO : PERSON_NAME:personName:String,PERSON_NAME:personName:String
     * @return java.util.Map<java.lang.String, java.util.Map < java.lang.String, java.lang.String>>
     */
    public static Map<String, Map<String, String>> parseInputVOXML(String inputVO) {
        String[] split = inputVO.split(",");
        Map<String, String> innerMap = new HashMap<>();
        Map<String, Map<String, String>> outMap = new HashMap<>();
        for (String str : split) {
            String[] splitIn = str.split(":");
            //构造数据
            innerMap.put(splitIn[0], splitIn[2]);
            outMap.put(splitIn[1], innerMap);
        }
        return outMap;
    }

    /**
     *  解析输出参数
     * @param outputVO : personName:String,personAge:Integer
     * @return java.util.Map<java.lang.String, java.lang.String>
     */
    public static Map<String, String> parseOutInputVO(String outputVO) {
        Map<String, String> map = new HashMap<>();
        String[] split = outputVO.split(",");
        for (String str : split) {
            String[] splitIn = str.split(":");
            map.put(splitIn[0], splitIn[1]);
        }
        return map;
    }

    /**
     *
     * @Author peng.yuan
     * @Date 2019/10/12 18:10
     * @param inputVO : PERSON_NAME:personName:String,PERSON_NAME:personName:String
     * @return java.util.Map<java.lang.String,java.lang.String>
     */
    public static Map<String, String> parseInputVO(String inputVO) {
        Map<String, String> map = new HashMap<>();
        String[] split = inputVO.split(",");
        for (String str : split) {
            String[] splitIn = str.split(":");
            map.put(splitIn[1], splitIn[2]);
        }
        return map;

    }
}
