package com.gonetsoftware.administrator.ticketeurolines;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.epson.eposprint.BatteryStatusChangeEventListener;
import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;
import com.epson.eposprint.Print;
import com.epson.eposprint.StatusChangeEventListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.IOException;

//public class MainActivity extends AppCompatActivity {
//public class MainActivity extends ActionBarActivity implements  StatusChangeEventListener, BatteryStatusChangeEventListener{
public class MainActivity extends AppCompatActivity implements  StatusChangeEventListener, BatteryStatusChangeEventListener{
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL=1;
    int MY_PERMISSIONS_REQUEST_BT=2;
    TextView txtDefaultPath ;
    TextView txtDefaultPrinter;
    Button BtnSetDefPath;
    Button BtnSetPrinter;

    File mPath = null;
    FileDialog fileDialog = null;
    SaveShareData s;

    static int OS_VERSION=0;
    static Print printer = null;
    String openDeviceName = "192.168.192.168";
    int connectionType = Print.DEVTYPE_BLUETOOTH;
    int language = com.epson.eposprint.Builder.LANG_EN;
    String printerName = "TM-P60II";

    static void setPrinter(Print obj){
        printer = obj;
    }

    static Print getPrinter(){
        return printer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        OS_VERSION=android.os.Build.VERSION.SDK_INT;

        try {
            if(OS_VERSION==21) {
                mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
            }else
            {
                mPath=new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            }
        }
        catch (Exception e){

            String s=e.toString();
            MyShowMessage.show(this,"EROARE\r\n" + s);
        }

        txtDefaultPath = (TextView)findViewById(R.id.txtDefaultPath);
        txtDefaultPrinter=(TextView)findViewById(R.id.textView);
        BtnSetDefPath=(Button)findViewById(R.id.button);
        BtnSetPrinter=(Button)findViewById(R.id.button2);
        s = new SaveShareData();
        GlobalDataSet.setContext(this);
        int permissionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH );
        permissionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_ADMIN );
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_PRIVILEGED)<0 )
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_PRIVILEGED},MY_PERMISSIONS_REQUEST_BT);
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE )<0)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_PRIVILEGED},MY_PERMISSIONS_REQUEST_READ_EXTERNAL);

        }
        fileDialog = new FileDialog(this, mPath);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                //Intent enableBtIntent = new Intent(
                //        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivity(enableBtIntent);
                mBluetoothAdapter.enable();
                MyShowMessage.show(GlobalDataSet.getContext(),"Am pornit portul Blue Tooth");
            }
        }


        String sPath2PDF=s.GiveMeShareData(GlobalDataSet.getContext(), 1);
        String sBTPrinter=s.GiveMeShareData(GlobalDataSet.getContext(), 2);
        int iRet=s.Copy2Global(GlobalDataSet.getContext());
        if ( iRet > 0) {
            if (iRet<2) {
                MyShowMessage.show(GlobalDataSet.getContext(),"Nu este setata imprimanta de lucru BT Epson");
            }
            else
            {
                Intent MyList=new Intent(this,MyPDF.class);
                startActivity(MyList);
            }

        }
        else {
            MyShowMessage.show(GlobalDataSet.getContext(),"Calea catre fisierele PDF si imprimanta de lucru, nu sunt setate");
        }
        if(sPath2PDF!="")
        {
            GlobalDataSet.getInstance();
            GlobalDataSet.sets_DefaultPDFPath(sPath2PDF);
            txtDefaultPath.setText(sPath2PDF);
        }
        if(sBTPrinter!="") {
            txtDefaultPrinter.setText(sBTPrinter);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Nu este implementat", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        BtnSetPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent m = new Intent(GlobalDataSet.getContext(), DiscoverPrinterActivity.class);
                if (m!=null)
                {
                    m.putExtra("devtype", connectionType);
                     m.putExtra("ipaddress", openDeviceName);
                    m.putExtra("printername", printerName);
                    m.putExtra("language", language);
                    startActivityForResult(m, 0);
                }

            }
        });
        BtnSetDefPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileDialog.setFileEndsWith(".zzzx");
                fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                    public void fileSelected(File file) {
                        Log.d(getClass().getName(), "selected file " + file.toString());
                    }
                });
                fileDialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
                    public void directorySelected(File directory) {
                        Log.d(getClass().getName(), "selected dir " + directory.toString());
                    }
                });
                fileDialog.setSelectDirectoryOption(false);
                fileDialog.showDialog();




            }

        });
    }

    //@Override
    //public void onDismiss(DialogInterface dialogInterface) {
    //    String a=s.GiveMeShareData(GlobalDataSet.getContext(),1);
    //}
    //@Override
    //public void onClick(View v) {
    //    //runPrintSequence();
    //    //openPrinter();
    //    //printImage();
    //    closePrinter();
    //}
    @Override
    public void onBackPressed() {
        String ssss="aaaa";
    }
    private void print(Builder builder, Result result) {
        int printerStatus[] = new int[1];
        int batteryStatus[] = new int[1];
        boolean isBeginTransaction = false;

        // sendData API timeout setting (10000 msec)
        final int sendTimeout = 10000;
        Print printer = null;

        // Null check
        if ((builder == null) || (result == null)) {
            return;
        }

        // init result
        result.setPrinterStatus(0);
        result.setBatteryStatus(0);
        result.setEposException(null);
        result.setEpsonIoException(null);

        printer = new Print(getApplicationContext());

        try {
            // Open
            printer.openPrinter(
                    1,
                    "TP60-II",
                    Print.FALSE,
                    Print.PARAM_DEFAULT,
                    Print.PARAM_DEFAULT);
        }
        catch (EposException e) {
            result.setEposException(e);
            return;
        }

        try {
            // Print data if printer is printable
            printer.getStatus(printerStatus, batteryStatus);
            result.setPrinterStatus(printerStatus[0]);
            result.setBatteryStatus(batteryStatus[0]);

            if (isPrintable(result)) {
                printerStatus[0] = 0;
                batteryStatus[0] = 0;

                printer.beginTransaction();
                isBeginTransaction = true;

                printer.sendData(builder, sendTimeout, printerStatus, batteryStatus);
                result.setPrinterStatus(printerStatus[0]);
                result.setBatteryStatus(batteryStatus[0]);
            }
        }
        catch (EposException e) {
            result.setEposException(e);
        }
        finally {
            if (isBeginTransaction) {
                try {
                    printer.endTransaction();
                }
                catch (EposException e) {
                    // Do nothing
                }
            }
        }

        try {
            printer.closePrinter();
        }
        catch (EposException e) {
            // Do nothing
        }

        return;
    }
    private boolean isPrintable(Result result) {
        if (result == null) {
            return false;
        }

        int status = result.getPrinterStatus();
        if ((status & Print.ST_OFF_LINE) == Print.ST_OFF_LINE) {
            return false;
        }

        if ((status & Print.ST_NO_RESPONSE) == Print.ST_NO_RESPONSE) {
            return false;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //MyShowMessage.show(this,"aaaaaaaa");
            Intent MyList=new Intent(this,MyPDF.class);
            startActivity(MyList);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            String ss=s.GiveMeShareData(GlobalDataSet.getContext(),1);
            if(ss!="")
            {
                txtDefaultPath.setText(s.GiveMeShareData(GlobalDataSet.getContext(),1));
            }
            else {
                MyShowMessage.show(this,"Nu este setat directorul implicit pt PDF");
            }

        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("openDeviceName", openDeviceName);
        outState.putInt("connectionType", connectionType);
        outState.putInt("language", language);
        outState.putString("printerName", printerName);
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
                ShowMsg.showStatusChangeEvent(deviceName, status, MainActivity.this);
            }
        });
    }

    @Override
    public void onBatteryStatusChangeEvent(final String deviceName, final int battery) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showBatteryStatusChangeEvent(deviceName, battery, MainActivity.this);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null){
            if(resultCode == 1 || resultCode == 2){
                connectionType = data.getIntExtra("devtype", 0);
                openDeviceName = data.getStringExtra("ipaddress");

            }
            if(resultCode == 2){
                printerName = data.getStringExtra("printername");
                language = data.getIntExtra("language", 0);
            }
        }

        if (printer != null) {
            printer.setStatusChangeEventCallback(this);
            printer.setBatteryStatusChangeEventCallback(this);
        }
        if(openDeviceName=="") {
            MyShowMessage.show(GlobalDataSet.getContext(),"You didn't select any printer");
        }
        else {
            txtDefaultPrinter.setText(openDeviceName);
            s.SaveMyShareData(GlobalDataSet.getContext(), s.GiveMeShareData(GlobalDataSet.getContext(), 1), openDeviceName);
        }
        //updateButtonState();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.gonetsoftware.administrator.ticketeurolines/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.gonetsoftware.administrator.ticketeurolines/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1 : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case 2 : {
                    if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    }
                return;
                }

        }
    }
}
