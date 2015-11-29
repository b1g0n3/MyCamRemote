package com.b1g0n3.mycamremote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.gopro.core.GoProHelper;
import org.gopro.core.model.BacPacStatus;
import org.gopro.core.model.CamFields;
import org.gopro.main.GoProApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import com.b1g0n3.tools;

public class PreviewActivityXiaomi extends Activity {
	VideoView videoview;
	public String GoproPassword;
	public int currentshutter=0,actual_model=0,actual_mode=0;
	private static String URL3 = "http://192.168.42.1:7878";
	String[] BssidGopro = {"D4:D9:19","D8:96:85","F4:DD:9E","D6:D9:19","F6:DD:9E"};
	String[] BssidApi = {"3","3","4","4","4"};
	String[] BssidModel = {"Gopro3","GoPro3","Gopro4Silver","GoPro4Black","Gopro4s"};
	String ip = "192.168.42.1";
	String libFov[] = {"W","M","N"};
	String libPhotoRes[] = {"11MP","8MP","5MP","5MP","7MP","12MP","7MP","XX","XX","XX","XX","XX","X"};
	String libPhotoRes4[] = {"12MP | W","7MP | W","7MP | M","5MP | M","XX","XX","XX","XX","XX","XX"};
	String libPhotoRes4s[] = {"8MP | W","7MP | W","7MP | M","5MP | M","XX","XX","XX","XX","XX","XX"};
	String libPhotoAngle[] = {"W","M","W","M","W","W","M","XX","XX","XX","XX","XX","XX"};
	String libBurstRate[] = {"3/1s","5/1s","10/1s","10/2s","30/1s","30/2s","30/3s","XX","XX","XX","XX","XX","XX"};
	String libContinousPhotoRate[] = {"3/1s","5/1s","10/1s","10/2s","30/1s","30/2s","30/3s","XX","XX","XX","XX","XX","XX"};
	String libBurstRate4[] = {"3/1s","5/1s","10/1s","10/2s","10/3s","30/1s","30/2s","30/3s","30/6s","XX","XX","XX","XX","XX"};
	String libVidres[] = {"WVGA","720","960","1080","1440","2.7K","4K","2.7K Cin","4K Cin","1080 S","720 S","XX","XX","XX","XX","XX","XX"};
	String libTimelapseInterval[] = {"0.5s","1s","2s","5s","10s","30s","60s","","","",""};
	String libPhotoShutter[] = {"Auto","2s","5s","10s","15s","20s","30s","","","","",""};
	String libVideoPhotoInterval[] = {"","5s","10s","30s","60s","","","",""};
	String libVidres4[] = {"","4K","4K Sup","","2.7K","2.7K Sup","2.7K 4:3","1440","1080 Sup","1080","960","720 Sup","720","WVGA","XX","XX","XX","XX","XX","XX"};
	String libFps[]={"12","15","24","25","30","48","50","60","100","120","240","XX","XX","XX","XX","XX","XX" };
	String libFps4[]={"240","120","100","90","80","60","50","48","30","25","24","XX","XX","XX","XX","XX","XX" };
	String libNightlapseInterval[]={"Cont","","","","4s","5s","10s","15s","20s","30s","1mn","2mn","5mn","30mn","60mn","","",""};
	String libNightlapseShutter[]={"Auto","2s","5s","10s","15s","20s","30s","","","","",""};
	int currentVideoExposure,currentProtuneVideoSetting,currentProtuneVideoEnabled,currentProtunePhotoEnabled,currentProtunePhotoSetting;
	int currentPhotoExposure,currentProtuneBurstEnabled,currentProtuneBurstSetting,currentBurstExposure,currentBurstRate,currentTimelapse ;
	int currentBurstRes,currentPhotoRes,currentVideoRes,currentAngle,currentfps,currentMode, currentSubMode,currentUpDown, currentTimelapseVideoInterval;
	int currentLowLight,currentPhotoVideoInterval,currentContinousPhotoRate,currentPhotoShutter,currentNightlapseInterval,currentNightlapseShutter,currentNoSDCard;
	public int GoproModel,GoproIndex,lastmode;
	Handler mHandler;
	Timer timer,timer2;
	WifiManager mainWifi;
	String GoproProduct, bssid;
	public int refresh_status = 3;
	boolean PreviewOn = false, PowerOn = false, first=true;
	private TextView textStatusCamera, textDuree, RecordFormat;
	private ImageView imageMode, imageWifi, imageUpDown, imageBatterie, imageRecording,imageExpo,imageBalance,imageProtune,imageLowLight;
	private RelativeLayout StatusHaut, StatusBas;
	private CamFields camFields;
	private Resources res;
	public int timeout = 3, busy_error, retry_after_off=0;
	Drawable drawwifi,drawupdown,drawPower,drawExpo,drawProtune,drawBalance,drawRecording,drawLowLight;
	int wifiLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		tools.out("PreviewActivityXiaomi onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);
		textStatusCamera = (TextView) findViewById(R.id.textStatusCamera);
		textDuree = (TextView) findViewById(R.id.textDuree);
		RecordFormat = (TextView) findViewById(R.id.RecordFormat);
		imageMode = (ImageView) findViewById(R.id.imageMode);
		imageWifi = (ImageView) findViewById(R.id.imageWifi);
		imageUpDown = (ImageView) findViewById(R.id.imageUpDown);
		imageBatterie = (ImageView) findViewById(R.id.imageBatterie);
		imageRecording = (ImageView) findViewById(R.id.imageRecord);
		imageLowLight = (ImageView) findViewById(R.id.imageLowLight);
		StatusHaut = (RelativeLayout) findViewById(R.id.StatusHaut);
		StatusBas = (RelativeLayout) findViewById(R.id.StatusBas);
		imageExpo = (ImageView) findViewById(R.id.imageExpo);
		imageBalance = (ImageView) findViewById(R.id.imageBalance);
		imageProtune = (ImageView) findViewById(R.id.imageProtune);
	}

