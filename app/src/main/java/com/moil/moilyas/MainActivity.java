package com.moil.moilyas;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.database.sqlite.*;
import android.provider.BaseColumns;

import org.w3c.dom.Text;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Context thisCtx=this;
    private Context getContext(){
        return thisCtx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button t1 = findViewById(R.id.chineseButton);
        final Button t3 = findViewById(R.id.englishButton);
        final TextView v1 = findViewById(R.id.maintxt);
        t1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                v1.setText("我的");
            }
        });
        t3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                v1.setText("Mine");
            }
        });


        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this);

        // Gets the data repository in write mode
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        /*ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, "HODOR");
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, "EYDOR");

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
        */
        final Button inserter = findViewById(R.id.DBInsert);
        final Button getter = findViewById(R.id.DBGet);
        final EditText titleEntry = findViewById(R.id.TitleEntryBox);
        final EditText subtitleEntry = findViewById(R.id.SubtitleEntryBox);
        final EditText titleGet = findViewById(R.id.TitleSearchBox);
        final TextView subtitleGet = findViewById(R.id.SubtitleGet);
        inserter.setOnClickListener(new View.OnClickListener(){
           public void onClick(View v){
               String title = titleEntry.getText().toString();
               String subtitle = subtitleEntry.getText().toString();
               if(!title.equals("") && !subtitle.equals("")) {
                   ContentValues values = new ContentValues();
                   values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, title);
                   values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);
                   long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);

                   Toast.makeText(getContext(), (String) title + " and " + subtitle + " added!", Toast.LENGTH_LONG).show();
                   titleEntry.setText("");
                   subtitleEntry.setText("");
               } else{
                   Toast.makeText(getContext(), "Please enter a title and subtitle!", Toast.LENGTH_SHORT).show();
               }


           }
        });
        getter.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String[] projection = {BaseColumns._ID, FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE};
                String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " = ?";
                String[] selectionArgs = {titleGet.getText().toString()};
                String sortOrder =
                        FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";
                Cursor cursor = db.query(FeedReaderContract.FeedEntry.TABLE_NAME, projection, selection, selectionArgs,null, null, sortOrder);
                //Cursor cursor = db.rawQuery("SELECT e._id, e.subitle FROM entry e WHERE title = '"+ titleGet.getText().toString()+"'",null);
                if(cursor.moveToNext()){
                    String subtitleItem = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE));
                    subtitleGet.setText(subtitleItem);
                } else subtitleGet.setText("");
                cursor.close();
            }
        });


    }

    public final class FeedReaderContract {
        // To prevent someone from accidentally instantiating the contract class,
        // make the constructor private.
        private FeedReaderContract() {}

        /* Inner class that defines the table contents */
        public class FeedEntry implements BaseColumns {
            public static final String TABLE_NAME = "entry";
            public static final String COLUMN_NAME_TITLE = "title";
            public static final String COLUMN_NAME_SUBTITLE = "subtitle";
        }
    }
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;

    public class FeedReaderDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "FeedReader.db";

        private FeedReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

}
