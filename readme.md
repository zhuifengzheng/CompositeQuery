# 组合查询API生成模板工具

## 导入分析
> + 需要传入类型为查询参数的数据格式： PERSON_NAME:personName:String,PERSON_AGE:personAge:Integer  这样的格式主要是为了和文档保持一致便于复制，但是这样的问题是会导致解析这样的格式时候比较慢，但考虑到项目自己使用，就不再去改输入格式了，输入格式对代码结构也有影响，好的输入格式可以使用更好的数据格式去存储。
> + 需要返回参数数据格式：personName:String,personAge:Integer
> + 入参的实体名称：xxxVO  返回参数的实体名称： xxxVO2
> + 传入的方法名称：xxx
> + 传入表名：xxxtable


## mapper.xml的生成
>+ 解析后为 Map<String,String> => Map<Map<personName,PERSON_NAME:String>>
```    
     <select id="selectCondition" resultType="tarzan.actual.domain.vo.MtWkcShiftVO9">
            SELECT *
            FROM mt_wkc_shift ws
            WHERE ws.TENANT_ID = ${tenantId}
            <if test="condition.wkcShiftId != null">
                AND ws.WKC_SHIFT_ID = #{condition.wkcShiftId}
            </if>
            <if test="condition.workcellId != null">
                AND ws.workcell_id = #{condition.workcellId}
            </if>
            <if test="condition.shiftDateFrom != null">
                AND ws.SHIFT_DATE &gt;= DATE_FORMAT(#{condition.shiftDateFrom},'%Y-%m-%d %H:%i:%S')
            </if>
     </selset>

```

## 输入参数实体生成、输出参数实体生成
```
输入参数解析后为 Map<String,String> => Map<personName,String>> 
输出参数解析后为 Map<String,String> => Map<personName,String>>
```

## mapper.java层方法生成
```
List<MtWkcShiftVO9> selectCondition(@Param(value = "tenantId") Long tenantId,
                    @Param(value = "condition") MtWkcShiftVO8 condition);
```

## repository层方法生成、repositoryImpl方法生成
```
List<MtWkcShiftVO9> propertyLimitWkcShiftPropertyQuery(Long tenantId, MtWkcShiftVO8 dto);

@Override
    public List<MtWkcShiftVO9> propertyLimitWkcShiftPropertyQuery(Long tenantId, MtWkcShiftVO8 dto) {
        List<MtWkcShiftVO9> shiftVO9List = mtWkcShiftMapper.selectCondition(tenantId, dto);
        if (CollectionUtils.isEmpty(shiftVO9List)) {
            return null;
        }
     }
```
## controller层方法生成
```
@ApiOperation(value = "propertyLimitWkcShiftPropertyQuery")
    @PostMapping(value = {"/shift/property/query"}, produces = "application/json;charset=UTF-8")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseData<List<MtWkcShiftVO9>> propertyLimitWkcShiftPropertyQuery(
                    @PathVariable("organizationId") Long tenantId, @RequestBody MtWkcShiftVO8 dto) {
        ResponseData<List<MtWkcShiftVO9>> result = new ResponseData<List<MtWkcShiftVO9>>();
        try {
            result.setRows(repository.propertyLimitWkcShiftPropertyQuery(tenantId, dto));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
 }

```
