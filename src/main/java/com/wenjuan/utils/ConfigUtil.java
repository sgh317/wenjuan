package com.wenjuan.utils;


import java.io.*;
import java.util.Properties;

public class ConfigUtil extends Properties {
    /**
     * 自增长序列化ID
     */
    //private static final long serialVersionUID = -5297381375690088745L;
    public static final String path = "properties/config.properties";
    public static final int NUMBER_OF_ARTICLE_ITEM_PER_PAGE = 12;

    public ConfigUtil() {
        InputStream inSm = this.getClass().getResourceAsStream(path);
        try {
            this.load(inSm);
        } catch (Exception ex) {
            System.out.println("配置文件加载异常");

        }
    }

    public static ConfigUtil getDefaultInstance() {
        return new ConfigUtil();
    }

    public static void saveParas(ConfigUtil p) {
        OutputStream outsm = null;
        try {
            outsm = new FileOutputStream(new File(path));
            p.store(outsm, "haaaa");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("文件未找到");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("修改文件异常");
        }
    }
}