package com.yp.service.impl;

import com.yp.service.IndexService;
import com.yp.utils.CommonUtil;
import com.yp.vo.IndexVO;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * @author fengzheng
 * @create 2019-10-11 21:30
 * @desc
 **/
@Service
public class IndexServiceImpl implements IndexService {


    @Override
    public void createEntrance(IndexVO vo) {

        //解析数据给 mapper.xml  这种Map<String,Map<String,String>>在后面循环时候数据会重复
//        Map<String,Map<String,String>> inputMap = CommonUtil.parseInputVOXML(vo.getInputVO());
//        createMapperXML(inputMap, vo.getOutputVOName(), vo.getTableName());
        Map<String, String> inputMap = CommonUtil.parseInputVOToXML(vo.getInputVO());
        createMapperXML(vo.getOutputVOName(), vo.getTableName(), inputMap, vo.getPackageName());

        //解析数据给 inputVO.java
        Map<String, String> inMap = CommonUtil.parseInputVO(vo.getInputVO());
        createInputVO(inMap, vo.getInputVOName(), vo.getPackageName());

        //解析数据给 outputVO.java
        Map<String, String> outMap = CommonUtil.parseOutInputVO(vo.getOutputVO());
        createOutputVO(outMap, vo.getOutputVOName(), vo.getPackageName());

        //创建repository数据 包括实现类的数据
        createRepository(vo.getMethodName(), vo.getInputVOName(), vo.getOutputVOName(), vo.getPackageName());

        //创建controller中的数据
        createController(vo.getMethodName(), vo.getInputVOName(), vo.getOutputVOName(), vo.getPackageName());
    }

    @Override
    @Deprecated
    public void createMapperXML(Map<String, Map<String, String>> mapperDto, String outputVOName, String tableName) {
        Set<Map.Entry<String, Map<String, String>>> entries = mapperDto.entrySet();
        StringBuilder content = new StringBuilder();
        content.append("<select id=\"selectCondition\" resultType=\"" + outputVOName + "\">").append("\n")
                .append("\t").append("SELECT * FROM " + tableName + " tb").append("\n")
                .append("\t").append("WHERE tb.TENANT_ID = ${tenantId}").append("\n");

        for (Map.Entry<String, Map<String, String>> entry : entries) {

            Map<String, String> value = entry.getValue();
            Set<Map.Entry<String, String>> inMap = value.entrySet();
            for (Map.Entry<String, String> map : inMap) {
                String key = entry.getKey(); //
                if ("String".equalsIgnoreCase(map.getValue())) {
                    content.append("\t").append("<if test=\"dto." + key + " != null\">").append("\n")
                            .append("\t\t").append("AND tb." + map.getKey() + " = #{dto." + key + "}").append("\n")
                            .append("\t").append("</if>").append("\n");
                }
                if ("Date".equalsIgnoreCase(map.getValue())) {
                    content.append("\t").append("<if test=\"dto." + key + " != null\">").append("\n")
                            .append("\t\t").append("AND tb." + map.getKey() + " &gt;= DATE_FORMAT(#{dto." + key + "},'%Y-%m-%d %H:%i:%S')").append("\n")
                            .append("\t").append("</if>").append("\n");
                }

            }

        }
        content.append("</selset>");

        //写入文件
        CommonUtil.writeFile(content.toString(), tableName);

    }

    @Override
    public void createMapperXML(String outputVOName, String tableName, Map<String, String> mapperDto, String packageName) {
        StringBuilder content = new StringBuilder();
        content.append("<select id=\"selectCondition\" resultType=\"" +packageName+".vo."+ outputVOName + "\">").append("\n")
                .append("\t").append("SELECT * FROM " + tableName + " tb").append("\n")
                .append("\t").append("WHERE tb.TENANT_ID = ${tenantId}").append("\n");
        Set<Map.Entry<String, String>> inMap = mapperDto.entrySet();
        for (Map.Entry<String, String> map : inMap) {
            String key = map.getKey();
            String value = map.getValue();
            //这里转义 .
            String[] splitValue = value.split("\\.");
            if ("String".equalsIgnoreCase(splitValue[1])) {
                content.append("\t").append("<if test=\"dto." + key + " != null\">").append("\n")
                        .append("\t\t").append("AND tb." + splitValue[0] + " = #{dto." + key + "}").append("\n")
                        .append("\t").append("</if>").append("\n");
            }
            if ("Date".equalsIgnoreCase(splitValue[1])) {
                String isForm = splitValue[1].substring(splitValue[1].length() - 4, splitValue[1].length());
                String isTo = splitValue[1].substring(splitValue[1].length() - 2, splitValue[1].length());
                if("form".equalsIgnoreCase(isForm)){
                    content.append("\t").append("<if test=\"dto." + key + " != null\">").append("\n")
                            .append("\t\t").append("AND tb." + splitValue[0] + " &gt;= DATE_FORMAT(#{dto." + key + "},'%Y-%m-%d %H:%i:%S')").append("\n")
                            .append("\t").append("</if>").append("\n");
                }
                else if ("to".equalsIgnoreCase(isTo)){
                    content.append("\t").append("<if test=\"dto." + key + " != null\">").append("\n")
                            .append("\t\t").append("AND tb." + splitValue[0] + " &lt;= DATE_FORMAT(#{dto." + key + "},'%Y-%m-%d %H:%i:%S')").append("\n")
                            .append("\t").append("</if>").append("\n");
                }else {
                    content.append("\t").append("<if test=\"dto." + key + " != null\">").append("\n")
                            .append("\t\t").append("AND tb." + splitValue[0] + " = DATE_FORMAT(#{dto." + key + "},'%Y-%m-%d %H:%i:%S')").append("\n")
                            .append("\t").append("</if>").append("\n");
                }

            }


        }
        content.append("</selset>");

        //写入文件
        CommonUtil.writeFile(content.toString(), tableName);
    }

    @Override
    public void createMapper(Map<String, Map<String, String>> mapperDto, String packageName) {

    }

    @Override
    public void createInputVO(Map<String, String> inputVO, String inputVOName, String packageName) {

    }

    @Override
    public void createOutputVO(Map<String, String> outputVO, String outputVOName, String packageName) {

    }

    @Override
    public void createRepository(String methodName, String inputVOName, String outputVOName, String packageName) {

    }

    @Override
    public void createController(String methodName, String inputVOName, String outputVOName, String packageName) {

    }
}