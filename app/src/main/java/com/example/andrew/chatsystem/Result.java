package com.example.andrew.chatsystem;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Result extends AppCompatActivity {
    int [] Photos;
    SqlDatabase database;
    String[] aidtext;
    String[] aidimages;
    String [] aidheaders;
    String aid;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(Result.this,Aids_List.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        database=new SqlDatabase(this);
       aid=getIntent().getExtras().getString("aidid");
        Cursor cur= database.getinfo(aid);
       String aidstring=cur.getString(0);
       String aidheads=cur.getString(1);
       String aidphotos=cur.getString(2);

        aidtext=aidstring.split("/");
       aidimages=aidphotos.split(",");
       aidheaders=aidheads.split("/");

        Photos=new int[]{R.drawable.kosor1,R.drawable.nazeef,R.drawable.kosor2,R.drawable.tanafos};
        LinearLayout myLinearLayout=(LinearLayout)findViewById(R.id.linear);
        final int NofImages = aidimages.length; // total number of textviews to add
        final int N = aidtext.length; // total number of textviews to add
        final int Nhead = aidheaders.length; // total number of textviews to add

        final ImageView[] myImageViews = new ImageView[NofImages]; // create an empty array;
        final TextView[] textViews = new TextView[N+Nhead]; // create an empty array;
        int j=0;
        int photoindex=0;
        for (int i = 0; i < N; i++) {


            if(!(aidtext[i].equals("--")))

            {

                final TextView headtext = new TextView(this);
                headtext.setText(aidheaders[j]);
                headtext.setTextSize(20);
                headtext.setTextColor(Color.parseColor("#FF175C"));

                myLinearLayout.addView(headtext);

                final TextView rowtext = new TextView(this);
                rowtext.setText(aidtext[i]);
                myLinearLayout.addView(rowtext);
                j++;
            }
            else {
                final ImageView imageView = new ImageView(this);
                int value=Integer.parseInt(aidimages[photoindex]);
                imageView.setImageResource(Photos[value]);

                myLinearLayout.addView(imageView);

                imageView.getLayoutParams().height = 800;

                imageView.getLayoutParams().width = 900;

                imageView.setScaleType(ImageView.ScaleType.FIT_XY);


                photoindex++;


            }





            // set some properties of rowTextView or something

            //rowTextView.setText("This is row #" + i);

            // add the textview to the linearlayout




            // save a reference to the textview for later
          //  myImageViews[i] = rowTextView;
        }

    }
}
