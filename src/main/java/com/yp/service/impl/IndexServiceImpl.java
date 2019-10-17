package com.yp.service.impl;

import com.yp.service.IndexService;
import com.yp.utils.CommonUtil;
import com.yp.utils.InitUtil;
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
//        Map<String,Map<String,String>> inputMap = CommonUtil.parseInputVOXML(inputVO);
//        createMapperXML(inputMap, vo.getOutputVOName(), vo.getTableName());
        //过滤空格和换行
        String inputVO = vo.getInputVO().replaceAll("[\\s*\\t\\n\\r]", "");
        String outputVO = vo.getOutputVO().replaceAll("[\\s*\\t\\n\\r]", "");
        Map<String, String> inputMap = CommonUtil.parseInputVOToXML(inputVO);
        createMapperXML(vo.getOutputVOName(), vo.getTableName(), inputMap, vo.getPackageName());

        //创建mapper.java
        createMapper(vo.getTableName(), vo.getMethodName(), vo.getInputVOName(), vo.getOutputVOName(), vo.getPackageName());

        //解析数据给 inputVO.java
        Map<String, String> inMap = CommonUtil.parseInputVO(inputVO);
        createInputVO(inMap, vo.getInputVOName(), vo.getPackageName());

        //解析数据给 outputVO.java
        Map<String, String> outMap = CommonUtil.parseOutInputVO(outputVO);
        createOutputVO(outMap, vo.getOutputVOName(), vo.getPackageName());

        //创建repository数据 包括实现类的数据
        createRepository(vo.getTableName(), vo.getMethodName(), vo.getInputVOName(), vo.getOutputVOName(), vo.getPackageName());

        //创建controller中的数据
        createController(vo.getTableName(), vo.getMethodName(), vo.getInputVOName(), vo.getOutputVOName(), vo.getPackageName());
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
        content.append("<select id=\"selectCondition\" resultType=\"" + packageName + ".domain.vo." + outputVOName + "\">").append("\n")
                .append("\t").append("SELECT * FROM " + tableName + " tb").append("\n")
                .append("\t").append("WHERE tb.TENANT_ID = ${tenantId}").append("\n");
        Set<Map.Entry<String, String>> inMap = mapperDto.entrySet();
        for (Map.Entry<String, String> map : inMap) {
            String key = map.getKey();
            String value = map.getValue();
            //这里转义 .
            String[] splitValue = value.split("\\.");
            if ("Date".equalsIgnoreCase(splitValue[1])) {
                String isForm = splitValue[1].substring(splitValue[1].length() - 4, splitValue[1].length());
                String isTo = splitValue[1].substring(splitValue[1].length() - 2, splitValue[1].length());
                if ("form".equalsIgnoreCase(isForm)) {
                    content.append("\t").append("<if test=\"dto." + key + " != null\">").append("\n")
                            .append("\t\t").append("AND tb." + splitValue[0] + " &gt;= DATE_FORMAT(#{dto." + key + "},'%Y-%m-%d %H:%i:%S')").append("\n")
                            .append("\t").append("</if>").append("\n");
                } else if ("to".equalsIgnoreCase(isTo)) {
                    content.append("\t").append("<if test=\"dto." + key + " != null\">").append("\n")
                            .append("\t\t").append("AND tb." + splitValue[0] + " &lt;= DATE_FORMAT(#{dto." + key + "},'%Y-%m-%d %H:%i:%S')").append("\n")
                            .append("\t").append("</if>").append("\n");
                } else {
                    content.append("\t").append("<if test=\"dto." + key + " != null\">").append("\n")
                            .append("\t\t").append("AND tb." + splitValue[0] + " = DATE_FORMAT(#{dto." + key + "},'%Y-%m-%d %H:%i:%S')").append("\n")
                            .append("\t").append("</if>").append("\n");
                }

            } else {
                content.append("\t").append("<if test=\"dto." + key + " != null\">").append("\n")
                        .append("\t\t").append("AND tb." + splitValue[0] + " = #{dto." + key + "}").append("\n")
                        .append("\t").append("</if>").append("\n");
            }
        }
        content.append("</select>");

        tableName = CommonUtil.parseTableName(tableName);
        String className = CommonUtil.parseTableClassName(tableName);

        //写入文件
        CommonUtil.writeFile(content.toString(), className + "Mapper.xml");
    }

    @Override
    public void createMapper(String tableName, String methodName, String inputVOName, String outputVOName, String packageName) {
        tableName = CommonUtil.parseTableName(tableName);
        String className = CommonUtil.parseTableClassName(tableName);
        //Repository
        StringBuilder content = new StringBuilder();
        content.append("package " + packageName + ".infra.mapper;").append("\n\n");
        content.append("import org.springframework.data.repository.query.Param;").append("\n");
        content.append("import java.util.List;").append("\n\n");
        // 创建类
        content.append("public class " + className + "Mapper {").append("\n\n");

        content.append("\t").append("List<" + outputVOName + "> selectCondition(@Param(value = \"tenantId\") Long tenantId, @Param(value = \"dto\")")
                .append(inputVOName + " dto);").append("\n");
        content.append("}");
        CommonUtil.writeFile(content.toString(), className + "Mapper.java");
    }

    @Override
    public void createInputVO(Map<String, String> inputVO, String inputVOName, String packageName) {
        generateSetAndGet(inputVO, inputVOName, packageName);
    }


    @Override
    public void createOutputVO(Map<String, String> outputVO, String outputVOName, String packageName) {
        generateSetAndGet(outputVO, outputVOName, packageName);
    }

    @Override
    public void createRepository(String tableName, String methodName, String inputVOName, String outputVOName, String packageName) {
        tableName = CommonUtil.parseTableName(tableName);
        String className = CommonUtil.parseTableClassName(tableName);
        //Repository
        StringBuilder content = new StringBuilder();
        content.append("package " + packageName + ".domain.repository;").append("\n\n");
        content.append("import java.util.List;").append("\n\n");
        // 创建类
        content.append("public class " + className + "Repository {").append("\n\n");

        content.append("\t").append("List<" + outputVOName + "> ")
                .append(methodName).append(" (Long tenantId, ").append(inputVOName + " dto);").append("\n");
        content.append("}");
        CommonUtil.writeFile(content.toString(), className + "Repository.java");

        StringBuilder contentImpl = new StringBuilder();
        contentImpl.append("package " + packageName + ".infra.repository.impl;").append("\n\n");
        contentImpl.append("import java.util.List;").append("\n\n");
        // 创建类
        contentImpl.append("public class " + className + "RepositoryImpl implements " + tableName + "Repository {").append("\n\n");

        contentImpl.append("\t").append("@Override").append("\n");

        contentImpl.append("\t").append("public List<" + outputVOName + "> ")
                .append(methodName).append(" (Long tenantId, ").append(inputVOName + "dto){").append("\n");
        contentImpl.append("\t\t").append("List<" + outputVOName + "> voList = " + tableName + "Mapper.selectCondition(tenantId, dto);").append("\n")
                .append("\t\t").append("if (CollectionUtils.isEmpty(shiftVO9List)) {").append("\n")
                .append("\t\t\t").append("return null;").append("\n")
                .append("\t\t").append("}").append("\n")
                .append("\t\t").append("// TODO others api query operation").append("\n")
                .append("\t").append("}").append("\n")
                .append("}");
        CommonUtil.writeFile(contentImpl.toString(), className + "RepositoryImpl.java");

    }

    @Override
    public void createController(String tableName, String methodName, String inputVOName, String outputVOName, String packageName) {
        tableName = CommonUtil.parseTableName(tableName);
        String className = CommonUtil.parseTableClassName(tableName);

        //Repository
        StringBuilder content = new StringBuilder();
        content.append("package " + packageName + ".api.controller.v1;").append("\n\n");
        content.append("import io.choerodon.core.iam.ResourceLevel;").append("\n")
                .append("import io.choerodon.swagger.annotation.Permission;").append("\n")
                .append("import io.swagger.annotations.ApiOperation;").append("\n")
                .append("import org.springframework.beans.factory.annotation.Autowired;").append("\n")
                .append("import java.util.List;").append("\n\n");
        // 创建类
        content.append("public class " + className + "Controller {").append("\n\n");
        content.append("\t").append("@Autowired").append("\n")
                .append("\t").append("private " + tableName + "Repository" + " repository;").append("\n\n");

        content.append("\t").append("@ApiOperation(value = \"" + methodName + "\")").append("\n")
                .append("\t").append("@PostMapping(value = {\"/query\"}, produces = \"application/json;charset=UTF-8\")").append("\n")
                .append("\t").append("@Permission(level = ResourceLevel.ORGANIZATION)").append("\n")
                .append("\t").append("public ResponseData<List<" + outputVOName + ">> " + methodName + "(@PathVariable(\"organizationId\") Long tenantId, @RequestBody " + inputVOName + " dto) {").append("\n")
                .append("\t\t").append("ResponseData<List<" + outputVOName + ">> result = new ResponseData<List<" + outputVOName + ">>();").append("\n")
                .append("\t\t").append("try {").append("\n")
                .append("\t\t\t").append("result.setRows(repository." + methodName + "(tenantId, dto));").append("\n")
                .append("\t\t").append("} catch (Exception e) {").append("\n")
                .append("\t\t\t").append("result.setSuccess(false);").append("\n")
                .append("\t\t\t").append("result.setMessage(e.getMessage());").append("\n")
                .append("\t\t").append("}").append("\n")
                .append("\t\t").append("return result;").append("\n")
                .append("\t").append("}").append("\n");

        content.append("}");
        CommonUtil.writeFile(content.toString(), className + "Controller.java");

    }

    /**
     * 生成get set方法
     *
     * @param inputVO
     * @param inputVOName
     * @param packageName
     */
    private void generateSetAndGet(Map<String, String> inputVO, String inputVOName, String packageName) {
        Set<Map.Entry<String, String>> entries = inputVO.entrySet();
        StringBuilder content = new StringBuilder();
        //导入包名
        content.append("package " + packageName + ".domain.vo;").append("\n\n");
        content.append("import java.io.Serializable;").append("\n");
        content.append("import io.swagger.annotations.ApiModelProperty;").append("\n");
        for (Map.Entry<String, String> map : entries) {
            //导入包
            String importPackage = InitUtil.IMPORT_PACK_MAP.get(map.getValue());
            if (null != importPackage) {
                content.append(importPackage).append("\n");
            }
        }
        // 创建类
        content.append("public class " + inputVOName + " implements Serializable {").append("\n\n");

        //创建属性
        for (Map.Entry<String, String> map : entries) {
            content.append("\t").append("@ApiModelProperty(\"\")").append("\n");
            content.append("\t").append("private " + map.getValue() + " " + map.getKey() + ";").append("\n");
        }
        content.append("\n");
        //创建set get方法
        for (Map.Entry<String, String> map : entries) {
            //如果是时间类型要注意set get方法有点特殊
            //将属性名首字母转换成大写
            String propertyName = InitUtil.generateFileName(map.getKey());
            if ("Date".equalsIgnoreCase(map.getValue())) {
                //get
                content.append("\t").append("public Date get" + propertyName + "() {").append("\n")
                        .append("\t\t").append("if (" + map.getKey() + " != null) {").append("\n")
                        .append("\t\t\t").append("return (Date) " + map.getKey() + ".clone();").append("\n")
                        .append("\t\t").append("} else  {").append("\n")
                        .append("\t\t\t").append("return null;").append("\n")
                        .append("\t\t")
                        .append("\t").append("}").append("\n\n");

                //set
                content.append("\t").append("public void set" + propertyName + "(Date " + map.getKey() + ") {").append("\n")
                        .append("\t\t").append("if (" + map.getKey() + " == null) {").append("\n")
                        .append("\t\t\t").append("this." + map.getKey() + " = null;").append("\n")
                        .append("\t\t").append("} else {").append("\n")
                        .append("\t\t\t").append("this." + map.getKey() + " = (Date) " + map.getKey() + ".clone();").append("\n")
                        .append("\t\t").append("}").append("\n")
                        .append("\t").append("}").append("\n\n");
            } else {
                //get
                content.append("\t").append("public " + map.getValue() + " get" + propertyName + "() {").append("\n")
                        .append("\t\t").append("return " + map.getKey() + ";").append("\n")
                        .append("\t").append("}").append("\n\n");

                //set
                content.append("\t").append("public void set" + propertyName + "(" + map.getValue() + " " + map.getKey() + ") {").append("\n")
                        .append("\t\t").append("this." + map.getKey() + " =" + map.getKey() + ";").append("\n")
                        .append("\t").append("}").append("\n\n");
            }

        }
        content.append("}");

        CommonUtil.writeVOFile(content.toString(), inputVOName + ".java");
    }

}