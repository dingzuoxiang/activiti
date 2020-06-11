package com.activiti.util;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

import java.text.SimpleDateFormat;

public class DateJsonValueProcessor implements JsonValueProcessor {
    private String format;

    public DateJsonValueProcessor(String format){
        this.format = format;
    }

    @Override
    public Object processArrayValue(Object o, JsonConfig jsonConfig) {
        return null;
    }

    @Override
    public Object processObjectValue(String s, Object o, JsonConfig jsonConfig) {
        if(o == null)
        {
            return "";
        }
        if(o instanceof java.sql.Timestamp)
        {
            String str = new SimpleDateFormat(format).format((java.sql.Timestamp)o);
            return str;
        }
        if (o instanceof java.util.Date)
        {
            String str = new SimpleDateFormat(format).format((java.util.Date) o);
            return str;
        }

        return o.toString();
    }
}
