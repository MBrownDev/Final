package com.example.brown.afinal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactList extends AppCompatActivity {

    contactDatabase cdHelper;
    SQLiteDatabase db;

    SimpleCursorAdapter SCA;
    ListView myList;

    private void openDB(){
        cdHelper = new contactDatabase(this);
        db = cdHelper.helper.getWritableDatabase();
        cdHelper.open();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);

        openDB();
        populateList();
    }

    public void populateList(){
        Cursor cursor = cdHelper.getAllRows();
        String[] columnFields = new String[]{cdHelper.helper.KEY_ID,cdHelper.helper.KEY_NAME,cdHelper.helper.KEY_NUMBER};
        int[] fieldIds = new int[]{R.id.item,R.id.Name,R.id.Number};
        SCA = new SimpleCursorAdapter(getBaseContext(),R.layout.list_item,cursor,columnFields,fieldIds,0);
        myList = (ListView)findViewById(R.id.contact_list);
        myList.setAdapter(SCA);

    }
}
