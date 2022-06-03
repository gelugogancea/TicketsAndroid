package com.gonetsoftware.administrator.ticketeurolines;


import android.content.Context;

/**
 * Created by Administrator on 9/16/2015.
 */
public class GlobalDataSet {

    private static Context myContext;
    private static String s_PDFFileName;
    private static String s_profile_db;
    private static String s_DefaultPDFPath;
    private static String s_lng;
    private static String s_printer_name;


    private static GlobalDataSet instance = null;


    public static synchronized GlobalDataSet getInstance() {
        if (instance == null) {
            instance = new GlobalDataSet();
        }
        return instance;
    }

    // Restrict the constructor from being instantiated
    public GlobalDataSet() {
    }


    public static Context getContext() {
        return myContext;
    }

    public static void setContext(Context mContext) {
        myContext = mContext;
    }
    public static void setS_PDFFileName(String s)
    {
        s_PDFFileName=s;
    }
    public static String getS_PDFFileName(){
        return s_PDFFileName;
    }
    public static String getS_DefaultPDFPath()
    {
        return s_DefaultPDFPath;
    }
    public static void sets_DefaultPDFPath(String s){
        s_DefaultPDFPath=s;
    }
    public String getS_profile_db() {
        return s_profile_db;
    }

    public void setS_profile_db(String s) {
        this.s_profile_db = s;
    }

    public String getS_lng() {
        return s_lng;
    }

    public void setS_lng(String s) {
        this.s_lng = s;
    }

    public String getS_printer_name() {
        return s_printer_name;
    }

    public void setS_printer_name(String s) {

        this.s_printer_name = s;
    }
}
