package org.gopro.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import org.apache.http.params.HttpConnectionParams;
import org.gopro.core.model.BacPacStatus;
import org.gopro.core.model.BackPack;
import org.gopro.core.model.CamFields;
import org.json.JSONObject;

public class GoProHelper {

	public static final boolean LOGGING_ENABLED = false;
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	private String mCameraAddress = null;
	private String mCameraAddress2 = null;
	private final DefaultHttpClient mClient = newInstance();
	private String ipAddress;
	private Integer port;
	private String password;
	static String response = null;

	public GoProHelper() {
	}

	public GoProHelper(String ipAddress, Integer port, String password) {
		this();
		this.setIpAddress(ipAddress);
		this.setPort(port);
		this.setPassword(password);
		// this.mCamera = paramGoProCamera;
		this.mCameraAddress = ("http://" + ipAddress + ":" + port);
		this.mCameraAddress2 = ("http://" + ipAddress);
	}

	private void hexDump(byte[] paramArrayOfByte, String paramString) {
	}

	private boolean passFail(byte[] paramArrayOfByte) {
		boolean bool = false;
		if (paramArrayOfByte != null) {
			int i = paramArrayOfByte.length;
			bool = false;
			if (i > 0) {
				int j = paramArrayOfByte[0];
				bool = false;
				if (j == 0)
					bool = true;
			}
		}
		return bool;
	}

	private boolean sendCommand(Operations paramString) {
		try {
			sendGET(this.mCameraAddress + paramString.toString() + "?t=" + this.getToken());
			return true;
		} catch (Exception localException) {
		}
		return false;
	}

	public boolean deleteFilesOnSd() {
		return sendCommand(Operations.CAMERA_DA);
	}

	public boolean deleteLastFileOnSd() {
		return sendCommand(Operations.CAMERA_DL);
	}

	public int fromBoolean(boolean paramBoolean) {
		if (paramBoolean)
			return 1;
		return 0;
	}

	public String getBacPacPassword() {
		try {
			GoProProtocolParser localGoProProtocolParser =
					new GoProProtocolParser(sendGET(this.mCameraAddress + Operations.BACPAC_SD));
			byte[] arrayOfByte = new byte[1];
			arrayOfByte[0] = localGoProProtocolParser.extractByte();
			boolean bool = passFail(arrayOfByte);
			Object localObject = null;
			if (bool) {
				String str = localGoProProtocolParser.extractString();
				localObject = str;
			}
			return (String) localObject;
		} catch (Exception localException) {
		}
		return null;
	}

	public BackPack getBackPackInfo() throws Exception {
//		System.out.println("getBackPackInfo()");
		BackPack localBackPack = new BackPack();
		GoProProtocolParser localGoProProtocolParser;
		try {
			byte[] arrayOfByte = sendGET("http://" + this.getIpAddress() + Operations.BACPAC_CV);
			localGoProProtocolParser = new GoProProtocolParser(arrayOfByte);
			if (localGoProProtocolParser.extractResultCode() != GoProProtocolParser.RESULT_IS_OK) {
				return null;
			}
		} catch (Exception localException) {
			throw new Exception("Fail to get backpack info", localException);
		}
		localBackPack.setVersion(localGoProProtocolParser.extractUnsignedByte());
		localBackPack.setModel(localGoProProtocolParser.extractUnsignedByte());
		localBackPack.setId(localGoProProtocolParser.extractFixedLengthString(2));
		localBackPack.setBootLoaderMajor(localGoProProtocolParser.extractUnsignedByte());
		localBackPack.setBootLoaderMinor(localGoProProtocolParser.extractUnsignedByte());
		localBackPack.setBootLoaderBuild(localGoProProtocolParser.extractUnsignedByte());
		localBackPack.setRevision(localGoProProtocolParser.extractUnsignedByte());
		localBackPack.setMajorversion(localGoProProtocolParser.extractUnsignedByte());
		localBackPack.setMinorversion(localGoProProtocolParser.extractUnsignedByte());
		localBackPack.setBuildversion(localGoProProtocolParser.extractUnsignedByte());
		localBackPack.setWifimac(localGoProProtocolParser.extractFixedLengthString(6));
		localBackPack.setSSID(localGoProProtocolParser.extractString());
		return localBackPack;
	}

