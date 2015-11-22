package com.b1g0n3.mycamremote;

import java.util.Date;

import org.gopro.core.GoProHelper;
import org.gopro.core.model.BacPacStatus;
import org.gopro.core.model.CamFields;
import org.gopro.main.GoProApi;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SettingsToPresetActivity extends Activity {
	
	int GoproModel = 0;
	String GoproMode = "0";
	int a_fps=0, a_Fov=0, a_VideoRes=0, a_ProtuneVideoEnabled = 0, a_ProtuneVideoSetting = 0, a_VideoExposure = 0,a_LowLightEnabled=0;
	public Button ButtonPreset1,ButtonPreset2,ButtonPreset3,ButtonPreset4,ButtonPreset5,ButtonPreset6;
	String[] Preset = {"0,0,0,0,0,0,0","0,0,0,0,0,0,0","0,0,0,0,0,0,0","0,0,0,0,0,0,0","0,0,0,0,0,0,0","0,0,0,0,0,0,0","0,0,0,0,0,0,0"};
	String[] libPreset = {"Empty","Empty","Empty","Empty","Empty","Empty","Empty"};
	String libPVS[] = {"Auto","3000K","5500K","6500K","RAW"};
	String libFov[] = {"W","M","N"};
	String libVidres[] = {"WVGA","720","960","1080","1440","2.7K","4K","2.7K Cin","4K Cin","1080 S","720 S","XX","XX","XX","XX","XX","XX"};
	String libVidres4[] = {"","4K","4K Cin","","2.7K","2.7K Cin","2.7K 4:3","1440","1080 Sup","1080","960","720 Sup","720","WVGA","XX","XX","XX","XX","XX","XX"};
	String libFps[]={"12","15","24","25","30","48","50","60","100","120","240","XX","XX","XX","XX","XX","XX" };
	String libFps4[]={"240","120","100","90","80","60","50","48","30","25","24","XX","XX","XX","XX","XX","XX" };
	public String GoproPassword; 
	private CamFields camFields;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settingstopreset);
		ButtonPreset1 = (Button) findViewById(R.id.button1);
		ButtonPreset2 = (Button) findViewById(R.id.button2);
		ButtonPreset3 = (Button) findViewById(R.id.button3);
		ButtonPreset4 = (Button) findViewById(R.id.button4);
		ButtonPreset5 = (Button) findViewById(R.id.button5);
		ButtonPreset6 = (Button) findViewById(R.id.button6);
		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
		    GoproModel = Integer.valueOf(extras.getString("GoproModel"));
		    GoproMode =  extras.getString("GoproMode");
		    GoproPassword = extras.getString("GoproPassword");
		    a_ProtuneVideoEnabled =  extras.getInt("ProtuneVideoEnabled");
		    a_ProtuneVideoSetting = extras.getInt("ProtuneVideoSetting");
			a_LowLightEnabled = extras.getInt("LowLightEnabled");
			a_VideoExposure =  extras.getInt("VideoExposure");
		    a_fps =  extras.getInt("fps");
		    a_VideoRes =  extras.getInt("VideoRes");
		    a_Fov =  extras.getInt("Angle");
		}
		SQLiteDatabase db;
		Log.v("SavePreset", "1");
		db=openOrCreateDatabase("MygoproRemote2", Context.MODE_PRIVATE,null);
		db.execSQL("CREATE TABLE IF NOT EXISTS VideoPreset(Vnum VARCHAR, VVidres1 VARCHAR, VFps1 VARCHAR, VFov1 VARCHAR, VVidres2 VARCHAR, VFps2 VARCHAR, VFov2 VARCHAR, VVidres3 VARCHAR, VFps3 VARCHAR, VFov3 VARCHAR);");
		Log.v("SavePreset", "2");
		Cursor c=db.rawQuery("SELECT * FROM VideoPreset",null );
		if (c.moveToFirst()) {
			Log.v("SavePreset", "3");
			Preset[1]=c.getString(1); Preset[2]=c.getString(2); Preset[3]=c.getString(3); Preset[4]=c.getString(4); Preset[5]=c.getString(5); Preset[6]=c.getString(6);
			if (Preset[1].equals("0,0,0,0,0,0,0")) { libPreset[1]="Empty"; } else { libPreset[1]=lib(1); }
			if (Preset[2].equals("0,0,0,0,0,0,0")) { libPreset[2]="Empty"; } else { libPreset[2]=lib(2); }
			if (Preset[3].equals("0,0,0,0,0,0,0")) { libPreset[3]="Empty"; } else { libPreset[3]=lib(3); }
			if (Preset[4].equals("0,0,0,0,0,0,0")) { libPreset[4]="Empty"; } else { libPreset[4]=lib(4); }
			if (Preset[5].equals("0,0,0,0,0,0,0")) { libPreset[5]="Empty"; } else { libPreset[5]=lib(5); }
			if (Preset[6].equals("0,0,0,0,0,0,0")) { libPreset[6]="Empty"; } else { libPreset[6]=lib(6); }
		} else {
			Log.v("SavePreset", "4");
			libPreset[1]="Empty<br>"; libPreset[2]="Empty"; libPreset[3]="Empty";
			libPreset[4]="Empty<br>"; libPreset[5]="Empty"; libPreset[6]="Empty";
			System.out.println("Table vide");
		}
		c.close(); db.close();
		Log.v("SavePreset", "5");
		ButtonPreset1.setText(Html.fromHtml(libPreset[1])); ButtonPreset2.setText(Html.fromHtml(libPreset[2])); ButtonPreset3.setText(Html.fromHtml(libPreset[3]));
		ButtonPreset4.setText(Html.fromHtml(libPreset[4])); ButtonPreset5.setText(Html.fromHtml(libPreset[5])); ButtonPreset6.setText(Html.fromHtml(libPreset[6]));

		ButtonPreset1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onPush(1);
                ButtonPreset1.setText(Html.fromHtml(libPreset[1]));
            }
        });
		ButtonPreset2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onPush(2);
                ButtonPreset2.setText(Html.fromHtml(libPreset[2]));
            }
        });
		ButtonPreset3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onPush(3);
                ButtonPreset3.setText(Html.fromHtml(libPreset[3]));
            }
        });
		ButtonPreset4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onPush(4);
                ButtonPreset4.setText(Html.fromHtml(libPreset[4]));
            }
        });
		ButtonPreset5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onPush(5);
                ButtonPreset5.setText(Html.fromHtml(libPreset[5]));
            }
        });
		ButtonPreset6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onPush(6);
                ButtonPreset6.setText(Html.fromHtml(libPreset[6]));
            }
        });
	}

	protected void onPush(Integer preset)  {
	
		GoProApi gopro = new GoProApi(GoproPassword);
		GoProHelper helper = gopro.getHelper();
		try {
			camFields = helper.getCameraSettings();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (GoproModel==3) { 
			BacPacStatus bacpacStatus;
			try {
				bacpacStatus = helper.getBacpacStatus();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				camFields = helper.getCameraSettingsExtended();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		if (GoproModel==4) {
			try {
				camFields = helper.getCameraSettingsExtended4();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		a_ProtuneVideoEnabled =camFields.getProtuneVideoEnabled();
		if (a_ProtuneVideoEnabled ==6) { a_ProtuneVideoEnabled =1; }
		if (a_ProtuneVideoEnabled ==4) { a_ProtuneVideoEnabled =0; }
		a_ProtuneVideoSetting=camFields.getProtuneVideoSetting();
		a_VideoExposure = camFields.getVideoExposure();
		a_fps = camFields.getFramesPerSecond();
		if (camFields.getLowLight()) { a_LowLightEnabled= 1; } else { a_LowLightEnabled= 0; }
		a_VideoRes =camFields.getVidres();
		a_Fov = camFields.getFieldOfView();
		if (GoproModel==3) { 
			Preset[preset]=libVidres[a_VideoRes]+","+libFps[a_fps]+","+libFov[a_Fov]+","+a_ProtuneVideoEnabled+","+libPVS[a_ProtuneVideoSetting]+","+a_VideoExposure+","+a_LowLightEnabled ;
			libPreset[preset]="<b>"+libVidres[a_VideoRes]+" / "+libFps[a_fps]+" / "+libFov[a_Fov]+"</b>";
		} else {
			Preset[preset]=libVidres4[a_VideoRes]+","+libFps4[a_fps]+","+libFov[a_Fov]+","+a_ProtuneVideoEnabled+","+libPVS[a_ProtuneVideoSetting]+","+a_VideoExposure+","+a_LowLightEnabled;
			libPreset[preset]="<b>"+libVidres4[a_VideoRes]+" / "+libFps4[a_fps]+" / "+libFov[a_Fov]+"</b>";
		}
		if (a_ProtuneVideoEnabled==1) { libPreset[preset]+="<br><small>PT ("+libPVS[a_ProtuneVideoSetting]+")</small>"; } else { libPreset[preset]+="<br><small>PT Off</small>"; }
		if (a_VideoExposure==1) { libPreset[preset]+="<small> / SM On</small>"; } else { libPreset[preset]+="<small> / SM Off</small>"; }
		if (a_LowLightEnabled==1) { libPreset[preset]+="<small> / Low On</small>"; } else { libPreset[preset]+="<small> / Low Off</small>"; }
	}

	protected void onSave() {
		SQLiteDatabase db; 
		db= openOrCreateDatabase("MygoproRemote2", Context.MODE_PRIVATE,null);
		db.execSQL("DROP TABLE IF EXISTS VideoPreset");
		db.execSQL("CREATE TABLE IF NOT EXISTS VideoPreset(Vnum VARCHAR, VPreset1 VARCHAR, VPreset2 VARCHAR, VPreset3 VARCHAR, VPreset4 VARCHAR, VPreset5 VARCHAR, VPreset6 VARCHAR);");
		db.execSQL("INSERT INTO VideoPreset VALUES('1','"+Preset[1]+"','"+Preset[2]+"','"+Preset[3]+"','"+Preset[4]+"','"+Preset[5]+"','"+Preset[6]+"')");
		db.close();
	}
	
	public String lib(int index) {
		String result="";
		String[] PresetItem = Preset[index].split(",");
		result="<b>"+PresetItem[0]+" / "+PresetItem[1]+" / "+PresetItem[2]+"</b>";
		if (PresetItem[3].equals("1")) { result=result+"<br><small>PT ("+PresetItem[4]+")</small>"; } else { result=result+"<br><small>PT Off</small>"; }
		if (PresetItem[5].equals("1")) { result=result+"<small> / SM On</small>"; } else { result=result+"<small> / SM Off</small>"; }
		if (PresetItem[6].equals("1")) { result=result+"<small> / Low On</small>"; } else { result=result+"<small> / Low Off</small>"; }
		return result;
	}
	
	@Override
	protected void onDestroy() {
		onSave();
		super.onDestroy();
		overridePendingTransition(R.anim.slideup_in, R.anim.slideup_out);
	}
}
