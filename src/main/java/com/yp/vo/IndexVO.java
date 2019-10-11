package com.yp.vo;

import lombok.Data;

/**
 * @author fengzheng
 * @create 2019-10-11 22:39
 * @desc
 **/
@Data
public class IndexVO {
    /**
     *  inputVO 输入参数
     *  outputVO 返回参数
     *  methodName 方法名称
     *  tableName 表名
     *  inputVOName 输入参数包装实体名称
     *  outputVOName 输出参数包装实体名称
     */
    private String inputVO;
    private String  outputVO;
    private String  methodName;
    private String  tableName;
    private String  inputVOName;
    private String  outputVOName;
}