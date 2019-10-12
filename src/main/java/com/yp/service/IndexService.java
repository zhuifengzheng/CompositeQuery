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
     */
    void createMapperXML(String outputVOName, String tableName, Map<String, String> mapperDto);


    /**
     * 创建mapper.java
     *
     * @param mapperDto
     */
    void createMapper(Map<String, Map<String, String>> mapperDto);

    /**
     * 创建输入VO实体
     *
     * @param inputVO
     * @param inputVOName
     */
    void createInputVO(Map<String, String> inputVO, String inputVOName);

    /**
     * 创建输入VO实体
     *
     * @param outputVO
     * @param outputVOName
     */
    void createOutputVO(Map<String, String> outputVO, String outputVOName);

    /**
     * 创建repository 包括实现类
     *
     * @param methodName
     * @param inputVOName
     * @param outputVOName
     */
    void createRepository(String methodName, String inputVOName, String outputVOName);


    /**
     * 创建controller层
     *
     * @param methodName
     * @param inputVOName
     * @param outputVOName
     */
    void createController(String methodName, String inputVOName, String outputVOName);

    /**
     * 解析表名 MT_LOV_TABLE => mtLovTable
     * @param tableName :
     * @return java.lang.String
     */
    String analysisTableName(String tableName);
}