	@Override
	protected void onDestroy() {
		tools.out("PreviewActivityXiaomi onDestroy()");
		timer.cancel(); timer2.cancel();
		super.onDestroy();
		overridePendingTransition(R.anim.slideup_in, R.anim.slideup_out);
	}

	@Override
	public void onResume()
	{
		tools.out("PreviewActivityXiaomi onResume()");
		super.onResume();
		timer = new Timer();
		timer2 = new Timer();
		tools.out("PreviewActivityXiaomi onResume() OnlineTimerTask on");
		TimerTask OnlineProfile = new OnlineTimerTask(PreviewActivityXiaomi.this);
		timer2.scheduleAtFixedRate(OnlineProfile, 0, 1*1000);
		tools.out("PreviewActivityXiaomi onResume() CustomTimerTask on");
		TimerTask updateProfile = new CustomTimerTask(PreviewActivityXiaomi.this);
		timer.scheduleAtFixedRate(updateProfile, 0, refresh_status*1000);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_CENTER :
			{
				GoProApi gopro = new GoProApi(GoproPassword);
				if (actual_mode==0) {
					if (currentshutter==0) {
						try {
							gopro.getHelper().startRecord();
							currentshutter=1;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
//							System.out.println("Error in /Cam Record On/");
						}
					} else {
						try {
							gopro.getHelper().stopRecord();
							currentshutter=0;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
//							System.out.println("Error in /Cam Record Off/");
						}
					}
				} else {
					try {
//		        		System.out.println("Photo Record On");
						gopro.getHelper().startRecord();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
//						System.out.println("Error in /Photo Record On/");
					}

				}
				return true;
			}
			case KeyEvent.KEYCODE_DPAD_LEFT :
			{
				GoProApi gopro = new GoProApi(GoproPassword);
				GoProHelper helper = gopro.getHelper();
				try {
					BacPacStatus bacpacStatus = helper.getBacpacStatus();
					if (bacpacStatus.isCameraPowerOn()) {
						try {
							switch (lastmode) {
								case 0 : gopro.getHelper().modePhoto();
									break;
								case 1 : gopro.getHelper().modeBurst();
									break;
								case 2 : if (GoproModel==3) { gopro.getHelper().timelapse1(); } else { gopro.getHelper().modeCamera(); }
									break;
								default: gopro.getHelper().modeCamera();
									break;
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("exception1");
						}
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return true;
			}
			case KeyEvent.KEYCODE_DPAD_RIGHT :
			{
				GoProApi gopro = new GoProApi(GoproPassword);
				GoProHelper helper = gopro.getHelper();
				try {
					BacPacStatus bacpacStatus = helper.getBacpacStatus();
					if (bacpacStatus.isCameraPowerOn()) {
						try {
							switch (lastmode) {
								case 2 : gopro.getHelper().modePhoto();
									break;
								case 1 : gopro.getHelper().modeCamera();
									break;
								case 0 : if (GoproModel==3) { gopro.getHelper().timelapse1(); } else { gopro.getHelper().modeBurst(); }
									break;
								default: gopro.getHelper().modeBurst();
									break;
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("exception1");
						}
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return true;
			}

			case KeyEvent.KEYCODE_DPAD_UP :
			{
				if (isOnline() && lastmode==0 ) {
					Intent i=new Intent(this,SettingsFromPresetActivity.class);
					i.putExtra("GoproModel", String.valueOf(GoproModel));
//					i.putExtra("GoproMode", String.valueOf(lastmode));
					i.putExtra("GoproIndex", String.valueOf(GoproIndex) );
					i.putExtra("GoproPassword", String.valueOf(GoproPassword));
					timer.cancel(); timer2.cancel();
					this.startActivity(i);
					overridePendingTransition(R.anim.slidedown_in, R.anim.slidedown_out);
				}
				return true;
			}
			case KeyEvent.KEYCODE_DPAD_DOWN :
			{
				if (isOnline() && lastmode==0 ) {
					Intent i=new Intent(this,SettingsToPresetActivity.class);
					i.putExtra("GoproModel", String.valueOf(GoproModel));
					i.putExtra("GoproMode", String.valueOf(lastmode));
					i.putExtra("GoproPassword", String.valueOf(GoproPassword));
					i.putExtra("LowLightEnabled", Integer.valueOf(currentLowLight));
					i.putExtra("ProtuneVideoEnabled", Integer.valueOf(currentProtuneVideoEnabled));
					i.putExtra("ProtuneVideoSetting", Integer.valueOf(currentProtuneVideoSetting));
					i.putExtra("VideoExposure", Integer.valueOf(currentVideoExposure));
					i.putExtra("fps", Integer.valueOf(currentfps));
					i.putExtra("VideoRes", Integer.valueOf(currentVideoRes));
					i.putExtra("Angle", Integer.valueOf(currentAngle));
					timer.cancel(); timer2.cancel();
					this.startActivity(i);
					overridePendingTransition(R.anim.slidedown_in, R.anim.slidedown_out);
				}
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	class OnlineTimerTask extends TimerTask {
		private Context context;
		private Handler mHandler = new Handler();
		public OnlineTimerTask(Context con) {
			this.context = con;
		}

		@Override
		public void run() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							tools.out("PreviewActivityXiaomi OnlineTimerTask");
							PowerOn=isOnline();
							tools.out("PreviewActivityXiaomi OnlineTimerTask Online("+PowerOn+")");
						}
					});
				}
			}).start();
		}
	}


	//
	//  BOUCLE DE STATUS
	//
	//   
	class CustomTimerTask extends TimerTask {
		private Context context;
		private Handler mHandler = new Handler();
		public CustomTimerTask(Context con) {
			this.context = con;
		}

//	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							tools.out("PreviewActivityXiaomi CustomTimerTask");
							//
							// Boucle de status
							//
/*							tools.out("PreviewActivityXiaomi CustomTimerTask - affectation1");
//							drawwifi = res.getDrawable(R.drawable.iconwifi0);
							tools.out("PreviewActivityXiaomi CustomTimerTask - affectation2");
//							drawupdown = res.getDrawable(R.drawable.updown);
							tools.out("PreviewActivityXiaomi CustomTimerTask - affectation3");
//							drawPower = res.getDrawable(R.drawable.iconepower4);
							tools.out("PreviewActivityXiaomi CustomTimerTask - affectation4");
//							drawExpo = res.getDrawable(R.drawable.exposure);
							tools.out("PreviewActivityXiaomi CustomTimerTask - affectation5");
//							drawProtune = res.getDrawable(R.drawable.protunelogo1);
							tools.out("PreviewActivityXiaomi CustomTimerTask - affectation6");
//							drawBalance = res.getDrawable(R.drawable.whitebalance);
							tools.out("PreviewActivityXiaomi CustomTimerTask - affectation7");
//							drawRecording = res.getDrawable(R.drawable.recording);
							tools.out("PreviewActivityXiaomi CustomTimerTask - affectation8");
//							drawLowLight = res.getDrawable(R.drawable.lowlight);
*/
							tools.out("PreviewActivityXiaomi CustomTimerTask - setImageDrawable");

							imageUpDown.setImageDrawable(drawupdown);
							imageExpo.setImageDrawable(drawExpo);
							imageProtune.setImageDrawable(drawProtune);
							imageBalance.setImageDrawable(drawBalance);
							imageLowLight.setImageDrawable(drawLowLight);
							imageRecording.setImageDrawable(drawRecording);

							tools.out("PreviewActivityXiaomi CustomTimerTask - setVisibility");

							imageExpo.setVisibility(View.INVISIBLE);
							imageBalance.setVisibility(View.INVISIBLE);
							imageExpo.setVisibility(View.INVISIBLE);
							imageProtune.setVisibility(View.INVISIBLE);
							imageBalance.setVisibility(View.INVISIBLE);
							imageLowLight.setVisibility(View.INVISIBLE);
							StatusHaut(false); StatusBas(false);
							imageRecording.setVisibility(View.INVISIBLE);

							if (PowerOn) {
								tools.out("PreviewActivityXiaomi : PowerOn=" + PowerOn);
								try { refresh_status(); } catch (Exception e) { e.printStackTrace(); }
							} else {
								tools.out("PreviewActivityXiaomi : PowerOn=false");
								textStatusCamera.setText("CAMERA OFF");
								try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
								StatusHaut(false); StatusBas(false);
								textStatusCamera.setText("CAMERA OFF");
							}
//							if (PowerOn) { PlayVideo(); }
						}
					});
				}
			}).start();
		}
	}

	public void refresh_status() throws Exception
	{
		tools.out("PreviewActivityXiaomi refresh-status----------------------------------------------");
		GoProApi gopro = new GoProApi(GoproPassword);
		GoProHelper helper = gopro.getHelper();
		StatusHaut(true); StatusBas(true);
		camFields = helper.getCameraSettings();
		Date df;
		long FreePhotos, FreeVideos, currentNbrePhotos, currentNbreVideos,RecordingSec,RecordingMin,dv;
		String libRecording,FreeTime, libTimelapse;
		if (GoproModel==3) {
			BacPacStatus bacpacStatus;
			bacpacStatus = helper.getBacpacStatus();
			wifiLevel = bacpacStatus.getRSSI();
			camFields = helper.getCameraSettingsExtended();
		}
		if (GoproModel==4) {
			camFields = helper.getCameraSettingsExtended4();
			wifiLevel= camFields.getWifiBar();
		}
		currentMode = camFields.getMode();
		currentSubMode = camFields.getSubMode();
		if (camFields.getLowLight()) { currentLowLight = 1; } else { currentLowLight = 0; }
		imageLowLight.setVisibility(View.INVISIBLE);
		currentUpDown = camFields.getUpdown();
		if (currentUpDown==0) {
			drawupdown = res.getDrawable(R.drawable.updown0);
			imageUpDown.setImageDrawable(drawupdown);
			imageUpDown.setVisibility(View.VISIBLE);
		} else {
			if (currentUpDown==2) {
				drawupdown = res.getDrawable(R.drawable.updown2);
				imageUpDown.setImageDrawable(drawupdown);
				imageUpDown.setVisibility(View.VISIBLE);
			} else {
				imageUpDown.setVisibility(View.INVISIBLE);
			}
		}
		currentshutter = camFields.getShutter();
		textStatusCamera.setText("");
		lastmode=currentMode;
		currentProtuneVideoEnabled=camFields.getProtuneVideoEnabled();
		if (currentProtuneVideoEnabled==6) { currentProtuneVideoEnabled=1; }
		if (currentProtuneVideoEnabled==4) { currentProtuneVideoEnabled=0; }
		currentProtuneVideoSetting=camFields.getProtuneVideoSetting();
		currentVideoExposure = camFields.getVideoExposure();
		currentfps = camFields.getFramesPerSecond();
		currentVideoRes =camFields.getVidres();
		currentNoSDCard = camFields.getNoSDCard();
		currentAngle = camFields.getFieldOfView();
		switch (currentMode) {
			case 0 :  	switch (currentSubMode) {
				case 0 :
					imageMode.setBackgroundResource(R.drawable.mode1);
					FreeVideos = camFields.getVideoAvailable();
					RecordingSec = camFields.getPlaybackSec();
					RecordingMin = camFields.getPlaybackMin();
					currentNbreVideos = camFields.getVideoOncard();
					if (currentLowLight==1) { imageLowLight.setVisibility(View.VISIBLE); } else { imageLowLight.setVisibility(View.INVISIBLE);  }
					if (currentVideoExposure==1) { imageExpo.setVisibility(View.VISIBLE); } else { imageExpo.setVisibility(View.INVISIBLE);  }
					if (currentshutter==0) { imageRecording.setVisibility(View.INVISIBLE); } else { imageRecording.setVisibility(View.VISIBLE); }
					if (currentProtuneVideoEnabled==1) { imageProtune.setVisibility(View.VISIBLE);
						if (currentProtuneVideoSetting>0) { imageBalance.setVisibility(View.VISIBLE); } else { imageBalance.setVisibility(View.INVISIBLE);  };
					} else {
						imageProtune.setVisibility(View.INVISIBLE); imageBalance.setVisibility(View.INVISIBLE);
					};
					if (GoproModel==3) {
						long hours, minutes;
						if (FreeVideos!=65535) {
							hours = FreeVideos / 60; //since both are ints, you get an int
							minutes = FreeVideos % 60;
						} else { hours = 0; minutes = 0; }
						FreeTime = String.format("%d H %02d", hours, minutes);
						RecordFormat.setText(libVidres[currentVideoRes]+" | "+libFps[currentfps]+" | "+libFov[currentAngle]);
						if (RecordingMin<10) { libRecording="0"+RecordingMin+":"; } else { libRecording=RecordingMin+":"; }
						if (RecordingSec<10) { libRecording+="0"+RecordingSec; } else { libRecording+=RecordingSec; }
					} else {
						FreeTime=MyTimeFormat(Long.valueOf(FreeVideos)*1000,"H'H'mm");
						libRecording=MyTimeFormat(Long.valueOf(RecordingSec) * 1000, "H:mm:ss");
//						Log.v("PreviewActivity", "-------------- FreeTime="+FreeTime);
						RecordFormat.setText(libVidres4[currentVideoRes]+" | "+libFps4[currentfps]+" | "+libFov[currentAngle]);
					}
					if (currentNoSDCard ==2) { textDuree.setText("No SD Card"); } else if (currentNoSDCard == 3) { textDuree.setText("SD Error"); } else { textDuree.setText(currentNbreVideos+" | "+libRecording+" | "+FreeTime); };
					break;
				case 1 :
					imageMode.setBackgroundResource(R.drawable.mode15);
					imageLowLight.setVisibility(View.INVISIBLE); imageBalance.setVisibility(View.INVISIBLE); imageProtune.setVisibility(View.INVISIBLE); imageExpo.setVisibility(View.INVISIBLE);
					currentTimelapseVideoInterval = camFields.getTimelapseVideoInterval();
					FreeVideos = camFields.getVideoAvailable();
					currentfps = camFields.getFramesPerSecond();
					currentVideoRes =camFields.getVidres();
					RecordingSec = camFields.getPlaybackSec();
					RecordingMin = camFields.getPlaybackMin();
					currentNbreVideos = camFields.getVideoOncard();
					currentAngle = camFields.getFieldOfView();
					if (currentshutter==0) { imageRecording.setVisibility(View.INVISIBLE); } else { imageRecording.setVisibility(View.VISIBLE); }
					FreeTime=MyTimeFormat(Long.valueOf(FreeVideos)*1000,"H'H'mm");
					libRecording=MyTimeFormat(Long.valueOf(RecordingSec) * 1000, "H:mm:ss");
					RecordFormat.setText(libVidres4[currentVideoRes]+" | "+libTimelapseInterval[currentTimelapseVideoInterval]);
					if (currentNoSDCard ==2) { textDuree.setText("No SD Card"); } else if (currentNoSDCard == 3) { textDuree.setText("SD Error"); } else { textDuree.setText(currentNbreVideos+" | "+libRecording+" | "+FreeTime); };
					break;
				case 2 :
					imageMode.setBackgroundResource(R.drawable.mode10b);
					imageLowLight.setVisibility(View.INVISIBLE); imageBalance.setVisibility(View.INVISIBLE); imageProtune.setVisibility(View.INVISIBLE); imageExpo.setVisibility(View.INVISIBLE);
					currentVideoExposure = camFields.getVideoExposure();
					currentPhotoVideoInterval = camFields.getVideoPhotoInterval();
					FreeVideos = camFields.getVideoAvailable();
					currentfps = camFields.getFramesPerSecond();
					currentVideoRes =camFields.getVidres();
					RecordingSec = camFields.getPlaybackSec();
					RecordingMin = camFields.getPlaybackMin();
					currentNbreVideos = camFields.getVideoOncard();
					currentAngle = camFields.getFieldOfView();
					if (currentVideoExposure==1) { imageExpo.setVisibility(View.VISIBLE); } else { imageExpo.setVisibility(View.INVISIBLE);  }
					if (currentshutter==0) { imageRecording.setVisibility(View.INVISIBLE); } else { imageRecording.setVisibility(View.VISIBLE); }
					FreeTime=MyTimeFormat(Long.valueOf(FreeVideos)*1000,"H'H'mm");
					libRecording=MyTimeFormat(Long.valueOf(RecordingSec) * 1000, "H:mm:ss");
					RecordFormat.setText(libVidres4[currentVideoRes]+" | "+libFps4[currentfps]+" | "+libFov[currentAngle]+" | "+libVideoPhotoInterval[currentPhotoVideoInterval]);
					if (currentNoSDCard ==2) { textDuree.setText("No SD Card"); } else if (currentNoSDCard == 3) { textDuree.setText("SD Error"); } else { textDuree.setText(currentNbreVideos+" | "+libRecording+" | "+FreeTime); }
					break;
			}
				break;
			case 1 :  	switch (currentSubMode) {
				case 0 :
					imageMode.setBackgroundResource(R.drawable.mode2);
					currentProtunePhotoEnabled=camFields.getProtunePhotoEnabled();
					currentProtunePhotoSetting=camFields.getProtunePhotoSetting();
					currentNbrePhotos = camFields.getPhotosOncard();
					currentPhotoRes = camFields.getPhotoResolution();
					FreePhotos = camFields.getPhotosAvailable();
					if (currentshutter==0) { imageRecording.setVisibility(View.INVISIBLE); } else { imageRecording.setVisibility(View.VISIBLE); }
					if (currentNoSDCard ==2) { textDuree.setText("No SD Card"); } else if (currentNoSDCard == 3) { textDuree.setText("SD Error"); } else { textDuree.setText(currentNbrePhotos + " | " + String.valueOf(FreePhotos)); }
					if (GoproModel==3) {
						currentVideoExposure = camFields.getVideoExposure();
						if (currentVideoExposure==1) { imageExpo.setVisibility(View.VISIBLE); } else { imageExpo.setVisibility(View.INVISIBLE);  }
						imageProtune.setVisibility(View.INVISIBLE);
						imageBalance.setVisibility(View.INVISIBLE);
						RecordFormat.setText(libPhotoRes[currentPhotoRes]+" | "+libPhotoAngle[currentPhotoRes]);
					} else {
						currentPhotoExposure = camFields.getPhotoExposure();
						if (currentPhotoExposure==1) { imageExpo.setVisibility(View.VISIBLE); } else { imageExpo.setVisibility(View.INVISIBLE);  }
						if (GoproProduct.equals("Gopro4s")) { RecordFormat.setText(libPhotoRes4s[currentPhotoRes]); } else { RecordFormat.setText(libPhotoRes4[currentPhotoRes]); }
						if (currentProtunePhotoEnabled==1) {
							imageProtune.setVisibility(View.VISIBLE);
							if (currentProtunePhotoSetting>0) { imageBalance.setVisibility(View.VISIBLE); } else { imageBalance.setVisibility(View.INVISIBLE);  };
						} else {
							imageProtune.setVisibility(View.INVISIBLE); imageBalance.setVisibility(View.INVISIBLE);
						};
					}
					break;
				case 1 :
					imageMode.setBackgroundResource(R.drawable.mode12);
					currentProtunePhotoEnabled=camFields.getProtunePhotoEnabled();
					currentProtunePhotoSetting=camFields.getProtunePhotoSetting();
					currentNbrePhotos = camFields.getPhotosOncard();
					currentPhotoRes = camFields.getPhotoResolution();
					FreePhotos = camFields.getPhotosAvailable();
					if (currentshutter==0) { imageRecording.setVisibility(View.INVISIBLE); } else { imageRecording.setVisibility(View.VISIBLE); }
					if (currentNoSDCard ==2) { textDuree.setText("No SD Card"); } else if (currentNoSDCard == 3) { textDuree.setText("SD Error"); } else { textDuree.setText(currentNbrePhotos + " | " + String.valueOf(FreePhotos)); }
					currentPhotoExposure = camFields.getPhotoExposure();
					currentContinousPhotoRate = camFields.getContinousPhotoRate();
					if (currentPhotoExposure==1) { imageExpo.setVisibility(View.VISIBLE); } else { imageExpo.setVisibility(View.INVISIBLE);  }
					if (GoproProduct.equals("Gopro4s")) { RecordFormat.setText(libPhotoRes4s[currentPhotoRes]); } else { RecordFormat.setText(libPhotoRes4[currentPhotoRes]); }
					RecordFormat.append(" | "+libContinousPhotoRate[currentContinousPhotoRate]);
					if (currentProtunePhotoEnabled==1) {
						imageProtune.setVisibility(View.VISIBLE);
						if (currentProtunePhotoSetting>0) { imageBalance.setVisibility(View.VISIBLE); } else { imageBalance.setVisibility(View.INVISIBLE);  };
					} else {
						imageProtune.setVisibility(View.INVISIBLE); imageBalance.setVisibility(View.INVISIBLE);
					};
					break;
				case 2 :
					imageMode.setBackgroundResource(R.drawable.mode13);
					currentProtunePhotoEnabled=camFields.getProtunePhotoEnabled();
					currentProtunePhotoSetting=camFields.getProtunePhotoSetting();
					currentPhotoShutter = camFields.getPhotoShutter();
					currentNbrePhotos = camFields.getPhotosOncard();
					currentPhotoRes = camFields.getPhotoResolution();
					FreePhotos = camFields.getPhotosAvailable();
					if (currentshutter==0) { imageRecording.setVisibility(View.INVISIBLE); } else { imageRecording.setVisibility(View.VISIBLE); }
					if (currentNoSDCard ==2) { textDuree.setText("No SD Card"); } else if (currentNoSDCard == 3) { textDuree.setText("SD Error"); } else { textDuree.setText(currentNbrePhotos + " | " + String.valueOf(FreePhotos)); }
					currentPhotoExposure = camFields.getPhotoExposure();
					if (currentPhotoExposure==1) { imageExpo.setVisibility(View.VISIBLE); } else { imageExpo.setVisibility(View.INVISIBLE);  }
					if (GoproProduct.equals("Gopro4s")) { RecordFormat.setText(libPhotoRes4s[currentPhotoRes]);
					} else {
						RecordFormat.setText(libPhotoRes4[currentPhotoRes]); }
					RecordFormat.append(" | "+libPhotoShutter[currentPhotoShutter]);
					if (currentProtunePhotoEnabled==1) {
						imageProtune.setVisibility(View.VISIBLE);
						if (currentProtunePhotoSetting>0) { imageBalance.setVisibility(View.VISIBLE); } else { imageBalance.setVisibility(View.INVISIBLE);  };
					} else {
						imageProtune.setVisibility(View.INVISIBLE); imageBalance.setVisibility(View.INVISIBLE);
					};
					break;

			}
				break;
			case 2 :  	switch (currentSubMode) {
				case 0 :
					imageMode.setBackgroundResource(R.drawable.mode3);
					currentProtuneBurstEnabled=camFields.getProtuneBurstEnabled();
					currentProtuneBurstSetting=camFields.getProtuneBurstSetting();
					currentBurstRate = camFields.getBurstRate();
					currentNbrePhotos = camFields.getPhotosOncard();
					FreePhotos = camFields.getPhotosAvailable();
					currentBurstExposure = camFields.getBurstExposure();
					if (currentBurstExposure==1) { imageExpo.setVisibility(View.VISIBLE); } else { imageExpo.setVisibility(View.INVISIBLE);  }
					if (currentshutter==0) { imageRecording.setVisibility(View.INVISIBLE); } else { imageRecording.setVisibility(View.VISIBLE); }
					imageExpo.setVisibility(View.INVISIBLE);
					imageProtune.setVisibility(View.INVISIBLE);
					imageBalance.setVisibility(View.INVISIBLE);
					if (currentshutter==0) { imageRecording.setVisibility(View.INVISIBLE); } else { imageRecording.setVisibility(View.VISIBLE); }
					if (currentNoSDCard ==2) { textDuree.setText("No SD Card"); } else if (currentNoSDCard == 3) { textDuree.setText("SD Error"); } else { textDuree.setText(currentNbrePhotos + " | " + String.valueOf(FreePhotos)); }
					if (GoproModel==3) {
						currentBurstRes =camFields.getPhotoResolution();
						currentBurstExposure = camFields.getVideoExposure();
						if (currentBurstExposure==1) { imageExpo.setVisibility(View.VISIBLE); } else { imageExpo.setVisibility(View.INVISIBLE);  }
						RecordFormat.setText(libPhotoRes[currentBurstRes]+" | "+libPhotoAngle[currentBurstRes]+" | "+libBurstRate[currentBurstRate]);
					} else {
						currentBurstRes =camFields.getBurstResolution();
						currentBurstExposure = camFields.getBurstExposure();
						if (currentBurstExposure==1) { imageExpo.setVisibility(View.VISIBLE); } else { imageExpo.setVisibility(View.INVISIBLE);  }
						if (GoproProduct.equals("Gopro4s")) { RecordFormat.setText(libPhotoRes4s[currentBurstRes]);} else { RecordFormat.setText(libPhotoRes4[currentBurstRes]);
						}
						RecordFormat.append(" | "+libBurstRate4[currentBurstRate]);
					}
//					RecordFormat.append(" | " + libBurstRate4[currentBurstRate]);
					if (currentProtuneBurstEnabled==1) {
						imageProtune.setVisibility(View.VISIBLE);
						if (currentProtuneBurstSetting>0) { imageBalance.setVisibility(View.VISIBLE); } else { imageBalance.setVisibility(View.INVISIBLE);  };
					} else {
						imageProtune.setVisibility(View.INVISIBLE); imageBalance.setVisibility(View.INVISIBLE);
					};
					break;

				case 1 :
					imageMode.setBackgroundResource(R.drawable.mode4);
					currentProtuneBurstEnabled=camFields.getProtuneBurstEnabled();
					currentProtuneBurstSetting=camFields.getProtuneBurstSetting();
					currentTimelapse = camFields.getTimeLapse();
					currentPhotoRes = camFields.getPhotoResolution();
					currentBurstRes =camFields.getBurstResolution();
					currentNbrePhotos = camFields.getPhotosOncard();
					FreePhotos = camFields.getPhotosAvailable();
					if (currentTimelapse == 0) { libTimelapse="0.5"; } else { libTimelapse = String.valueOf(currentTimelapse); }
					if (currentshutter==0) { imageRecording.setVisibility(View.INVISIBLE); } else { imageRecording.setVisibility(View.VISIBLE); }
					if (currentNoSDCard ==2) { textDuree.setText("No SD Card"); } else if (currentNoSDCard == 3) { textDuree.setText("SD Error"); } else { textDuree.setText(currentNbrePhotos + " | " + String.valueOf(FreePhotos)); }
					currentBurstExposure = camFields.getBurstExposure();
					if (currentBurstExposure==1) { imageExpo.setVisibility(View.VISIBLE); } else { imageExpo.setVisibility(View.INVISIBLE);  }
					if (GoproProduct.equals("Gopro4s")) { RecordFormat.setText(libPhotoRes4s[currentBurstRes]); } else { RecordFormat.setText(libPhotoRes4[currentBurstRes]); }
					RecordFormat.append(" | " + libTimelapse + " SEC");
					if (currentProtuneBurstEnabled==1) {
						imageProtune.setVisibility(View.VISIBLE);
						if (currentProtuneBurstSetting>0) { imageBalance.setVisibility(View.VISIBLE); } else { imageBalance.setVisibility(View.INVISIBLE);  };
					} else {
						imageProtune.setVisibility(View.INVISIBLE); imageBalance.setVisibility(View.INVISIBLE);
					};
					break;
				case 2 :
					imageMode.setBackgroundResource(R.drawable.mode14);
					currentProtuneBurstEnabled=camFields.getProtuneBurstEnabled();
					currentProtuneBurstSetting=camFields.getProtuneBurstSetting();
					currentNightlapseInterval = camFields.getNightlapseInterval();
					currentNightlapseShutter = camFields.getNightlapseShutter();
					currentTimelapse = camFields.getTimeLapse();
					currentPhotoRes = camFields.getPhotoResolution();
					currentBurstRes =camFields.getBurstResolution();
					currentNbrePhotos = camFields.getPhotosOncard();
					FreePhotos = camFields.getPhotosAvailable();
					if (currentshutter==0) { imageRecording.setVisibility(View.INVISIBLE); } else { imageRecording.setVisibility(View.VISIBLE); }
					if (currentNoSDCard ==2) { textDuree.setText("No SD Card"); } else if (currentNoSDCard == 3) { textDuree.setText("SD Error"); } else { textDuree.setText(currentNbrePhotos + " | " + String.valueOf(FreePhotos)); }
					currentBurstExposure = camFields.getBurstExposure();
					if (currentBurstExposure==1) { imageExpo.setVisibility(View.VISIBLE); } else { imageExpo.setVisibility(View.INVISIBLE);  }
					String lib2NightlapseInterval;
					if (currentNightlapseInterval>59) { lib2NightlapseInterval=String.valueOf(currentNightlapseInterval/60)+"mn"; } else { lib2NightlapseInterval= String.valueOf(currentNightlapseInterval)+"s"; };
					if (GoproProduct.equals("Gopro4s")) { RecordFormat.setText(libPhotoRes4s[currentBurstRes]); } else { RecordFormat.setText(libPhotoRes4[currentBurstRes]); }
					RecordFormat.append(" | " + lib2NightlapseInterval + " | " + libNightlapseShutter[currentNightlapseShutter]);
					if (currentProtuneBurstEnabled==1) {
						imageProtune.setVisibility(View.VISIBLE);
						if (currentProtuneBurstSetting>0) { imageBalance.setVisibility(View.VISIBLE); } else { imageBalance.setVisibility(View.INVISIBLE);  };
					} else {
						imageProtune.setVisibility(View.INVISIBLE); imageBalance.setVisibility(View.INVISIBLE);
					};
					break;
			}
				break;
			case 3 :  	imageMode.setBackgroundResource(R.drawable.mode4);
				currentProtuneBurstEnabled=camFields.getProtuneBurstEnabled();
				currentProtuneBurstSetting=camFields.getProtuneBurstSetting();
				currentTimelapse = camFields.getTimeLapse();
				currentPhotoRes = camFields.getPhotoResolution();
				currentBurstRes =camFields.getBurstResolution();
				currentNbrePhotos = camFields.getPhotosOncard();
				FreePhotos = camFields.getPhotosAvailable();
				if (currentTimelapse == 0) { libTimelapse="0.5"; } else { libTimelapse = String.valueOf(currentTimelapse); }
				if (currentshutter==0) { imageRecording.setVisibility(View.INVISIBLE); } else { imageRecording.setVisibility(View.VISIBLE); }
				if (currentNoSDCard ==2) { textDuree.setText("No SD Card"); } else if (currentNoSDCard == 3) { textDuree.setText("SD Error"); } else { textDuree.setText(currentNbrePhotos+" | "+String.valueOf(FreePhotos)); }
				if (GoproModel==3) {
					currentBurstExposure = camFields.getVideoExposure();
					if (currentBurstExposure==1) { imageExpo.setVisibility(View.VISIBLE); } else { imageExpo.setVisibility(View.INVISIBLE);  }
					RecordFormat.setText(libPhotoRes[currentPhotoRes]+" | "+libPhotoAngle[currentPhotoRes]+" | "+libTimelapse+" SEC");
				} else {
					currentBurstExposure = camFields.getBurstExposure();
					if (currentBurstExposure == 1) {
						imageExpo.setVisibility(View.VISIBLE); } else { imageExpo.setVisibility(View.INVISIBLE);  }
					if (GoproProduct.equals("Gopro4s")) {
						RecordFormat.setText(libPhotoRes4s[currentBurstRes]); } else { RecordFormat.setText(libPhotoRes4[currentBurstRes]); }
					RecordFormat.append(" | "+libTimelapse+" SEC");
				}
				if (currentProtuneBurstEnabled==1) {
					imageProtune.setVisibility(View.VISIBLE);
					if (currentProtuneBurstSetting>0) { imageBalance.setVisibility(View.VISIBLE); } else { imageBalance.setVisibility(View.INVISIBLE);  };
				} else {
					imageProtune.setVisibility(View.INVISIBLE); imageBalance.setVisibility(View.INVISIBLE);
				};
				break;
			case 5 :	imageMode.setBackgroundResource(R.drawable.mode7);
				StatusHaut(false); StatusBas(false);
				imageMode.setVisibility(View.VISIBLE);
				break;
			case 7 :	imageMode.setBackgroundResource(R.drawable.mode7);
				StatusHaut(false); StatusBas(false);
				imageMode.setVisibility(View.VISIBLE);
				break;
			default :	imageMode.setBackgroundResource(R.drawable.mode7);
				StatusHaut(false); StatusBas(false);
				imageMode.setVisibility(View.VISIBLE);
				break;
		}
		//
		// image du battery
		//
		int BatteryOn = camFields.getBattery();
		switch (BatteryOn) {
			case 4 : drawPower = res.getDrawable(R.drawable.iconepowerac); break;
			case 3 : drawPower = res.getDrawable(R.drawable.iconepower3); break;
			case 2 : drawPower = res.getDrawable(R.drawable.iconepower2); break;
			case 1 : drawPower = res.getDrawable(R.drawable.iconepower1); break;
			case 0 : drawPower = res.getDrawable(R.drawable.iconepower00); break;
		}
		imageBatterie.setImageDrawable(drawPower);
		//
		// image du Wifi
		//
		switch (wifiLevel) {
			case 0 : drawwifi = res.getDrawable(R.drawable.iconwifi0); break;
			case 1 : drawwifi = res.getDrawable(R.drawable.iconwifi1); break;
			case 2 : drawwifi = res.getDrawable(R.drawable.iconwifi2); break;
			case 3 : drawwifi = res.getDrawable(R.drawable.iconwifi3); break;
			case 4 : drawwifi = res.getDrawable(R.drawable.iconwifi4); break;
		}
		imageWifi.setImageDrawable(drawwifi);
		if (!PreviewOn) { textStatusCamera.setText("PREVIEW OFF"); }
//		Log.v("PreviewActivity", "fin refresh-status------------------------------------------");
	}

	private void PlayVideo()
	{
//		System.out.println("PlayVideo()");	
		try
		{
			final VideoView videoView = (VideoView) findViewById(R.id.videoView1);
			MediaController mediaController = new MediaController(this);
			mediaController.setAnchorView(videoView);
			Uri video; video=Uri.parse("rtsp://192.168.42.1/live");
			videoView.setMediaController(mediaController);
			videoView.setVideoURI(video);
			videoView.setMediaController(null);
			videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
//	            	System.out.println("onCompletion(MediaPlayer)");
					mp.stop();
					PlayVideo();
				}
			});
			videoView.setOnErrorListener(new OnErrorListener() {
				public boolean onError(MediaPlayer mp, int what, int extra) {
//	            	System.out.println("onError(MediaPlayer)");
					PreviewOn=false;
					return true;
				}
			});
			videoView.setOnPreparedListener(new OnPreparedListener() {
				public void onPrepared(MediaPlayer mp) {
//	            	System.out.println("onPrepared(MediaPlayer)");
					PreviewOn=true;
					videoView.start();
				}
			});
		}
		catch(Exception e)
		{
//	                System.out.println("Video Play Error :"+e.toString());
			finish();
		}
