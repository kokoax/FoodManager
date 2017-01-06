package com.example.apublic.foodmanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.apublic.foodmanager.R;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by public on 2016/08/16.
 */
public class AddFood extends AppCompatActivity {

    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    static final int REQUEST_CAPTURE_IMAGE = 100;
    private DBcontrol foodDB;
    private Uri m_uri;
    private ImageButton foodImage;
    private EditText foodName;
    private DatePicker exPicker;
    private Button sendButton;
    private Bitmap capturedImage;
    private int id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_food_layout);

        Intent getter = getIntent();
        foodImage = (ImageButton) findViewById(R.id.foodImage);
        foodName = (EditText) findViewById(R.id.foodName);
        exPicker = (DatePicker) findViewById(R.id.exPicker);

        sendButton = (Button) findViewById(R.id.return_button);
        capturedImage = BitmapFactory.decodeResource(getResources(), R.drawable.noimage);

        foodDB = new DBcontrol(this);

        // 商品リストのEditからインテントを実行していた場合元からあるデータを
        // 表示する.
        if( (this.id = getter.getIntExtra("ID", -1)) != -1 ) {

            foodImage.setImageBitmap(BitmapFactory.decodeFile((String)getter.getExtras().get("IMAGE")));

            foodName.setText(getter.getStringExtra("TITLE"));

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Calendar exp = Calendar.getInstance();
            try {
                exp.setTime( formatter.parse(getter.getStringExtra("EXP")) );
            } catch (ParseException e) {
                e.printStackTrace();
            }
            exPicker.updateDate(exp.get(Calendar.YEAR), exp.get(Calendar.MONTH), exp.get(Calendar.DAY_OF_MONTH));
            sendButton.setText("変更");
        }

        foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // ACTION_IMAGE_CAPTURE に対応する アプリ をインテント
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //　ここのREQUEST_CAPTURE_IMAGEのインテントが実行されるとResultのrequestCodeに代入してonActivityResultが実行される
                startActivityForResult(intentCamera, REQUEST_CAPTURE_IMAGE);
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText foodName = (EditText)findViewById(R.id.foodName);
                DatePicker exPicker = (DatePicker)findViewById(R.id.exPicker);
                if( !foodName.getText().toString().equals("") ) {
                    foodDB.open();

                    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy'-'MM'-'dd");

                    GregorianCalendar exDate = new GregorianCalendar
                            ( exPicker.getYear(), exPicker.getMonth(), exPicker.getDayOfMonth() );

                    if(id == -1) {
                        foodDB.saveFoodData(foodName.getText().toString(), sdf.format(exDate.getTime()), capturedImage);
                    } else {
                        foodDB.updateFoodData(id, foodName.getText().toString(), sdf.format(exDate.getTime()), capturedImage);
                    }
                    foodDB.close();
                    finish();
                }
            }
        });
    }
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data){
        if( REQUEST_CAPTURE_IMAGE == requestCode
                && resultCode == Activity.RESULT_OK ){
            capturedImage =(Bitmap)data.getExtras().get("data");
            foodImage.setImageResource(android.R.color.transparent);
            foodImage.setImageBitmap(capturedImage);
        }
    }
}