	public BacPacStatus getBacpacStatus() throws Exception {
//		System.out.println("getBacpacStatus()");
		BacPacStatus localBacPacStatus = new BacPacStatus();
		GoProProtocolParser localGoProProtocolParser;
		try {
			byte[] arrayOfByte =
					sendGET(this.mCameraAddress + Operations.BACPAC_SE + "?t=" + this.getToken());
			hexDump(arrayOfByte, "BacPac SE");
			localGoProProtocolParser = new GoProProtocolParser(arrayOfByte);
			if (localGoProProtocolParser.extractResultCode() != GoProProtocolParser.RESULT_IS_OK)
				return null;
		} catch (Exception localException) {
			throw localException;
		}
		localBacPacStatus.setBacPacBattery(localGoProProtocolParser.extractUnsignedByte());
		localBacPacStatus.setWifiMode(localGoProProtocolParser.extractUnsignedByte());
		localBacPacStatus.setBlueToothMode(localGoProProtocolParser.extractUnsignedByte());
		localBacPacStatus.setRSSI(localGoProProtocolParser.extractUnsignedByte());
		localBacPacStatus.setShutterStatus(localGoProProtocolParser.extractUnsignedByte());
		localBacPacStatus.setAutoPowerOff(localGoProProtocolParser.extractUnsignedByte());
		localBacPacStatus.setBlueToothAudioChannel(localGoProProtocolParser.extractUnsignedByte());
		localBacPacStatus.setFileServer(localGoProProtocolParser.extractUnsignedByte());
		localBacPacStatus.setCameraPower(localGoProProtocolParser.extractUnsignedByte());
		localBacPacStatus.setCameraI2CError(localGoProProtocolParser.extractUnsignedByte());
		localBacPacStatus.setCameraReady(localGoProProtocolParser.extractUnsignedByte());
		localBacPacStatus.setCameraModel(localGoProProtocolParser.extractUnsignedByte());
		localBacPacStatus.setCameraProtocolVersion(localGoProProtocolParser.extractUnsignedByte());
		localBacPacStatus.setCameraAttached(localGoProProtocolParser.extractUnsignedByte());
		localBacPacStatus.setBOSSReady(localGoProProtocolParser.extractUnsignedByte());
		return localBacPacStatus;
	}
	public int getCameraHLSSegment() {
		try {
			byte[] arrayOfByte =
					sendGET(this.mCameraAddress + Operations.CAMERA_HS2 + "?t=" + this.getToken());
			return new GoProProtocolParser(arrayOfByte).extractUnsignedByte();
		} catch (Exception localException) {
		}
		return -1;
	}

