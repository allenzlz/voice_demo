package com.xiaoi.exp.voice.util;

import java.util.UUID;

public class UUIDUtils {
    /**
     * 生成32位UUID随机数
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
