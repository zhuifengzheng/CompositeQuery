package com.yp.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author peng.yuan
 * @ClassName CommonUtil
 * @description 公共工具类
 */
public class CommonUtil {

    //定义全局接收参数的集合
    public static List<String> inputNotes = new ArrayList();
    public static List<String> outputNotes = new ArrayList();
    /**
     * 解析输入参数给mapper.xml
     *
     * @param inputVO : PERSON_NAME:personName:String,PERSON_NAME:personName:String
     * @return java.util.Map<java.lang.String, java.util.Map < java.lang.String, java.lang.String>>
     */
    public static Map<String, Map<String, String>> parseInputVOXML(String inputVO) {
        String[] split = inputVO.split(",");
        Map<String, String> innerMap = new LinkedHashMap<>();
        Map<String, Map<String, String>> outMap = new LinkedHashMap<>();
        for (String str : split) {
            String[] splitIn = str.split(":");
            //构造数据
            innerMap.put(splitIn[0], splitIn[2]);
            outMap.put(splitIn[1], innerMap);
        }
        return outMap;
    }

    /**
     * 解析输入参数给mapper.xml
     *
     * @param inputVO
     * @return
     */
    public static Map<String, String> parseInputVOToXML(String inputVO) {
        String[] split = inputVO.split(",");
        Map<String, String> outMap = new LinkedHashMap<>();
        for (String str : split) {
            String[] splitIn = str.split(":");
            //构造数据
            outMap.put(splitIn[1], splitIn[0] + "." + splitIn[2]);
            //把注释添加到集合中
            inputNotes.add(splitIn[3]);
        }
        return outMap;
    }

    /**
     * 解析输出参数
     *
     * @param outputVO : personName:String,personAge:Integer
     * @return java.util.Map<java.lang.String, java.lang.String>
     */
    public static Map<String, String> parseOutInputVO(String outputVO) {
        Map<String, String> map = new LinkedHashMap<>();
        String[] split = outputVO.split(",");
        for (String str : split) {
            String[] splitIn = str.split(":");
            map.put(splitIn[0], splitIn[1]);
            //把注释添加到集合中
            outputNotes.add(splitIn[2]);
        }
        return map;
    }

    /**
     * @param inputVO : PERSON_NAME:personName:String,PERSON_NAME:personName:String
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @Author peng.yuan
     * @Date 2019/10/12 18:10
     */
    public static Map<String, String> parseInputVO(String inputVO) {
        Map<String, String> map = new LinkedHashMap<>();
        String[] split = inputVO.split(",");
        for (String str : split) {
            String[] splitIn = str.split(":");
            map.put(splitIn[1], splitIn[2]);
        }
        return map;

    }

    /**
     * 写入文件到本地磁盘
     *
     * @param content
     * @param tableName
     */
    public static void writeFile(String content, String tableName) {
        //这里tableName一般是这样的形式 MT_USER_TABLE => mtUserTable 转换成这样的名称
        //tableName = parseTableName(tableName);
        generateFile(content, tableName);
    }


    /**
     * 生成文件
     *
     * @param content
     * @param tableName
     */
    public static void writeVOFile(String content, String tableName) {
        generateFile(content, tableName);
    }

    private static void generateFile(String content, String tableName) {
        FileOutputStream fileOutputStream = null;
        //获取当前文件路径
        try {
            String rootName = new File("").getCanonicalPath()
                    + File.separator + "src" + File.separator + "common";
            //创建文件
            File file = new File(rootName);
            if (!file.exists()) {
                file.mkdirs();
            }

            File lastFile = new File(rootName + File.separator + tableName);
            //写入文件
            fileOutputStream = new FileOutputStream(lastFile);
            fileOutputStream.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    fileOutputStream = null;
                }
            }
        }
    }

    /**
     * 解析tableName  MT_USER_TABLE => mtUserTable
     *
     * @param tableName
     */
    public static String parseTableName(String tableName) {
        String[] split = tableName.toLowerCase().split("_");
        StringBuilder finallyTableName = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            if (i == 0) {
                finallyTableName.append(split[0]);
            } else {
                finallyTableName.append(split[i].substring(0, 1).toUpperCase()).append(split[i].substring(1, split[i].length()));
            }
        }
        return finallyTableName.toString();
    }

    /**
     * 将表名解析成类名
     *
     * @param tableName
     * @return
     */
    public static String parseTableClassName(String tableName) {
        return tableName.substring(0, 1).toUpperCase() + tableName.substring(1, tableName.length());
    }

    public static void main(String[] args) throws IOException {
        String rootName = new File("").getCanonicalPath()
                + File.separator + "src" + File.separator + "common" + File.separator;
        System.out.println(rootName);
        String str = "123456789";
        String isForm = str.substring(str.length() - 4, str.length());

        System.out.println(isForm);
    }


}
