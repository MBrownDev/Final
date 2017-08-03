package com.example.brown.afinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Brown on 7/9/2017.
 */

public class contactDatabase {
    ContactHelper helper;
    SQLiteDatabase db;

    contactDatabase(Context context){helper = new ContactHelper(context);}

    public contactDatabase open(){
        db = helper.getWritableDatabase();
        return this;
    }

    public long insertPassword(String password){
        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactHelper.KEY_PASSWORD,password);
        long id = db.insert(ContactHelper.TABLE_NAME,null,contentValues);
        return id;
    }

    public long insertNumber(String name, String number){
        open();
        ContentValues contentValues = new ContentValues();

        if(((name != null )||(name != ""))&&(number != null)||(number != "")) {
            contentValues.put(ContactHelper.KEY_NAME, name);
            contentValues.put(ContactHelper.KEY_NUMBER, number);
        }
        long id = db.insert(ContactHelper.TABLE_NAME,null,contentValues);
        return id;
    }

    public Cursor checkMessage(){
        Cursor c = db.rawQuery("SELECT " + ContactHelper.KEY_MESSAGE + " FROM " + ContactHelper.TABLE_NAME,null);
        return c;
    }

    public long setMessage(String message){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactHelper.KEY_MESSAGE,message);
        long id = db.insert(ContactHelper.TABLE_NAME,null,contentValues);
        return id;
    }

    public Cursor getAllRows(){
        String where = null;
        String[] All_KEYS = new String[]{ContactHelper.KEY_ID,ContactHelper.KEY_NAME,ContactHelper.KEY_NUMBER};
        Cursor c = db.query(ContactHelper.TABLE_NAME,All_KEYS,where,null,null,null,null);
        if(c != null){
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getMessages(){
        Cursor c = db.rawQuery("SELECT " + helper.KEY_MESSAGE + " FROM " + helper.TABLE_NAME, null);
        if(c != null){
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getNumber(){
        Cursor c = db.rawQuery("SELECT " + helper.KEY_NUMBER + " FROM " + helper.TABLE_NAME, null);
        if(c != null){
            c.moveToFirst();
        }
        return c;
    }

    class ContactHelper extends SQLiteOpenHelper {

        private Context context;

        private static final String DATABASE_NAME = "emergencycontacts";
        public static final String TABLE_NAME = "CONTACTS_TABLE";
        private static final int DATABASE_VERSION = 1;

        public static final String KEY_ID = "_id";
        public static final String KEY_NAME = "_name";
        public static final String KEY_NUMBER = "_number";
        public static final String KEY_PASSWORD = "_password";
        public static final String KEY_MESSAGE = "_message";


        private static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" + KEY_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + KEY_NAME + " VARCHAR(255),"
                        + KEY_NUMBER + " VARCHAR(255),"
                        + KEY_MESSAGE + " VARCHAR(255),"
                        + KEY_PASSWORD + " VARCHAR(255));";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS "
                + TABLE_NAME;

        public ContactHelper(Context context) {
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE);
            }catch (SQLException e){

            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try{
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch (SQLException e){

            }
        }
    }
}
