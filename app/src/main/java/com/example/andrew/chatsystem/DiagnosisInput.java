package com.example.andrew.chatsystem;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.Locale;


public class DiagnosisInput extends AppCompatActivity  {

    private EditText temperature;
    private TextView nausea, lumbarpain, micturition, urinepushing, burningofurine, urinetext, kidneytext;
    private Switch snausea, slumbarpain, smicturition, surinepushing, sburningofurine;
    private TextToSpeech t1;

    double Temperature=0;
    double[] result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnosis_input);

        initialize();

        FloatingActionButton floatingActionButton=(FloatingActionButton)findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urinetext.setText("Urine Inflammations:  ");
                kidneytext.setText("Kidney Inflammations: ");
                if(!TextUtils.isEmpty(temperature.getText().toString()))
                {
                    Temperature=Double.parseDouble(temperature.getText().toString());

                    result= getUrineInflammations();
                    String urine="";
                    String kidney="";

                    urinetext.append("Positive with percentage "+String.valueOf(Math.round(result[0]))+"%"+"\n"+"Negative with percentage"+String.valueOf(Math.round(result[1]))+'%');
                    urinetext.setVisibility(View.VISIBLE);
                    kidneytext.append("Positive with percentage"+String.valueOf(Math.round(result[2]))+"%"+"\n"+"Negative with percentage "+String.valueOf(Math.round(result[3]))+"%");
                    kidneytext.setVisibility(View.VISIBLE);
                    String toSpeak="";
                    String name="";
                    String namekidney="";
                    double max=0;
                    double maxkidney=0;
                    if (result[0]>result[1]) {
                        max = (result[0]);
                        name="Urine inflammations is Positive with ";
                    }
                    else {
                        max = (result[1]);
                        name="Urine inflammations is negative";
                    }
                    if (result[2]>result[3]) {
                        maxkidney = (result[0]);
                        namekidney=" Kidney inflammtions is Positive with";
                    }
                    else {
                        maxkidney = (result[3]);
                        namekidney="Kidney inflammations is negative with";
                    }

                    if (max>maxkidney)
                        toSpeak=name+String.valueOf(Math.round(max))+"%";
                    else
                        toSpeak=namekidney+String.valueOf(Math.round(maxkidney))+"%";


                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    t1.setSpeechRate((float)(1.0));
                }
                else
                    Toast.makeText(DiagnosisInput.this, "Temperature is required !", Toast.LENGTH_SHORT).show();

            }
        });
}

    @Override
    public void onBackPressed() {
        t1.stop();
        super.onBackPressed();
    }

    public void initialize()
{

    temperature=(EditText)findViewById(R.id.editText);

    t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if(status != TextToSpeech.ERROR) {
                t1.setLanguage(Locale.UK);
            }
        }
    });

    Toolbar toolbar = (Toolbar)findViewById(R.id.diagnose_AppBar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setTitle("Diagnoses");

    nausea=(TextView)findViewById(R.id.textView);
    lumbarpain=(TextView)findViewById(R.id.textView2);
    micturition=(TextView)findViewById(R.id.textView3);
    urinepushing=(TextView)findViewById(R.id.textView4);
    burningofurine=(TextView)findViewById(R.id.textView5);

    urinetext=(TextView)findViewById(R.id.textView6);
    kidneytext=(TextView)findViewById(R.id.textView7);

    snausea=(Switch)findViewById(R.id.switch1);
    slumbarpain=(Switch)findViewById(R.id.switch2);
    smicturition=(Switch)findViewById(R.id.switch3);
    surinepushing=(Switch)findViewById(R.id.switch4);
    sburningofurine=(Switch)findViewById(R.id.switch5);
}
public double[] getUrineInflammations()
{
    boolean nauseaCheck=snausea.isChecked();
    boolean lumbarCheck=slumbarpain.isChecked();
    boolean pushingCheck=surinepushing.isChecked();
    boolean burningCheck=surinepushing.isChecked();

    double urineTruepercent=0;
    double urineFalsepercent=0;
    double kidneyTruepercent=0;
    double kidneyFalsepercent=0;
    double urinefalsecount=61;
    double kidneyfalsecount=70;
    double kidneytruecount=50;
    double urinetruecount=59;




    if(Temperature<37.95)
    {
       kidneyFalsepercent=60;

        if(pushingCheck==false){ urineFalsepercent=20; }

        else{ urineTruepercent=40; }
    }
    else
    {
        if (pushingCheck == true)   { kidneyTruepercent=40;}

        else if (pushingCheck == false)
        {

            if (lumbarCheck == false) { kidneyFalsepercent=10;}

            else if (lumbarCheck == true){ kidneyTruepercent=10;}
        }
            if (Temperature < 39.85) { urineFalsepercent=10; }

            else
            {
                if (nauseaCheck == false) { urineFalsepercent=21; }

                else
                {
                    if (burningCheck == true) { urineTruepercent=9; }
                    else {

                        if (Temperature >= 41.4) {

                            urineFalsepercent=1;
                        } else {
                            if (Temperature >= 41.25) {

                                urineTruepercent=1;
                            } else {
                                if (Temperature < 40.95) {
                                    if (pushingCheck == true) {

                                        urineTruepercent=6;
                                    } else {

                                        urineFalsepercent=8;
                                    }
                                } else if (Temperature >= 40.95) {
                                    if (pushingCheck == true) {

                                        urineTruepercent=1;
                                    } else {

                                        urineFalsepercent=3;
                                    }
                                }


                            }

                        }
                    }
                }
            }
        }

        urineFalsepercent=(urineFalsepercent/urinefalsecount)*100;
        urineTruepercent=(urineTruepercent/urinetruecount)*100;

        kidneyFalsepercent=(kidneyFalsepercent/kidneyfalsecount)*100;
        kidneyTruepercent=(kidneyTruepercent/kidneytruecount)*100;
        return new double[]{urineTruepercent,urineFalsepercent,kidneyTruepercent,kidneyFalsepercent};

    }



}





