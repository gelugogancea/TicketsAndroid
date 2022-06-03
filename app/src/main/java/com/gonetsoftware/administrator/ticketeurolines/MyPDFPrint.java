package com.gonetsoftware.administrator.ticketeurolines;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;

import com.epson.eposprint.BatteryStatusChangeEventListener;
import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;
import com.epson.eposprint.Print;
import com.epson.eposprint.StatusChangeEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class MyPDFPrint extends Activity implements StatusChangeEventListener, BatteryStatusChangeEventListener {

    private PdfRenderer mPdfRenderer;
    private PdfRenderer.Page mCurrentPage;
    private ParcelFileDescriptor mFileDescriptor;
    private ImageView mImageView;

    static final int SEND_TIMEOUT = 10 * 1000;
    static final int REQUEST_CODE = 12345;
    static final int IMAGE_WIDTH_MAX = 512;
    Bitmap selectImage = null;
    static Print printer = null;
    String openDeviceName = "192.168.192.168";
    int connectionType = Print.DEVTYPE_BLUETOOTH;
    int language = com.epson.eposprint.Builder.LANG_EN;
    String printerName = "TM-P60II";
    static Context MyCtx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pdfprint);
        //MyCtx=this;
        GlobalDataSet.getInstance();
        LoadPDFFileIntoImage(GlobalDataSet.getS_DefaultPDFPath() + "/" + GlobalDataSet.getS_PDFFileName());
        openPrinter();
        printImage();
        closePrinter();
        finish();
    }
    static void closePrinter(){
        try{
            printer.closePrinter();
            printer = null;
        }catch(Exception e){
            printer = null;
        }
    }
    private void LoadPDFFileIntoImage(String sPath) {

        mImageView = (ImageView) findViewById(R.id.ImgPDF);
        File file = new File(sPath);

        ParcelFileDescriptor mFileDescriptor = null;
        try {
            mFileDescriptor = ParcelFileDescriptor.open(file,ParcelFileDescriptor.MODE_READ_ONLY);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        PdfRenderer mPdfRenderer = null;

        try {
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

// Open page with specified index
        PdfRenderer.Page mCurrentPage = mPdfRenderer.openPage(0);



        Bitmap bm = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap bitmap= Bitmap.createScaledBitmap(bm, (int)(mCurrentPage.getWidth()*3.2), (int)(mCurrentPage.getHeight() * 3.2), true);

// Pdf page is rendered on Bitmap
        mCurrentPage.render(bitmap, null, null,
                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
// Set rendered bitmap to ImageView (pdfView in my case)
        mImageView.setImageBitmap(bitmap);

        mCurrentPage.close();
        mPdfRenderer.close();
        try {
            mFileDescriptor.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    private void printImage() {
        if(selectImage == null){
           selectImage=((BitmapDrawable)mImageView.getDrawable()).getBitmap();
        }
        SaveShareData ss=new SaveShareData();
        Builder builder = null;
        String method = "";
        try{
            //create builder
            Intent intent = getIntent();
            method = "Builder";
            //builder = new Builder(intent.getStringExtra("printername"), intent.getIntExtra("language", 0), getApplicationContext());
            builder = new Builder("TM-P60II",0, getApplicationContext());
            //add command
            method = "addImage";
            builder.addImage(selectImage, 0, 0, Math.min(IMAGE_WIDTH_MAX, selectImage.getWidth()), selectImage.getHeight(), Builder.COLOR_1,
                            getBuilderMode(), getBuilderHalftone(), getBuilderBrightness());

            method = "addCut";
            builder.addCut(Builder.CUT_NO_FEED);

            //send builder data
            int[] status = new int[1];
            int[] battery = new int[1];
            try{
                Print printer = getPrinter() ;
                //Print printer=null;
                printer.sendData(builder, SEND_TIMEOUT, status, battery);
                //ShowMsg.showStatus(EposException.SUCCESS, status[0], battery[0], this);
            }catch(EposException e){
                ShowMsg.showStatus(e.getErrorStatus(), e.getPrinterStatus(), e.getBatteryStatus(), this);
            }
        }catch(Exception e){
            ShowMsg.showException(e, method, this);
        }

        //remove builder
        if(builder != null){
            try{
                builder.clearCommandBuffer();
                builder = null;
            }catch(Exception e){
                builder = null;
            }
        }
    }

    public void openPrinter() {
        int deviceType = Print.DEVTYPE_TCP;

        deviceType = Print.DEVTYPE_BLUETOOTH;

        int enabled = Print.FALSE;

        int interval = 1000;
        //open
        Print printer = new Print(getApplicationContext());

        if(printer != null){
            printer.setStatusChangeEventCallback(this);
            printer.setBatteryStatusChangeEventCallback(this);
        }
        SaveShareData s=new SaveShareData();

        try{
            printer.openPrinter(deviceType, s.GiveMeShareData(GlobalDataSet.getContext(),2), enabled, interval);

        }catch(Exception e){
            printer = null;
            ShowMsg.showException(e, "openPrinter" , this);
            return;
        }

        //return settings
        Intent intent = new Intent();

        intent.putExtra("devtype", deviceType);
        intent.putExtra("ipaddress", s.GiveMeShareData(GlobalDataSet.getContext(), 2));
        //intent.putExtra("printername", "TM-P60II");
        intent.putExtra("language", Builder.MODEL_ANK);

        setPrinter(printer);
        setResult(2, intent);
        finish();
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        openDeviceName = savedInstanceState.getString("openDeviceName");
        connectionType = savedInstanceState.getInt("connectionType");
        language = savedInstanceState.getInt("language");
        printerName = savedInstanceState.getString("printerName");
    }
    @Override
    public void onStatusChangeEvent(final String deviceName, final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showStatusChangeEvent(deviceName, status, MyPDFPrint.this);
            }
        });
    }

    @Override
    public void onBatteryStatusChangeEvent(final String deviceName, final int battery) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showBatteryStatusChangeEvent(deviceName, battery, MyPDFPrint.this);
            }
        });
    }
    private int getBuilderMode() {

        return Builder.MODE_MONO;

    }
    private int getBuilderHalftone() {

        return Builder.HALFTONE_DITHER;
    }

    private double getBuilderBrightness() {

        return 1.0;

    }
    static private void setPrinter(Print obj){
        printer = obj;
    }

    static private Print getPrinter(){
        return printer;
    }


}