//		System.out.println("Out.PlayVideo()");
	}

	public String MyTimeFormat(long temp, String leformat) {
		SimpleDateFormat sdf = new SimpleDateFormat(leformat);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(new Date(temp));
	}

	public boolean isOnline()  {
		tools.out("PreviewActivityXiaomi isOnline()");
		boolean answer = false;
		int port = 7878;
		int TIMEOUT = 1000;
		Socket sock = new Socket();
		try {
			tools.out("PreviewActivityXiaomi isOnline() pour "+ip+":"+port);
			sock.connect(new InetSocketAddress(ip, port), TIMEOUT);
		} catch (IOException e) { answer = false; }
		if (sock.isConnected()) {
			try {
				sock.close();
			} catch (IOException e) { }
			answer = true;
		}
		tools.out("PreviewActivityXiaomi isOnline()="+answer);
		return answer;
	}

	private void StatusBas(boolean check) {
		if (check) {
			for (int i = 0; i < StatusBas.getChildCount(); i++) {
				View child = StatusBas.getChildAt(i);
				child.setVisibility(View.VISIBLE);
			}
		} else {
			for (int i = 0; i < StatusBas.getChildCount(); i++) {
				View child = StatusBas.getChildAt(i);
				child.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void StatusHaut(boolean check) {
		if (check) {
			for (int i = 0; i < StatusHaut.getChildCount(); i++) {
				View child = StatusHaut.getChildAt(i);
				child.setVisibility(View.VISIBLE);
			}
		} else {
			for (int i = 0; i < StatusHaut.getChildCount(); i++) {
				View child = StatusHaut.getChildAt(i);
				child.setVisibility(View.INVISIBLE);
			}

		}
	}

	public static boolean isBetween(int x, int lower, int upper) {
		return lower <= x && x <= upper;
	}


}