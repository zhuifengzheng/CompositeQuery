package com.yp.service.impl;

import com.yp.service.IndexService;
import com.yp.utils.CommonUtil;

import java.util.Map;

/**
 * @author fengzheng
 * @create 2019-10-11 21:30
 * @desc
 **/
public class IndexServiceImpl implements IndexService {


    @Override
    public void createEntrance(String inputVO, String outputVO, String methodName, String tableName, String inputVOName, String outputVOName) {

        //解析数据给 mapper.xml
        Map<String,Map<String,String>> inputMap = CommonUtil.parseInputVOXML(inputVO);
        createMapperXML(inputMap);

        //解析数据给 inputVO.java
        Map<String,String> inMap =CommonUtil.parseInputVO(inputVO);
        createInputVO(inMap, inputVOName);

        //解析数据给 outputVO.java
        Map<String,String> outMap = CommonUtil.parseOutInputVO(outputVO);
        createOutputVO( outMap, outputVOName);

        //创建repository数据 包括实现类的数据
        createRepository(methodName, inputVOName, outputVOName);

        //创建controller中的数据
        createController(methodName, inputVOName, outputVOName);
    }

    @Override
    public void createMapperXML(Map<String, Map<String, String>> mapperDto) {

    }

    @Override
    public void createMapper(Map<String, Map<String, String>> mapperDto) {

    }

    @Override
    public void createInputVO(Map<String, String> inputVO, String inputVOName) {

    }

    @Override
    public void createOutputVO(Map<String, String> outputVO, String outputVOName) {

    }

    @Override
    public void createRepository(String methodName, String inputVOName, String outputVOName) {

    }

    @Override
    public void createController(String methodName, String inputVOName, String outputVOName) {

    }

    @Override
    public String analysisTableName(String tableName) {
        return null;
    }
}