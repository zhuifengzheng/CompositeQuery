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
        IMPORT_PACK_MAP.put("Date", "import java.util.Date;");
        IMPORT_PACK_MAP.put("date", "import java.util.Date;");
        IMPORT_PACK_MAP.put("List<String>", "import java.util.List;");

    }

    /**
     * 首字母大写
     * @param propertyName
     * @return
     */
    public static String generateFileName(String propertyName) {
        return propertyName.substring(0, 1).toUpperCase()+propertyName.substring(1, propertyName.length());
    }
}