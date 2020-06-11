package com.activiti.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串工具类
 * @author
 *
 */
public class StringUtil {
    /**
     * 判断是否是空
     * @param str String
     * @return boolean
     */
    public static boolean isEmpty(String str){
        if(str==null||"".equals(str.trim())){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 判断是否不是空
     * @param str String
     * @return boolean
     */
    public static boolean isNotEmpty(String str){
        if((str!=null)&&!"".equals(str.trim())){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 格式化模糊查询
     * @param str String
     * @return String
     */
    public static String formatLike(String str){
        if(isNotEmpty(str)){
            return "%"+str+"%";
        }else{
            return null;
        }
    }

    /**
     * 过滤掉集合里的空格
     * @param list List<String>
     * @return List<String>
     */
    public static List<String> filterWhite(List<String> list){
        List<String> resultList=new ArrayList<String>();
        for(String l:list){
            if(isNotEmpty(l)){
                resultList.add(l);
            }
        }
        return resultList;
    }

}