	public String mondecode(byte[] aob) {
		char[] hexChars = new char[aob.length * 2];
		for ( int j = 0; j < aob.length; j++ ) {
		int v = aob[j] & 0xFF;
		hexChars[j * 2] = hexArray[v >>> 4];
		hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return String.valueOf(hexChars);
	}

	
	public CamFields getCameraInfo() {
//		System.out.println("getCameraInfo()");
		CamFields localCamFields = new CamFields();
		GoProProtocolParser localGoProProtocolParser;
		try {
			byte[] arrayOfByte =
					sendGET(this.mCameraAddress + Operations.CAMERA_CV + "?t=" + this.getToken());
			localGoProProtocolParser = new GoProProtocolParser(arrayOfByte);
			if (localGoProProtocolParser.extractResultCode() != GoProProtocolParser.RESULT_IS_OK)
				return null;
		} catch (Exception localException) {
			return null;
		}
		localCamFields.setProtocol(localGoProProtocolParser.extractUnsignedByte());
		localCamFields.setModel(localGoProProtocolParser.extractUnsignedByte());
		localCamFields.setVersion(localGoProProtocolParser.extractString());
		localCamFields.setCamname(localGoProProtocolParser.extractString());
		return localCamFields;
	}

	public String getCameraNameCN() {
//		System.out.println("getCameraNameCN");
		String str = this.getIpAddress();
		byte[] arrayOfByte;
		try {
			arrayOfByte =
					sendGET(this.mCameraAddress + Operations.CAMERA_CN + "?t=" + this.getToken());
			if ((arrayOfByte == null) || (arrayOfByte.length == 0) || (arrayOfByte[0] == 1))
				return str;
		} catch (Exception localException) {
			return str;
		}
		int i = arrayOfByte[1];
		int j = 0;
		for (int k = 2;; k++) {
			if (j >= i)
				return str;
			if (k < arrayOfByte.length)
				str = str + (char) arrayOfByte[k];
			j++;
		}
	}

	public CamFields getCameraSettings() throws Exception {
//		System.out.println("getCameraSettings()");
		try {
			byte[] arrayOfByte = sendGET(this.mCameraAddress + "/camera/se" + "?t=" + this.getToken());
//			System.out.println("         1         2         3         4         5         6         7         8         9        10        11  ");
//			System.out.println(" 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2");
//			System.out.println(""+mondecode(arrayOfByte));

			return getCameraSettings(new GoProProtocolParser(arrayOfByte));
		} catch (Exception localException) {
			throw new Exception("Fail to get camera settings", localException);
		}
	}

	public CamFields getCameraSettings(GoProProtocolParser paramGoProProtocolParser) {
//		System.out.println("getCameraSettings(GoProProtocolParser paramGoProProtocolParser)");
		CamFields localCamFields = new CamFields();
		if (paramGoProProtocolParser.extractResultCode() != GoProProtocolParser.RESULT_IS_OK)
			return null;
		localCamFields.setMode(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setMicrophoneMode(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setOndefault(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setVideoExposure(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setTimeLapse(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setAutopower(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setFieldOfView(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setPhotoResolution(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setVidres(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setAudioinput(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setPlaymode(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setPlaybackPos(paramGoProProtocolParser.extractInteger());
		localCamFields.setBeepSound(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setLedblink(paramGoProProtocolParser.extractUnsignedByte());
		int i = paramGoProProtocolParser.extractByte();
		localCamFields.setPreviewActive(true);
		localCamFields.setBattery(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setUsbMode(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setPhotosAvailable(paramGoProProtocolParser.extractShort());
		localCamFields.setPhotosOncard(paramGoProProtocolParser.extractShort());
		localCamFields.setVideoAvailable(paramGoProProtocolParser.extractShort());
		localCamFields.setVideoOncard(paramGoProProtocolParser.extractShort());
		localCamFields.setShutter(paramGoProProtocolParser.extractUnsignedByte());
		return localCamFields;
	}

	public CamFields getCameraSettingsExtended()  throws Exception {
//		System.out.println("getCameraSettingsExtended()");

		try {
				byte[] arrayOfByte = sendGET(this.mCameraAddress + "/camera/sx" + "?t=" + this.getToken());
//				System.out.println("         1         2         3         4         5         6         7         8         9        10        11  ");
//				System.out.println(" 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2 4 6 8 0 2");
//				System.out.println(""+mondecode(arrayOfByte));
				return getCameraSettingsExtended(new GoProProtocolParser(arrayOfByte));
		} catch (Exception localException) {
			throw new Exception("Fail to get camera extended settings", localException);
		}
	}
	
	public CamFields getCameraSettingsExtended4()  throws Exception {
//		System.out.println("getCameraSettingsExtended4()");
		try {
				String arrayOfByte = sendGET4(this.mCameraAddress + "/gp/gpControl/status");
		} catch (Exception localException) {
			throw new Exception("Fail to get camera status/settings", localException);
		}
		JSONObject reader = new JSONObject(response);
		CamFields localCamFields = new CamFields();
//		System.out.println(response);
		JSONObject status  = reader.getJSONObject("status");
		JSONObject settings  = reader.getJSONObject("settings");
//		System.out.println(status.getInt("1")+" "+status.getInt("2")+" "+status.getInt("3")+" "+status.getInt("4")+" "+status.getInt("6")+" "+status.getInt("8")+" "+status.getInt("9")+" | "+status.getInt("10")+" "+status.getInt("11")+" "+status.getInt("13")+" "+status.getInt("14")+" "+status.getInt("15")+" "+status.getInt("16")+" "+status.getInt("17")+" "+status.getInt("19")+" | "+status.getInt("20")+" "+status.getInt("22")+" "+status.getInt("24")+" "+status.getInt("26")+" "+status.getInt("27")+" "+status.getInt("28")+" "+status.getString("29")+" | "+status.getString("30")+" "+status.getInt("31")+" "+status.getInt("32")+" "+status.getInt("33")+" "+status.getInt("34")+" "+status.getInt("35")+" "+status.getInt("36")+" "+status.getInt("37")+" "+status.getInt("38")+" "+status.getInt("39")+" | "+status.getString("40")+" "+status.getInt("41")+" "+status.getInt("42")+" "+status.getInt("43")+" "+status.getInt("44")+" "+status.getInt("45")+" "+status.getInt("46")+" "+status.getInt("47")+" | "+status.getInt("48")+" "+status.getInt("49")+" "+status.getInt("54")+" "+status.getInt("55")+" "+status.getInt("56")+" "+status.getInt("57")+" "+status.getInt("58")+" "+status.getInt("59")+" ");
//		System.out.println(settings.getInt("1")+" "+settings.getInt("2")+" "+settings.getInt("3")+" "+settings.getInt("4")+" "+settings.getInt("5")+" "+settings.getInt("6")+" "+settings.getInt("7")+" "+settings.getInt("8")+" "+settings.getInt("9")+" | "+settings.getInt("10")+" "+settings.getInt("11")+" "+settings.getInt("12")+" "+settings.getInt("13")+" "+settings.getInt("14")+" "+settings.getInt("15")+" "+settings.getInt("16")+" "+settings.getInt("17")+" "+settings.getInt("18")+" "+settings.getInt("19")+" | "+settings.getInt("20")+" "+settings.getInt("21")+" "+settings.getInt("22")+" "+settings.getInt("23")+" "+settings.getInt("24")+" "+settings.getInt("25")+" "+settings.getInt("26")+" "+settings.getInt("27")+" "+settings.getInt("28")+" "+settings.getInt("29")+" | "+settings.getInt("30")+" "+settings.getInt("31")+" "+settings.getInt("32")+" "+settings.getInt("33")+" "+settings.getInt("34")+" "+settings.getInt("35")+" "+settings.getInt("36")+" "+settings.getInt("37")+" "+settings.getInt("38")+" "+settings.getInt("39")+" | "+settings.getInt("40")+" "+settings.getInt("41")+" "+settings.getInt("42")+" "+settings.getInt("43")+" "+settings.getInt("44")+" "+settings.getInt("45")+" "+settings.getInt("46")+" "+settings.getInt("47")+" "+settings.getInt("48")+" "+settings.getInt("49")+" | "+settings.getInt("50")+" "+settings.getInt("51")+" "+settings.getInt("52")+" "+settings.getInt("53")+" "+settings.getInt("54")+" "+settings.getInt("55")+" "+settings.getInt("56")+" "+settings.getInt("57")+" "+settings.getInt("58")+" "+settings.getInt("59")+" | "+settings.getInt("60")+" "+settings.getInt("61")+" "+settings.getInt("62")+" "+settings.getInt("63")+" "+settings.getInt("64")+" "+settings.getInt("65")+" "+settings.getInt("66")+" "+settings.getInt("67")+" "+settings.getInt("68")+" "+settings.getInt("69")+" | "+settings.getInt("70")+" "+settings.getInt("71")+" "+settings.getInt("72"));
		localCamFields.setMode(status.getInt("43"));
		localCamFields.setSubMode(status.getInt("44"));
		localCamFields.setMicrophoneMode(0);
		localCamFields.setOndefault(0);
		localCamFields.setVideoExposure(settings.getInt("9"));
		localCamFields.setPhotoExposure(settings.getInt("20"));
		localCamFields.setBurstExposure(settings.getInt("33"));
		localCamFields.setTimeLapse(settings.getInt("30"));
		localCamFields.setAutopower(0);
		localCamFields.setFieldOfView(settings.getInt("4"));
		localCamFields.setPhotoResolution(settings.getInt("17"));
		localCamFields.setBurstResolution(settings.getInt("28"));
		localCamFields.setVidres(settings.getInt("2"));
		localCamFields.setAudioinput(0);                                      
		localCamFields.setPlaymode(0);
		localCamFields.setPlaybackMin(0);
		localCamFields.setPlaybackSec(status.getInt("13"));
		localCamFields.setPreviewActive(true);
		localCamFields.setBattery(status.getInt("2"));
		localCamFields.setUsbMode(0);
		localCamFields.setPhotosAvailable(status.getInt("34"));
		localCamFields.setPhotosOncard(status.getInt("38"));
		localCamFields.setVideoAvailable(status.getInt("35"));
		localCamFields.setVideoOncard(status.getInt("39"));
		localCamFields.setShutter(status.getInt("10"));
		localCamFields.setProtuneVideoEnabled((short) settings.getInt("10"));
		localCamFields.setProtuneVideoSetting(settings.getInt("11"));
		localCamFields.setProtunePhotoEnabled((short) settings.getInt("21"));
		localCamFields.setProtunePhotoSetting(settings.getInt("22"));
		localCamFields.setProtuneBurstEnabled((short) settings.getInt("34"));
		localCamFields.setProtuneBurstSetting(settings.getInt("35"));
		localCamFields.setBurstRate(settings.getInt("29"));				
		localCamFields.setFramesPerSecond(settings.getInt("3"));
		localCamFields.setWifiBar(status.getInt("56"));
		localCamFields.setUpdown(settings.getInt("52")); 
		localCamFields.setContinousPhotoRate(settings.getInt("18")); 
		localCamFields.setTimelapseVideoInterval(settings.getInt("5"));
		localCamFields.setVideoPhotoInterval(settings.getInt("7"));
		localCamFields.setPhotoShutter(settings.getInt("19"));
		localCamFields.setNightlapseShutter(settings.getInt("31"));
		localCamFields.setNightlapseInterval(settings.getInt("32"));
		localCamFields.setNoSDCard(status.getInt("33"));
		if (settings.getInt("8")==1) { localCamFields.setLowLight(true); } else { localCamFields.setLowLight(false); }
		return localCamFields;
	}

	public CamFields getCameraSettingsExtended(GoProProtocolParser paramGoProProtocolParser) {
//		System.out.println("getCameraSettingsExtended(GoProProtocolParser paramGoProProtocolParser)");
		CamFields localCamFields = new CamFields();
		int i,j;
		if (paramGoProProtocolParser.extractResultCode() != GoProProtocolParser.RESULT_IS_OK)
			return null;
		localCamFields.setMode(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setMicrophoneMode(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setOndefault(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setVideoExposure(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setTimeLapse(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setAutopower(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setFieldOfView(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setPhotoResolution(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setVidres(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setAudioinput(paramGoProProtocolParser.extractUnsignedByte());                                      
		localCamFields.setPlaymode(paramGoProProtocolParser.extractShort());
		localCamFields.setPlaybackMin(paramGoProProtocolParser.extractByte());
		localCamFields.setPlaybackSec(paramGoProProtocolParser.extractByte());
		i = paramGoProProtocolParser.extractByte();
		i = paramGoProProtocolParser.extractByte();
		i = paramGoProProtocolParser.extractByte();
		i = paramGoProProtocolParser.extractByte();
		String s1 =   String.format("%8s", Integer.toBinaryString(i & 0xFF)).replace(' ', '0').substring(1,2);
		String s2 =   String.format("%8s", Integer.toBinaryString(i & 0xFF)).replace(' ', '0').substring(3,4);
		String s3 =   String.format("%8s", Integer.toBinaryString(i & 0xFF)).replace(' ', '0').substring(5,6);
		String s4 =   String.format("%8s", Integer.toBinaryString(i & 0xFF)).replace(' ', '0').substring(7,8);
		if (s1.equals("1")) { localCamFields.setPreviewActive(true); } else { localCamFields.setPreviewActive(false); }
		if (s3.equals("1")) { localCamFields.setUpdown(2); } else { localCamFields.setUpdown(1); }
		localCamFields.setBattery(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setUsbMode(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setPhotosAvailable(paramGoProProtocolParser.extractShort());
		localCamFields.setPhotosOncard(paramGoProProtocolParser.extractShort());
		localCamFields.setVideoAvailable(paramGoProProtocolParser.extractShort());
		localCamFields.setVideoOncard(paramGoProProtocolParser.extractShort());
		localCamFields.setShutter(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setProtuneVideoEnabled(paramGoProProtocolParser.extractUnsignedByte());
		i = paramGoProProtocolParser.extractUnsignedByte();
		localCamFields.setBurstRate(paramGoProProtocolParser.extractUnsignedByte());				
		i = paramGoProProtocolParser.extractUnsignedByte();
		localCamFields.setProtuneVideoSetting(paramGoProProtocolParser.extractUnsignedByte());
		i = paramGoProProtocolParser.extractUnsignedByte();
		i = paramGoProProtocolParser.extractUnsignedByte();
		i = paramGoProProtocolParser.extractUnsignedByte();
		i = paramGoProProtocolParser.extractUnsignedByte();
		i = paramGoProProtocolParser.extractUnsignedByte();
		i = paramGoProProtocolParser.extractUnsignedByte();
		i = paramGoProProtocolParser.extractUnsignedByte();
		i = paramGoProProtocolParser.extractUnsignedByte();
		i = paramGoProProtocolParser.extractUnsignedByte();
		i = paramGoProProtocolParser.extractUnsignedByte();
		i = paramGoProProtocolParser.extractUnsignedByte();
		i = paramGoProProtocolParser.extractUnsignedByte();
		i = paramGoProProtocolParser.extractUnsignedByte();
		i = paramGoProProtocolParser.extractUnsignedByte();
		i = paramGoProProtocolParser.extractUnsignedByte();
		localCamFields.setVidres(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setFramesPerSecond(paramGoProProtocolParser.extractUnsignedByte());
		if (localCamFields.getPhotosOncard()==65535 ) {	localCamFields.setNoSDCard(2); } else { localCamFields.setNoSDCard(0); }
		return localCamFields;
	}
	
	public DefaultHttpClient newInstance() {
//		System.out.println("getCameraSettingsExtended(GoProProtocolParser paramGoProProtocolParser)");
		BasicHttpParams localBasicHttpParams = new BasicHttpParams();
		HttpProtocolParams.setVersion(localBasicHttpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(localBasicHttpParams, "ISO-8859-1");
		HttpProtocolParams.setUseExpectContinue(localBasicHttpParams, true);
		HttpConnectionParams.setStaleCheckingEnabled(localBasicHttpParams, false);
		HttpConnectionParams.setConnectionTimeout(localBasicHttpParams, 10000);
		HttpConnectionParams.setSoTimeout(localBasicHttpParams, 10000);
		HttpConnectionParams.setSocketBufferSize(localBasicHttpParams, 8192);
		SchemeRegistry localSchemeRegistry = new SchemeRegistry();
		localSchemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		ConnManagerParams.setMaxTotalConnections(localBasicHttpParams, 1);
		return new DefaultHttpClient(new ThreadSafeClientConnManager(localBasicHttpParams,
				localSchemeRegistry), localBasicHttpParams);
	}

	public boolean sendCommand(Operations paramString, int paramInt) throws Exception {
//		System.out.println("sendCommand(Operations paramString, int paramInt)");
		StringBuilder localStringBuilder = new StringBuilder("%");
		Object[] arrayOfObject = new Object[1];
		arrayOfObject[0] = Integer.valueOf(paramInt);
		String paramString2 = "%"+String.format("%02x", arrayOfObject);
		return sendCommand(paramString, paramString2);
	}

	public boolean sendCommand(Operations paramString1, String paramString2) throws Exception {
//		System.out.println("sendCommand(Operations paramString1, String paramString2)");
		String param = null;
		if (!paramString1.toString().startsWith("/")) {
			param = "/" + paramString1.toString();
		} else {
			param = paramString1.toString();
		}
//		System.out.println("commande=" + this.mCameraAddress + param + "?t=" + this.getToken() + "&p=" + paramString2);
		sendGET(this.mCameraAddress + param + "?t=" + this.getToken() + "&p=" + paramString2);
		return true;
	}

	public boolean sendCommand4(Operations paramString, int paramInt) throws Exception {
//		System.out.println("sendCommand4(Operations paramString, int paramInt)");
		StringBuilder localStringBuilder = new StringBuilder("%");
		Object[] arrayOfObject = new Object[1];
		arrayOfObject[0] = Integer.valueOf(paramInt);
		String paramString2 = String.valueOf(paramInt);
		return sendCommand4(paramString, paramString2);
	}
	
	public boolean sendCommand4(Operations paramString1, String paramString2) throws Exception {
//		System.out.println("sendCommand4(Operations paramString1, String paramString2)");
		String param = null;
		if (!paramString1.toString().startsWith("/")) {
			param = "/" + paramString1.toString();
		} else {
			param = paramString1.toString();
		}
//		System.out.println("commande="+this.mCameraAddress2 + "/gp/gpControl" +param + "/" +paramString2);
		sendGET(this.mCameraAddress2 + "/gp/gpControl" +param + "/" +paramString2);
		return true;
	}
	
	public String sendGET4(String paramString) throws Exception {
		return sendGET4(paramString, this.mClient);
	}

	public String sendGET4(String paramString, DefaultHttpClient paramDefaultHttpClient)
			throws Exception {
//		System.out.println("sendGET4(String paramString, DefaultHttpClient paramDefaultHttpClient)");
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		HttpResponse localHttpResponse;
		HttpEntity httpEntity = null;
		try {
			System.setProperty("http.keepAlive", "true");
			HttpGet localHttpGet = new HttpGet(paramString);
			localHttpResponse = paramDefaultHttpClient.execute(localHttpGet);
			int statusCode = localHttpResponse.getStatusLine().getStatusCode();
			if (statusCode >= 400) {
				localHttpGet.abort();
				throw new IOException("Fail to send GET - HTTP error code = [" + statusCode + "]");
			}
		} catch (Exception localException) {
//			System.out.println("httpGet exception (sendGET4)");
			throw localException;
		}
		
		httpEntity = localHttpResponse.getEntity();
        response = EntityUtils.toString(httpEntity);
        return response;
	}

	
	public byte[] sendGET(String paramString) throws Exception {
		return sendGET(paramString, this.mClient);
	}

	public byte[] sendGET(String paramString, DefaultHttpClient paramDefaultHttpClient)
			throws Exception {
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		HttpResponse localHttpResponse;
		try {
			System.setProperty("http.keepAlive", "true");
			HttpGet localHttpGet = new HttpGet(paramString);
			localHttpResponse = paramDefaultHttpClient.execute(localHttpGet);
			int statusCode = localHttpResponse.getStatusLine().getStatusCode();
			if (statusCode >= 400) {
				localHttpGet.abort();
				throw new IOException("Fail to send GET - HTTP error code = [" + statusCode + "]");
			}
		} catch (Exception localException) {
//			System.out.println("httpGet exception (sendGET)");
			throw localException;
		}
		int j = (int) localHttpResponse.getEntity().getContentLength();
		if (j <= 0)
			j = 128;
		InputStream localInputStream = localHttpResponse.getEntity().getContent();
		byte[] arrayOfByte = new byte[j];
		while (true) {
			if (localInputStream.read(arrayOfByte, 0, arrayOfByte.length) == -1) {
				localByteArrayOutputStream.flush();
				return localByteArrayOutputStream.toByteArray();
			}
			localByteArrayOutputStream.write(arrayOfByte, 0, arrayOfByte.length);
		}
	}

	public boolean setBacPacWifiMode(int paramInt) throws Exception {
		return sendCommand(Operations.BACPAC_WI, paramInt);
	}

	public boolean setBackPackPowerCamera(boolean paramBoolean) throws Exception {
		return sendCommand(Operations.BACPAC_PW, fromBoolean(paramBoolean));
	}

	public boolean setCamAutoPowerOff(int paramInt) throws Exception {
		return sendCommand(Operations.CAMERA_AO, paramInt);
	}

	public boolean setCamDateTime(String paramString) {
		try {
			boolean bool =
					passFail(sendGET(this.mCameraAddress + "/camera/TM?t=" + this.getToken()
							+ "&p=" + paramString));
			return bool;
		} catch (Exception localException) {
		}
		return false;
	}

	public boolean setCamDefaultMode(int paramInt) throws Exception {
		return sendCommand(Operations.CAMERA_DM, paramInt);
	}


	public boolean setCamVideoResolution(int paramInt) throws Exception {
		return sendCommand(Operations.CAMERA_VV, paramInt);
	}

	public boolean setCamVideoResolution4(int paramInt) throws Exception {
		return sendCommand4(Operations.CAMERA4_VV, paramInt);
	}

	public boolean setLowLight(int paramInt) throws Exception {
		return sendCommand(Operations.CAMERA_LL, paramInt);
	}

	public boolean setLowLight4(int paramInt) throws Exception {
		return sendCommand4(Operations.CAMERA4_LL, paramInt);
	}

	public boolean setCamExposure(int paramInt) throws Exception {
		return sendCommand(Operations.CAMERA_EX, paramInt);
	}

	public boolean setCamExposure4(int paramInt) throws Exception {
		return sendCommand4(Operations.CAMERA4_EX, paramInt);
	}

	public boolean setCamFov(int paramInt) throws Exception {
		return sendCommand(Operations.CAMERA_FV, paramInt);
	}

	public boolean setCamFov4(int paramInt) throws Exception {
		return sendCommand4(Operations.CAMERA4_FV, paramInt);
	}
	
	public boolean setCamFps(int paramInt) throws Exception {
		return sendCommand(Operations.CAMERA_FS, paramInt);
	}
	
	public boolean setCamFps4(int paramInt) throws Exception {
		return sendCommand4(Operations.CAMERA4_FS, paramInt);
	}
	
	public boolean setCamProtune(int paramInt) throws Exception {
		return sendCommand(Operations.CAMERA_PT, paramInt);
	}

	public boolean setCamProtune4(int paramInt) throws Exception {
		return sendCommand4(Operations.CAMERA4_PT, paramInt);
	}

	public boolean setCamWriteBalance(int paramInt) throws Exception {
		return sendCommand(Operations.CAMERA_WB, paramInt);
	}

	public boolean setCamWriteBalance4(int paramInt) throws Exception {
		return sendCommand4(Operations.CAMERA4_WB, paramInt);
	}

	
	

	public boolean setCamLEDBlink(int paramInt) throws Exception {
		return sendCommand(Operations.CAMERA_LB, paramInt);
	}

	public boolean setCamLivePreview(boolean paramBoolean) throws Exception {
		/*
		 * if (paramBoolean) ; for (int i = 2;; i = 0) return
		 * sendCommand(Operations.CAMERA_PV, i);
		 */

		if (paramBoolean) {
			return sendCommand(Operations.CAMERA_PV, 2);
		} else {
			// return sendCommand(Operations.CAMERA_PV, 0);

			try {
				sendGET(this.mCameraAddress + Operations.CAMERA_PV + "?t=" + this.getToken()
						+ "&p=");
				return true;
			} catch (Exception localException) {
			}
			return false;
		}
	}

	public boolean setCamLocate(boolean paramBoolean) throws Exception {
		return sendCommand(Operations.CAMERA_LL, fromBoolean(paramBoolean));
	}

	public boolean setCamMode(String strmode) throws Exception {
		return sendCommand(Operations.CAMERA_CM, strmode);
	}

	public boolean setCamNtscPal(boolean paramBoolean) throws Exception {
		if (paramBoolean)
			;
		for (int i = 0;; i = 1)
			return sendCommand(Operations.CAMERA_VM, i);
	}

	public boolean setCamOnScreenDisplay(int paramInt) throws Exception {
		return sendCommand(Operations.CAMERA_DS, paramInt);
	}

	public boolean setCamPhotoResolution(int paramInt) throws Exception {
		return sendCommand(Operations.CAMERA_PR, paramInt);
	}

	public boolean setCamShutter(boolean paramBoolean) throws Exception {
		return sendCommand(Operations.BACPAC_SH, fromBoolean(paramBoolean));
	}

	public boolean setCamSound(int paramInt) throws Exception {
		return sendCommand(Operations.CAMERA_BS, paramInt);
	}

	public boolean setCamTimeLapseTI(String paramString) throws Exception {
		return sendCommand(Operations.CAMERA_TI, paramString);
	}

	public boolean setCamUpDown(int paramInt) throws Exception {
		return sendCommand(Operations.CAMERA_UP, paramInt);
	}

	public boolean setCameraHLSSegment(int paramInt) throws Exception {
		return sendCommand(Operations.CAMERA_HS, paramInt);
	}

	public boolean setCameraName(String paramString) {
		if ((paramString.length() > 31) || (paramString.length() == 0))
			return false;
		Object[] arrayOfObject = new Object[2];
		arrayOfObject[0] = Integer.valueOf(paramString.length());
		arrayOfObject[1] = paramString;
		String str =
				URLEncoder.encode(String.format("%x%s", arrayOfObject).replaceAll("\\s+", "%20"));
		try {
			boolean bool =
					passFail(sendGET(this.mCameraAddress + Operations.CAMERA_CN + "?t="
							+ this.getToken() + "&p=%0" + str));
			return bool;
		} catch (Exception localException) {
		}
		return false;
	}

	public boolean toBoolean(int paramInt) {
		return paramInt != 0;
	}

	public String getToken() {
		return getPassword();
	}

	public String getIpAddress() {
		return ipAddress;
	}

	private void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	private Integer getPort() {
		return port;
	}

	private void setPort(Integer port) {
		this.port = port;
	}

	private String getPassword() {
		return password;
	}

	private void setPassword(String password) {
		this.password = password;
	}

	public boolean startRecord() throws Exception {
		return sendCommand(Operations.BACPAC_SH, "%01");
	}

	public boolean stopRecord() throws Exception {
		return sendCommand(Operations.BACPAC_SH, "%00");
	}

	public boolean turnOnCamera() throws Exception {
		return sendCommand(Operations.BACPAC_PW, "%01");
	}

	public boolean turnOffCamera() throws Exception {
		return sendCommand(Operations.BACPAC_PW, "%00");
	}

	public boolean changeModeCamera() throws Exception {
		return sendCommand(Operations.BACPAC_PW, "%02");
	}

	public boolean modeCamera() throws Exception {
		return sendCommand(Operations.CAMERA_CM, "%00");
	}

	public boolean modePhoto() throws Exception {
		return sendCommand(Operations.CAMERA_CM, "%01");
	}

	public boolean modeBurst() throws Exception {
		return sendCommand(Operations.CAMERA_CM, "%02");
	}

	public boolean timelapse1() throws Exception {
		return sendCommand(Operations.CAMERA_CM, "%03");
	}

	public boolean timelapse2() throws Exception {
		return sendCommand(Operations.CAMERA_CM, "%04");
	}

}