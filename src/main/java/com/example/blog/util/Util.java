package com.example.blog.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.google.gson.Gson;

public class Util {
    
    /**
     * Json String to Bean  Convert
     * 
     * @param src
     * @return
     */
    public static <T> T JsonTobean( String json, Class<T> classOfT )
    {
        if( classOfT == null )
            return null;
        
        return new Gson().fromJson(json, classOfT );
    }
    
    
    public static String nvl(String str, String nullStr)
    {
        if (str == null || str.trim().length() == 0)
            return nullStr;
        else
            return str;
    }
   
}

