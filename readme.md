#组合查询API生成模板工具

##导入分析
> + 需要传入类型为查询参数的数据格式： PERSON_NAME:personName:String,PERSON_AGE:personAge:Integer
> + 需要返回参数数据格式：personName:String,personAge:Integer
> + 入参的实体名称：xxxVO  返回参数的实体名称： xxxVO2
> + 传入的方法名称：xxx
> + 传入表名：xxxtable


## mapper.xml的生成
>+ 解析后为 Map<String,Map<String,String>> => Map<personName,Map<PERSON_NAME,String>>
```
select * 
from 
    xxxtable tb
where 
    tb.TEALENTID = #{tealentId}
    if(dto.personName != null){
        and tb.PERSON_NAME = #{dto.personName}
    }

```

## 输入参数实体生成、输出参数实体生成
```
输入参数解析后为 Map<String,String> => Map<personName,String>> 
输出参数解析后为 Map<String,String> => Map<personName,String>>
```

## mapper.java层方法生成
```
List<xxxVO2> selectCondition(@Param(value="tealentId") Long tealentId,@Param(value="dto") xxxVo dto)
```

## repository层方法生成、repositoryImpl方法生成
```
List<xxxVO2> xxx(Long tealentId, xxxVo dto);

List<xxxVO2> xxx(Long tealentId, xxxVo dto){
    List<xxxVO2> VO2List = mapper.selectCondition(tealentId, dto);
}
```
## controller层方法生成
```
@AoiOperation(value="xxx")
@PostMapping(value="/")
@Permission()
public ResponseData<List<xxxVO2>> xxx(@PathVariable("organizationId") Long tealentId, @Responsebody xxxVO dto){
    ResponseData<List<xxxVO2>> reponseData = new ResponseData<List<xxxVO2>>();
    try{
        reponseData.setRow(repository.xxx(tealentId, dto));
    }catch(Exception e){
        reponseData.setSuccess(false);
        reponseData.setMessage(e.getMessage());
    }
    
}

```