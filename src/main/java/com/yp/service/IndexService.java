package com.yp.service;

import com.yp.vo.IndexVO;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author fengzheng
 * @create 2019-10-11 21:29
 * @desc 业务逻辑处理
 **/
public interface IndexService {

    /**
     * 执行入口，解析参数封装
     */
    void createEntrance(IndexVO vo);

    /**
     * 创建mapper.xml
     * @param mapperDto
     * @param outputVOName
     * @param tableName
     */
    @Deprecated
    void createMapperXML(Map<String, Map<String, String>> mapperDto, String outputVOName, String tableName);

    /**
     * 创建mapper.xml
     * @param mapperDto
     * @param outputVOName
     * @param tableName
     * @param packageName
     */
    void createMapperXML(String outputVOName, String tableName, Map<String, String> mapperDto, String packageName);


    /**
     * 创建mapper.java
     *
     * @param mapperDto
     * @param packageName
     */
    void createMapper(Map<String, Map<String, String>> mapperDto, String packageName);

    /**
     * 创建输入VO实体
     *
     * @param inputVO
     * @param inputVOName
     * @param packageName
     */
    void createInputVO(Map<String, String> inputVO, String inputVOName, String packageName);

    /**
     * 创建输入VO实体
     *
     * @param outputVO
     * @param outputVOName
     * @param packageName
     */
    void createOutputVO(Map<String, String> outputVO, String outputVOName, String packageName);

    /**
     * 创建repository 包括实现类
     *
     * @param methodName
     * @param inputVOName
     * @param outputVOName
     * @param packageName
     */
    void createRepository(String methodName, String inputVOName, String outputVOName, String packageName);


    /**
     * 创建controller层
     *
     * @param methodName
     * @param inputVOName
     * @param outputVOName
     * @param packageName
     */
    void createController(String methodName, String inputVOName, String outputVOName, String packageName);

}