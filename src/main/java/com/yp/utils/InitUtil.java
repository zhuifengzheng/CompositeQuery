package com.yp.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fengzheng
 * @create 2019-10-12 23:05
 * @desc 初始化工具类
 **/
public class InitUtil {
    // 类型对应的导入包
    public final static Map<String, String> IMPORT_PACK_MAP = new HashMap<>();
    static {

        IMPORT_PACK_MAP.put("DATE", "import java.util.Date;");

    }
}