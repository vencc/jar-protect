package com.free.bsf.jarprotect.core.util;

/**
 * @author: chejiangyi
 * @version: 2019-07-24 11:00
 **/
public class StringUtils {
    public static String nullToEmpty(Object str) {
        return str != null ? str.toString() : "";
    }
    public static boolean isEmpty(String str){
        if(str ==null || str.isEmpty())
        {return true;}
        return false;
    }

    public static String trimLeft(String str,char trim){
        int beginIdx;
        for(beginIdx = 0; beginIdx < str.length() && trim == str.charAt(beginIdx); ++beginIdx) {
        }
        return str.substring(beginIdx);
    }
    public static String trimRight(String str,char trim){
        int endIdx;
        for(endIdx = str.length() - 1; endIdx >= 0 && trim == str.charAt(endIdx); --endIdx) {
        }
        return str.substring(0, endIdx + 1);
    }
    public static String trim(String str,char trim){
        return trimRight(trimLeft(str,trim),trim);
    }

    public static String[] spilt(String str,String spiltChar){
        if(isEmpty(str))
            return new String[]{};
        return str.split(spiltChar);
    }

    public static String insertArray(String[] arrayStr, String insertStr, String position) {
        StringBuffer newStr = new StringBuffer();
        boolean isInsert = false;
        for (int i = 0; i < arrayStr.length; i++) {
            newStr.append(arrayStr[i]).append("\r\n");
            if (arrayStr[i].startsWith(position)) {
                newStr.append(insertStr).append("\r\n");
                isInsert = true;
            }
        }
        if (!isInsert) {
            newStr.append(insertStr).append("\r\n");
        }
        return newStr.toString();
    }

    /**
     * ,分割,*做模糊匹配的 条件匹配算法
     * */
    public static boolean hitCondition(String condition,String data){
        if(StringUtils.isEmpty(condition)||data == null)
            return false;

        String[] skipUrls = condition.split(",");
        String trimChar="*";
        for(String skip : skipUrls) {
            //匹配所有
            if("*".equals(skip)){
                return true;
            }
            String trimUrl=StringUtils.trim(skip,'*');
            if(!skip.startsWith(trimChar)&&!skip.endsWith(trimChar)){
                if(data.equals(trimUrl))
                    return true;
            }
            else if(skip.startsWith(trimChar)&&skip.endsWith(trimChar)){
                if(data.contains(trimUrl))
                    return true;
            }else if(skip.startsWith(trimChar)){
                if(data.endsWith(trimUrl))
                    return true;
            }else if(skip.endsWith(trimChar)){
                if(data.startsWith(trimUrl))
                    return true;
            }
        }
        return false;
    }

    public static String print(Object obj){
        if(obj==null){
            return "null";
        }
        return obj.toString();
    }

    public static String strip(String str, String remove) {
        return stripEnd(stripBegin(str,remove),remove);
    }

    public static String stripBegin(String str, String remove) {
        int strlen = remove.length();
        while (true) {
            boolean strBegin = str.substring(0, strlen).equals(remove);
            str = strBegin ? str.substring(strlen) : str;
            if (!strBegin) {
                break;
            }
        }
        return str;
    }

    public static String stripEnd(String str, String remove) {
        int len, strlen = remove.length();
        while (true) {
            len = str.length();
            boolean strEnd = str.substring(len - strlen).equals(remove);
            str = strEnd ? str.substring(0, len - strlen) : str;
            if (!strEnd) {
                break;
            }
        }
        return str;
    }
}
