package com.gonetsoftware.administrator.ticketeurolines;


import android.content.Context;
import android.content.SharedPreferences;

public class SaveShareData {


    public int SaveMyShareData(Context ctx,  String myPath, String myPrinterName) {
        int ret = 0;
        try {
            SharedPreferences gobizSettings = ctx.getSharedPreferences("TicketEurolines", ctx.MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = gobizSettings.edit();
            prefEditor.putString("MY_DEFAULT_PATH", myPath);
            prefEditor.putString("MY_PRINTER", myPrinterName);

            prefEditor.commit();
        } catch (Exception Ex) {
            String mSg = Ex.getMessage().toString();
            ret = -1;
        }
        return ret;
    }

    public String GiveMeShareData(Context ctx, int MyCase) {
        String ret = "";
        try {
            SharedPreferences gobizSettings = ctx.getSharedPreferences("TicketEurolines", ctx.MODE_PRIVATE);

            switch (MyCase) {
                case 1:/*Host*/
                    ret = gobizSettings.getString("MY_DEFAULT_PATH", "");
                    break;
                case 2:
                    ret = gobizSettings.getString("MY_PRINTER", "");
                    break;
            }
        } catch (Exception Ex) {
            String msg = Ex.getMessage().toString();
            ret = "";
        }
        return ret;
    }

    public int Copy2Global(Context ctx) {

        int ret = 0;
        int i = 0;
        String res;
        GlobalDataSet globalvar = GlobalDataSet.getInstance();
        for (i = 0; i < 2; i++) {
            res = GiveMeShareData(ctx, i + 1);
            if (res == "") {
                ret += 0;
            } else
            {
                if (i == 0) {
                    globalvar.sets_DefaultPDFPath(res);
                } else if (i == 1) {
                    globalvar.setS_printer_name(res);
                } else {
                    ret = -1;
                }
                ret += 1;
            }
        }
        return ret;
    }

}

