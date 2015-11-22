package com.b1g0n3.mycamremote;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class GlobalSettingsActivity extends Activity {

	Button ButtonEmpty,Button1;

	Spinner Spinner1;
	String a_rate="3";
	CharSequence n_rate;
	RadioButton rb1, rb2, rb3, rb4, rb5;
	private TextView About;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_globalsettings);
//        ButtonEmpty = (Button)findViewById(R.id.buttonEmpty);
        Button1 = (Button)findViewById(R.id.button1);
		About= (TextView) findViewById(R.id.about);
		SQLiteDatabase db;
		db=openOrCreateDatabase("MyCamRemote", Context.MODE_PRIVATE,null);
		db.execSQL("CREATE TABLE IF NOT EXISTS Refresh(Vrate VARCHAR);");
		Cursor c=db.rawQuery("SELECT * FROM Refresh",null );
		if (c.moveToFirst()) {
			a_rate=c.getString(0);
		}	
		c.close(); db.close();

		About.setText("MyCamRemote 2.0c.build74 Developed by Francois LONGIN");
		About.append("\ncontacts,comments and support: longin.francois@gmail.com");

		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
	    rb1=(RadioButton) findViewById(R.id.radio0);
	    rb2=(RadioButton) findViewById(R.id.radio1);
	    rb3=(RadioButton) findViewById(R.id.radio2);
	    rb4=(RadioButton) findViewById(R.id.radio3);
	    rb5=(RadioButton) findViewById(R.id.radio4);
	    switch (Integer.valueOf(a_rate)) {
	    	case 2 : { rb1.setChecked(true); break; }
	    	case 3 : { rb2.setChecked(true); break; }
	    	case 4 : { rb3.setChecked(true); break; }
	    	case 5 : { rb4.setChecked(true); break; }
	    	case 10 : { rb5.setChecked(true); break; }
	    }
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
    			RadioButton rb=(RadioButton)findViewById(checkedId);
    			n_rate=rb.getText();
    	    	System.out.println("ancien rate="+a_rate+" new rate="+n_rate);
    			SQLiteDatabase db; 
    	    	db= openOrCreateDatabase("MyCamRemote", Context.MODE_PRIVATE,null);
    			db.execSQL("DROP TABLE IF EXISTS Refresh");
    			db.execSQL("CREATE TABLE IF NOT EXISTS Refresh(Vrate VARCHAR);");
    			db.execSQL("INSERT INTO Refresh VALUES('"+n_rate+"')");
    			db.close();
            }
        });
        
//        ButtonEmpty.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//        		SQLiteDatabase db;
//        		db=openOrCreateDatabase("MyCamRemote", Context.MODE_WORLD_WRITEABLE, null);
//        		db.execSQL("DROP TABLE IF EXISTS Gopro");
//        		db.close();
//            	Toast.makeText(getApplicationContext(), "Gopro database deleted",Toast.LENGTH_LONG).show();
//            }
//        });

        Button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
        		SQLiteDatabase db;
        		db=openOrCreateDatabase("MyCamRemote", Context.MODE_WORLD_WRITEABLE, null);
        		db.execSQL("DROP TABLE IF EXISTS VideoPreset");
        		db.close();
            	Toast.makeText(getApplicationContext(), "Preset database deleted",Toast.LENGTH_LONG).show();
            }
        });
	}
	
	@Override
	protected void onDestroy() {
    	overridePendingTransition(R.anim.slidedown_in, R.anim.slidedown_out);
		super.onDestroy();
	}

}
