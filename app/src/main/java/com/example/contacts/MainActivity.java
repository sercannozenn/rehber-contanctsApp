package com.example.contacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ListView listView = findViewById(R.id.listView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                {
                    //Rehber okuma izni varsa
                    ContentResolver contentResolver = getContentResolver();//içerik çözümleyicisi ile içerik alınır.
                    String[] projection = {ContactsContract.Contacts.DISPLAY_NAME};
                    String order = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
                    Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, order);

                    if (cursor != null)
                    {
                        ArrayList<String> contactList = new ArrayList<String>();
//                        String columnIndexName = ContactsContract.Contacts.DISPLAY_NAME;
                        String columnIndexID = ContactsContract.CommonDataKinds.Phone._ID;
                        String columnIndexName = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
                        while (cursor.moveToNext())
                        {
                            String id=cursor.getString(cursor.getColumnIndex(columnIndexID));
                            if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                            {
                                Cursor number= contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?", new String[]{id},null);
                                while (number.moveToNext()) {
                                    String phone = number.getString(number
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    Log.d("ser", "\tPHONE: " + phone);
                                    contactList.add(cursor.getString(cursor.getColumnIndex(columnIndexName)) + " - " + phone);

                                }
                                number.close();
                            }
//                            String id= cursor.getString(cursor.getColumnIndex(columnIndexID));
//                            Log.d("ContactsActivityTAG","YeniID:" + id);
                        }
                        cursor.close();

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, contactList);
                        listView.setAdapter(adapter);
                    }
                }
                else
                {
                    Snackbar.make(view, "İzin vermeniz gerekli.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("İzin Ver", new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS))
                                    {
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                                    }
                                    else
                                    {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                                        intent.setData(uri);
                                        MainActivity.this.startActivity(intent);
                                    }
                                }
                            }).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
