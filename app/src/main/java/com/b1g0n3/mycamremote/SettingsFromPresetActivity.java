package com.b1g0n3.mycamremote;

import org.gopro.core.GoProHelper;
import org.gopro.core.model.BacPacStatus;
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
import android.widget.Toast;

public class SettingsFromPresetActivity extends Activity {
	
	int GoproModel = 0,GoproIndex=0;
	String GoproMode = "0", GoproProduct="";
	int a_fps=0, a_Fov=0, a_VideoRes=0, a_ProtuneVideoEnabled = 0, a_ProtuneVideoSetting = 0, a_VideoExposure = 0;
	public Button ButtonPreset1,ButtonPreset2,ButtonPreset3,ButtonPreset4,ButtonPreset5,ButtonPreset6;
	String[] Preset = {"0,0,0,0,0,0,0","0,0,0,0,0,0,0","0,0,0,0,0,0,0","0,0,0,0,0,0,0","0,0,0,0,0,0,0","0,0,0,0,0,0,0","0,0,0,0,0,0,0"};
	String[] libPreset = {"Empty","Empty","Empty","Empty","Empty","Empty","Empty"};
	String libPVS[] = {"Auto","3000K","5500K","6500K","RAW"};
	String libFov[] = {"W","M","N"};
	String libVidres[] = {"WVGA","720","960","1080","1440","2.7K","4K","2.7K Cin","4K Cin","1080 S","720 S","XX","XX","XX","XX","XX","XX"};
	String libVidres4[] = {"","4K","4K Cin","","2.7K","2.7K Cin","2.7K 4:3","1440","1080 Sup","1080","960","720 Sup","720","WVGA","XX","XX","XX","XX","XX","XX"};
	String libFps[]={"12","15","24","25","30","48","50","60","100","120","240","XX","XX","XX","XX","XX","XX" };
	String libFps4[]={"240","120","100","90","80","60","50","48","30","25","24","XX","XX","XX","XX","XX","XX" };
	String[] PresetItem;
	public String GoproPassword;
	String[] BssidGopro = {"D4:D9:19","D8:96:85","F4:DD:9E","D6:D9:19","F6:DD:9E"};
	String[] BssidApi = {"3","3","4","4","4"};
	String[] BssidModel = {"Gopro3","GoPro3","Gopro4Silver","GoPro4Black","Gopro4s"};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settingsfrompreset);
		ButtonPreset1 = (Button) findViewById(R.id.button1);
		ButtonPreset2 = (Button) findViewById(R.id.button2);
		ButtonPreset3 = (Button) findViewById(R.id.button3);
		ButtonPreset4 = (Button) findViewById(R.id.button4);
		ButtonPreset5 = (Button) findViewById(R.id.button5);
		ButtonPreset6 = (Button) findViewById(R.id.button6);
		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
			GoproModel = Integer.valueOf(extras.getString("GoproModel"));
			GoproIndex = Integer.valueOf(extras.getString("GoproIndex"));
		    GoproPassword =  extras.getString("GoproPassword");
		}
		GoproProduct=BssidModel[GoproIndex];

		SQLiteDatabase db;
		db=openOrCreateDatabase("MygoproRemote2", Context.MODE_PRIVATE,null);
		db.execSQL("CREATE TABLE IF NOT EXISTS VideoPreset(Vnum VARCHAR, VVidres1 VARCHAR, VFps1 VARCHAR, VFov1 VARCHAR, VVidres2 VARCHAR, VFps2 VARCHAR, VFov2 VARCHAR, VVidres3 VARCHAR, VFps3 VARCHAR, VFov3 VARCHAR);");
		Cursor c=db.rawQuery("SELECT * FROM VideoPreset",null );
		if (c.moveToFirst()) {
			Preset[1]=c.getString(1); Preset[2]=c.getString(2); Preset[3]=c.getString(3); Preset[4]=c.getString(4); Preset[5]=c.getString(5); Preset[6]=c.getString(6);
			if (Preset[1].equals("0,0,0,0,0,0,0")) { libPreset[1]="Empty"; } else { libPreset[1]=lib(1); }
			if (Preset[2].equals("0,0,0,0,0,0,0")) { libPreset[2]="Empty"; } else { libPreset[2]=lib(2); }
			if (Preset[3].equals("0,0,0,0,0,0,0")) { libPreset[3]="Empty"; } else { libPreset[3]=lib(3); }
			if (Preset[4].equals("0,0,0,0,0,0,0")) { libPreset[4]="Empty"; } else { libPreset[4]=lib(4); }
			if (Preset[5].equals("0,0,0,0,0,0,0")) { libPreset[5]="Empty"; } else { libPreset[5]=lib(5); }
			if (Preset[6].equals("0,0,0,0,0,0,0")) { libPreset[6]="Empty"; } else { libPreset[6]=lib(6); }
		} else {
			libPreset[1]="Empty<br>"; libPreset[2]="Empty"; libPreset[3]="Empty";
			libPreset[4]="Empty<br>"; libPreset[5]="Empty"; libPreset[6]="Empty";
		}
		c.close(); db.close();
	
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
		super.onDestroy();
		overridePendingTransition(R.anim.slideup_in, R.anim.slideup_out);
	}

	public int indexOf(String[] a, String s) {
		for (int i = 0; i < a.length; i++)
		{
		if (a[i].equals(s)) { return i; }
		}
		return -1;
	}
	
	protected void onPush(Integer preset) {
		PresetItem = Preset[preset].split(",");
		boolean err=false;
		GoProApi gopro = new GoProApi(GoproPassword);
		GoProHelper helper = gopro.getHelper();
		try {
			BacPacStatus bacpacStatus = helper.getBacpacStatus();

			if (bacpacStatus.isCameraPowerOn()) {						
//				try {
					if (GoproModel==3) {
						Log.v("SettingsFromPreset", "CamProtune");
						try { gopro.getHelper().setCamProtune(Integer.valueOf(PresetItem[3])); } catch (Exception e1) { err=true; }
						Log.v("SettingsFromPreset", "CamWriteBalance");
						try { gopro.getHelper().setCamWriteBalance(indexOf(libPVS, PresetItem[4])); } catch (Exception e1) { err=true; }
						Log.v("SettingsFromPreset", "CamExposure");
						try { gopro.getHelper().setCamExposure(Integer.valueOf(PresetItem[5])); } catch (Exception e1) { err=true; }
						Log.v("SettingsFromPreset", "CamVideoResolution");
						try { gopro.getHelper().setCamVideoResolution(indexOf(libVidres,PresetItem[0])); } catch (Exception e1) { err=true; }
						Log.v("SettingsFromPreset", "CamFps");
						try { gopro.getHelper().setCamFps(indexOf(libFps, PresetItem[1])); } catch (Exception e1) { err=true; }
						Log.v("SettingsFromPreset", "CamFov");
						try { gopro.getHelper().setCamFov(indexOf(libFov,PresetItem[2]));  } catch (Exception e1) { err=true; }
					} else {
						try { gopro.getHelper().setLowLight4(Integer.valueOf(PresetItem[6])); } catch (Exception e1) { err=true; }
						try { gopro.getHelper().setCamWriteBalance4(indexOf(libPVS,PresetItem[4])); } catch (Exception e1) { err=true; }
						try { gopro.getHelper().setCamProtune4(Integer.valueOf(PresetItem[3]));  } catch (Exception e1) { err=true; }
						try { gopro.getHelper().setCamExposure4(Integer.valueOf(PresetItem[5])); } catch (Exception e1) { err=true; }
						try { gopro.getHelper().setCamVideoResolution4(indexOf(libVidres4,PresetItem[0])); } catch (Exception e1) { err=true; }
						try { gopro.getHelper().setCamFps4(indexOf(libFps4,PresetItem[1])); } catch (Exception e1) { err=true; }
						try { gopro.getHelper().setCamFov4(indexOf(libFov,PresetItem[2]));  } catch (Exception e1) { err=true; }
					}
			}
		} catch (Exception e1) { e1.printStackTrace(); }

		if (err) Toast.makeText(getApplicationContext(), "Unsupported settings sent to your Gopro",Toast.LENGTH_LONG).show(); 
		else Toast.makeText(getApplicationContext(), "Preset sent successfully",Toast.LENGTH_LONG).show();
		this.finish();
	}

}
