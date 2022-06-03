package com.gonetsoftware.administrator.ticketeurolines;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class MyPDF extends ListActivity {

    ListView lv;
    String[] alFileList=null;

    Context MyCtx;

    int I_POS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pdf);

        SaveShareData s=new SaveShareData();
        alFileList=GetFiles(s.GiveMeShareData(GlobalDataSet.getContext(),1));

        lv= getListView();
        lv.setChoiceMode(lv.CHOICE_MODE_MULTIPLE);
        lv.setTextFilterEnabled(true);
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, alFileList));


    }

    public void onListItemClick(ListView parent, View v,int position,long id){
        boolean myck=parent.isItemChecked(position);

        String text = parent.getItemAtPosition(position).toString();
        CheckedTextView item = (CheckedTextView) v;
        I_POS=position;



        v.setSelected(true);


        if(item.isEnabled()==true) {

            //item.setChecked(true);
            item.setEnabled(false);

            MyAlertDialog(text,item);


        }
        else {
            lv.setItemChecked(position,true);

        }

    }

    private int MyAlertDialog(final String sFileName, final CheckedTextView MyItem)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Este selectat fiserul \r\n" + sFileName );
        builder.setMessage("Vrei sa continui operatia de tiparire ?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do do my action here
                GlobalDataSet.setS_PDFFileName(sFileName);
                MyItem.setEnabled(false);
                lv.setItemChecked(I_POS, true);


                dialog.dismiss();
                Intent mP=new Intent(MyPDF.this,MyPDFPrint.class);
                startActivity(mP);

            }

        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                MyItem.setEnabled(true);
                lv.setItemChecked(I_POS,false);
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        return 0;
    }

    private  String[] GetFiles(String path) {
        ArrayList<String> arr2 = new ArrayList<String>();
        File file = new File(path);
        FilenameFilter flt=new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if(filename.lastIndexOf('.')>0)
                {
                    // get last index for '.' char
                    int lastIndex = filename.lastIndexOf('.');

                    // get extension
                    String str = filename.substring(lastIndex);

                    // match path name extension
                    if(str.equals(".pdf"))
                    {
                        return true;
                    }
                }
                return false;
            }
        };
        File[] allfiles = file.listFiles();
        if (allfiles.length == 0)
        {
            return null;
        }
        else
        {
            for (int i = 0; i < allfiles.length; i++)
            {
                arr2.add(allfiles[i].getName());
            }
        }
        //convert ListArray to String array
        return arr2.toArray(new String[arr2.size()]);
    }
}
