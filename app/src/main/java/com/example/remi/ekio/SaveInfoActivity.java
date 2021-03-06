package com.example.remi.ekio;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.util.Calendar;

/**
 * Created by Hoang Nam on 31/05/2016.
 */
public class SaveInfoActivity extends Activity {

    public final static String MESSAGE_KEY = "com.example.remi.ekio.messagekey";
    public final static String MESSAGE_DEL = "com.example.remi.ekio.messagedel";
    public final static String MESSAGE_FROMBIG = "com.example.remi.ekio.messagefrombig";
    ImageView preview;
    EditText etDate, etTitle, etComment, etKeyWord, etLocation;
    ImageButton geo;
    String name,path, pathtemp;
    boolean isEdit, isFromBig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_info_layout);

        // get intent fot photo path
        Intent intent = getIntent();
        name = intent.getStringExtra(MESSAGE_KEY);
        path = "sdcard/EkioPhotos/"+name;
        pathtemp = "sdcard/EkioPhotos/tmp/" + name;
        isFromBig = intent.getBooleanExtra(MESSAGE_FROMBIG,false);
        isEdit = intent.getBooleanExtra(MESSAGE_DEL,false);
        //path = "sdcard/EkioPhotos/"+name;

        //get edit Text
        etTitle = (EditText) findViewById(R.id.photo_title);
        etComment = (EditText) findViewById(R.id.photo_comment);
        etKeyWord = (EditText) findViewById(R.id.keyword);
        etLocation = (EditText) findViewById(R.id.photo_location);



        // get other component
        geo = (ImageButton) findViewById(R.id.geolocalisation);
        preview = (ImageView) findViewById(R.id.photo);
        if (isFromBig!=true){
            preview.setImageDrawable(Drawable.createFromPath(path));
        }else preview.setImageDrawable(Drawable.createFromPath(pathtemp));


        // set date
        etDate = (EditText) findViewById(R.id.photo_date);
        final Calendar cal = Calendar.getInstance();
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        int mm = cal.get(Calendar.MONTH);
        int yy = cal.get(Calendar.YEAR);
        // set current date into textview
        etDate.setText(new StringBuilder()
        // Month is 0 based, just add 1
                .append(dd).append("/").append(mm + 1).append("/")
                .append(yy));

        if (isEdit == true){
            CollectionableDAO objectDao = new CollectionableDAO(this);
            objectDao.open();
            int x = objectDao.getIdFromPath(path);
            etTitle.setText(objectDao.select(x).getTitle());
            etDate.setText(objectDao.select(x).getDate());
            etKeyWord.setText(objectDao.select(x).getKeyWords());
            etLocation.setText(objectDao.select(x).getLocation());
            etComment.setText(objectDao.select(x).getcomment());
            objectDao.close();
        }
    }

    public void goResult(View view){

        final String title = etTitle.getText().toString();
        final String comment = etComment.getText().toString();
        final String keyWords = etKeyWord.getText().toString();
        final String date = etDate.getText().toString();
        final String location = etLocation.getText().toString();


        CollectionableDAO objectDao = new CollectionableDAO(this);
        objectDao.open();
        if(isEdit == true){
            objectDao.delete(objectDao.getIdFromPath(path));
        }
        if (isFromBig==true){
            File x = new File(pathtemp);
            if (x.exists()){
                //Toast.makeText(getApplicationContext(), " file existe", Toast.LENGTH_SHORT).show();
                x.renameTo(new File(path));
            }

        }
        Collectionable object = new Collectionable(title, date, location, comment, keyWords, path, true);
        int id = objectDao.ajouter(object);
        objectDao.close();

        Toast.makeText(getApplicationContext(),
                title + " has been saved ", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ChooseFromCollectionActivity.class);
        intent.putExtra(MESSAGE_KEY, id);
        startActivity(intent);
        finish();
    }

    public void premiumFeature(View view){
        Toast.makeText(getApplicationContext(),
                "This is a premium feature !!", Toast.LENGTH_LONG).show();
    }

    public void goHome(View view){
        if (isFromBig==true){
            File x = new File(pathtemp);
            x.delete();
        }
        Intent goHome = new Intent(this, CollectionShowcaseActivity.class);
        startActivity(goHome);
        finish();
    }

}
