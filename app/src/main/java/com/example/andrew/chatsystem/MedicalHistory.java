package com.example.andrew.chatsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MedicalHistory extends AppCompatActivity implements SelectableViewHolder.OnItemSelectedListener {

    ArrayList<String> select;
    HashMap<String,Object> ResultString;
    String disease , currentUserID , hasMH;
    boolean wf , hf , df , gf , pf , sf , af;

    RecyclerView recyclerView;
    ListView listView;
    TextView save_btn ;
    EditText userAge ;
    List<Item> selectedItems , weightData  , hyperData, diabetesData, Gender, PsychiatricDisorders, Smoking, Alcohol;

    SelectableAdapter adapter;

    DatabaseReference userRef ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medical_history);

        currentUserID = Objects.requireNonNull(getIntent().getExtras()).getString("currentUserID");
        hasMH = Objects.requireNonNull(getIntent().getExtras()).getString("has_MH");

        initialize();

        String [] list={"Weight","Hypertension","Diabetes","Gender","Psychiatric Disorders","Smoking","Alcohol"};
        listView =findViewById(R.id.list1);

        listView.setAdapter(new ArrayAdapter<String>(MedicalHistory.this,android.R.layout.simple_list_item_1,list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Objects.requireNonNull(getSupportActionBar()).hide();

                switch (position)
                {
                    case 0:
                        loadrecycler(weightData,false);

                        disease="weight";

                        listView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        break;

                    case 1:
                        loadrecycler(hyperData,false);

                        disease="hyper";

                        listView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        break;

                    case 2:
                        loadrecycler(diabetesData,false);

                        disease="diabetes";
                        recyclerView.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                        break;

                    case 3:
                        loadrecycler(Gender,false);

                        disease="Gender";
                        recyclerView.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                        break;

                    case 4:
                        loadrecycler(PsychiatricDisorders,true);

                        disease="Psychiatric Disorders";
                        recyclerView.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                        break;

                    case 5:
                        loadrecycler(Smoking,false);

                        disease="Smoking";
                        recyclerView.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                        break;

                    case 6:
                        loadrecycler(Alcohol,false);

                        disease="Alcohol";
                        recyclerView.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                        break;

                        default:
                            break;
                }
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String age = userAge.getText().toString().trim() ;

                if (!TextUtils.isEmpty(age) && wf && hf && df && gf && pf && sf && af)
                {
                    ResultString.put("Age" , age);
                    userRef.updateChildren(ResultString).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(MedicalHistory.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                SendUserToMainActivity();
                            }

                            else
                                Toast.makeText(MedicalHistory.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                else if (!(TextUtils.isDigitsOnly(age)))
                    Toast.makeText(MedicalHistory.this, "its obvious that age must be digits only !", Toast.LENGTH_SHORT).show();

                else
                    Toast.makeText(MedicalHistory.this, "All fields are required", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void loadrecycler(List<Item> itemList,boolean multi)
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) this.findViewById(R.id.selection_list);
        recyclerView.setLayoutManager(layoutManager);
     //   List<Item> selectableItems = generateItems();
        adapter = new SelectableAdapter(this,itemList,multi);

        recyclerView.setAdapter(adapter);
    }

    public void initialize() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.medicalHistory_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Medical History");

        if(hasMH.equals("has_MH"))
        {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        wf = hf = df = gf = pf = sf = af = false;

        userAge = (EditText) findViewById(R.id.medicalHistory_et_age);
        save_btn = (TextView)findViewById(R.id.medicalHistory_save_btn);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Medical History") ;

        ResultString=new HashMap<String,Object>();

        ResultString.put("weight","");
        ResultString.put("hyper", "");
        ResultString.put("diabetes", "");
        ResultString.put("Gender", "");
        ResultString.put("Psychiatric Disorders", "");
        ResultString.put("Smoking", "");
        ResultString.put("Alcohol","");

        weightData = new ArrayList<>();
        weightData.add(new Item("  < 40 kg"));
        weightData.add(new Item("40 : 60 kg"));
        weightData.add(new Item("60 : 80 kg"));
        weightData.add(new Item("80 : 90 kg"));
        weightData.add(new Item("90 : 100 kg"));
        weightData.add(new Item("100 : 120 kg"));
        weightData.add(new Item("120 : 150 kg"));
        weightData.add(new Item(" > 150 kg"));

        hyperData = new ArrayList<>();
        hyperData.add(new Item("Normal-hypertensive (120/80 mmHg)"));
        hyperData.add(new Item("Low-hypertensive (100-119/70-79 mmHg)"));
        hyperData.add(new Item("Pre-hypertensive (120-139/80-89 mmHg)"));
        hyperData.add(new Item("Stage 1 hypertensive (140-159/90-99 mmHg)"));
        hyperData.add(new Item("Stage 2 hypertensive (=>160/=>100 mmHg)"));



        diabetesData = new ArrayList<>();
        diabetesData.add(new Item("Non Diabetic"));
        diabetesData.add(new Item("Pre-Diabetic"));
        diabetesData.add(new Item("Controlled Diabetic"));
        diabetesData.add(new Item("Uncontrolled Diabetic"));


        Gender = new ArrayList<>();
        Gender.add(new Item("Male"));
        Gender.add(new Item("Female"));


        PsychiatricDisorders = new ArrayList<>();
        PsychiatricDisorders.add(new Item("Non"));
        PsychiatricDisorders.add(new Item("Depression"));
        PsychiatricDisorders.add(new Item("Personality Disorder"));
        PsychiatricDisorders.add(new Item("Anxiety Disorder"));
        PsychiatricDisorders.add(new Item("Schizophrenia"));
        PsychiatricDisorders.add(new Item("Eating disorders"));
        PsychiatricDisorders.add(new Item("Addictive behaviors"));


        Smoking = new ArrayList<>();
        Smoking.add(new Item("Non Smoker "));
        Smoking.add(new Item("Past Smoker (At least one year)"));
        Smoking.add(new Item("Past Heavy Smoker (At least one year)"));
        Smoking.add(new Item("Current Smoker (Within the current year)"));
        Smoking.add(new Item("Current Heavy Smoker (Within the current year)"));
        Smoking.add(new Item("Non-daily(Occasional) Smoker"));


        Alcohol= new ArrayList<>();
        Alcohol.add(new Item("Non "));
        Alcohol.add(new Item("Light "));
        Alcohol.add(new Item("Moderate"));
        Alcohol.add(new Item("Heavy"));
        Alcohol.add(new Item("Very Heavy"));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public void onItemSelected(SelectableItem selectableItem) {

        selectedItems = adapter.getSelectedItems();

        select=adapter.getSelectedItemsnames();

        if (disease.equals("weight"))
        {
            ResultString.put("weight",selectableItem.getName()).toString();

            if (!TextUtils.isEmpty(selectableItem.getName()))
                wf = true ;
        }


        if (disease.equals("hyper"))
        {
            ResultString.put("hyper",selectableItem.getName()).toString();

            if (!TextUtils.isEmpty(selectableItem.getName()))
                hf = true ;
        }

        if (disease.equals("diabetes"))
        {
            ResultString.put("diabetes",selectableItem.getName()).toString();

            if (!TextUtils.isEmpty(selectableItem.getName()))
                df = true ;
        }

        if (disease.equals("Gender"))
        {
            ResultString.put("Gender",selectableItem.getName()).toString();

            if (!TextUtils.isEmpty(selectableItem.getName()))
                gf = true ;
        }

        if (disease.equals("Psychiatric Disorders"))
        {
            String olds= ResultString.get("Psychiatric Disorders").toString();
            olds+=selectableItem.getName()+',';
            ResultString.put("Psychiatric Disorders",olds);

            if (!TextUtils.isEmpty(selectableItem.getName()))
                pf = true ;
        }
        if (disease.equals("Smoking"))
        {
            ResultString.put("Smoking",selectableItem.getName()).toString();

            if (!TextUtils.isEmpty(selectableItem.getName()))
                sf = true ;

        }

        if (disease.equals("Alcohol"))
        {
            ResultString.put("Alcohol",selectableItem.getName()).toString();

            if (!TextUtils.isEmpty(selectableItem.getName()))
                af = true ;
        }
    }

    @Override
    public void onBackPressed() {
        try
        {
            recyclerView.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
            Objects.requireNonNull(getSupportActionBar()).show();
        }
        catch (Exception ex)
        {
            if(hasMH.equals("has_MH"))
            {
                Toast.makeText(this, "There is no change !", Toast.LENGTH_SHORT).show();
                SendUserToMainActivity();
            }
            else
                Toast.makeText(this, "Please , complete all fields then press save", Toast.LENGTH_SHORT).show();
        }
    }

    private void SendUserToMainActivity() {
        Intent intent = new Intent(MedicalHistory.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}