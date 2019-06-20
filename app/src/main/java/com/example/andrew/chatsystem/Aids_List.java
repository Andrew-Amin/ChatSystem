package com.example.andrew.chatsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

public class Aids_List extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aids__list);
        ListView listView = findViewById(R.id.list1);

        Toolbar toolbar = (Toolbar)findViewById(R.id.FA_AppBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("First Aid");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] array = new String[]{"حالات الكسر", "اغماء السكر", "التنفس الاصطناعي", "فقدان الوعي", "النزيف"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
        listView.setAdapter(arrayAdapter);
        final Intent i = new Intent(Aids_List.this, Result.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    i.putExtra("aidid", "smashing");
                else if (position == 1)

                    i.putExtra("aidid", "sugar");
                else if (position == 2)

                    i.putExtra("aidid", "Artificialrespiration");
                else if (position == 3)
                    i.putExtra("aidid", "Unconsciousness");
                else if (position == 4)
                    i.putExtra("aidid", "bleeding");
                startActivity(i);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Aids_List.this, MainActivity.class));
    }
}
