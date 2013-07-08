package av1rus.arduinoconnect.adk.background;
/*
 * Created by Nick Maiello (aV1rus)
 * January 2, 2013
 * 
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import av1rus.arduinoconnect.adk.R;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import av1rus.arduinoconnect.adk.server.AbstractServerListener;
import av1rus.arduinoconnect.adk.server.Server;
import av1rus.arduinoconnect.adk.utils.Utils;

import static av1rus.arduinoconnect.adk.utils.Uses.*;

public class ADKService extends Service {
	
	private final static String TAG = "ADKService";
	private final IBinder mBinder = new MyBinder();
	private static ArrayList<String> list = new ArrayList<String>();
	private static String currentConnectionState = "Thinking";

	private static Handler _handler = new Handler();

	private int MAX = 250;
	Utils utils;

	private int RPM = 0;
	private int RunTime = 0;
	private int Speed = 0;
	
	static Server server = null;
	int sensorValue;
	
	private TextToSpeech tts;
	public static final String BROADCAST_ACTION = "av1rus.arduinoconnect";
	Intent intent;
	

	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		intent = new Intent(BROADCAST_ACTION);
		utils = (Utils) this.getApplicationContext();
		// start();
		start();
		
		tts = new TextToSpeech(getApplicationContext(), null);
		
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		// handler.removeCallbacks(sendUpdatesToUI);
		// handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
		//start();
		return Service.START_STICKY;
	}
	
	public class MyBinder extends Binder {
		public ADKService getService() {
			return ADKService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	public void start(){
		// Create TCP server
		if(server == null){
			try{
				server = new Server(4567);
				server.start();
				setupServer();
				currentConnectionState = "Connected";
			}catch (IOException e){
				Log.e("microbridge", "Unable to start TCP server", e);
				currentConnectionState = "Error.";
				System.exit(-1);
			}
		}
	}
	
	public void setupServer(){
		if(server != null){
			list.add("TCP: Successfully created");
			server.addListener(new AbstractServerListener() {
				@Override
				public void onReceive(av1rus.arduinoconnect.adk.server.Client client, byte[] data){
					if (data.length<2) return;
					sensorValue = (data[0] & 0xff) | ((data[1] & 0xff) << 8);
					handleReceived(data);
					//list.add("<<_"+sensorValue);
				};
				@Override
				public void onServerStarted(Server serve){
					currentConnectionState = "Connected";
					DisplayLoggingInfo(CONNECTION_STATE_CHANGE);
				};
				@Override
				public void onServerStopped(Server server){
					currentConnectionState = "Disconnected";
					DisplayLoggingInfo(CONNECTION_STATE_CHANGE);
				};
			});
				
		}
	}
	public void handleReceived(byte[] data){
		String workingWith = "";
		for(int i=0; i<data.length;i++) workingWith+=(char)data[i];
		
		list.add("received: "+workingWith);
		try {
			final String split[] = workingWith.split(":");
			if (split.length > 1) {
				if (!split[0].trim().equals("PM"))
					if (split[0].trim().equals("RPM")) {
						if (Integer.parseInt(split[1]) != RPM) {
							RPM = Integer.parseInt(split[1]);
							DisplayLoggingInfo(RPM_CHANGE);
						}

					} else if (split[0].trim().equals("RunTime")) {
						if (Integer.parseInt(split[1]) != RunTime) {
							RunTime = Integer.parseInt(split[1]);
							DisplayLoggingInfo(RUNTIME_CHANGE);
						}

					} else if (split[0].trim().equals("Speed")) {
						if (Integer.parseInt(split[1]) != Speed) {
							Speed = Integer.parseInt(split[1]);
							DisplayLoggingInfo(SPEED_CHANGE);
						}

					} else if (split[0].trim().equals("$")) {
						try{
							handleVoiceData(Integer.parseInt(split[1]));
						}catch(Exception e){
							list.add("Speak" + workingWith);
						}
						
					}else {
						if (list.size() > MAX) list.remove(0);
						list.add("<--" + workingWith);
						DisplayLoggingInfo(LOG_CHANGE);
						if (split[0].trim().equals("DT")) {
							_handler.post(new Runnable() {
								public void run() {
									try {
										//list.add("^^Light data");
										Log.d(TAG, "Handling Light data");
										handleLightData(split);
									} catch (Exception e) {
										Log.d(TAG, "Error handling light data" + e);
									}
								}
							});
						}
					}
			} else {
				if (list.size() > MAX) list.remove(0);
				list.add("<--" + workingWith);
				if(workingWith.toLowerCase().equals("off")) vehicleisOn = false;
				else if(workingWith.toLowerCase().equals("on")) vehicleisOn = true;
				DisplayLoggingInfo(LOG_CHANGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			list.add("<Error Handling" + workingWith);
		}
	}
	
	private void handleLightData(String[] data) {
		if (data.length >= 4 && data[0].trim().equals("DT")) {
			boolean areON = false;
			int Brightness = 0;
			if (data[1].trim().equals("S")) { // / 'S' for Switch
				try {
					if (1 == Integer.parseInt(data[3].trim())) {
						areON = true;
					}
					if (data[2].trim().equals("HB")) { // Handling Heads Both
						if (Head_Halo_Both_On != areON) { // Current Data is  different
							Head_Halo_Both_On = areON;
							Head_Halo_White_On = !areON;
							Head_Halo_Red_On = !areON;
							DisplayLoggingInfo(HEAD_HALO_CHANGE);
						}
					} else if (data[2].trim().equals("HW")) {// Handling Heads
																// White
						if (Head_Halo_White_On != areON) { // Current Data is
															// different
							Head_Halo_White_On = areON;
							Head_Halo_Red_On = !areON;
							Head_Halo_Both_On = false;
							DisplayLoggingInfo(HEAD_HALO_CHANGE);
						}
					} else if (data[2].trim().equals("HR")) {// Handling Heads
																// Red
						if (Head_Halo_Red_On != areON) { // Current Data is
															// different
							Head_Halo_Red_On = areON;
							Head_Halo_White_On = !areON;
							Head_Halo_Both_On = false;
							DisplayLoggingInfo(HEAD_HALO_CHANGE);
						}
					}
					if (data[2].trim().equals("FB")) { // Handling Fogs Both
						if (Fog_Halo_Both_On != areON) { // Current Data is  different
							Fog_Halo_Both_On = areON;
							Fog_Halo_White_On = !areON;
							Fog_Halo_Red_On = !areON;
							DisplayLoggingInfo(FOG_HALO_CHANGE);
						}
					} else if (data[2].trim().equals("FW")) {// Handling Fogs  White
						if (Fog_Halo_White_On != areON) { // Current Data is  different
							Fog_Halo_White_On = areON;
							Fog_Halo_Both_On = false;
							Fog_Halo_Red_On = !areON;
							DisplayLoggingInfo(FOG_HALO_CHANGE);
						}
					} else if (data[2].trim().equals("FR")) {// Handling Fogs
																// Red
						if (Fog_Halo_Red_On != areON) { // Current Data is
														// different
							Fog_Halo_Red_On = areON;
							Fog_Halo_White_On = !areON;
							Fog_Halo_Both_On = false;
							DisplayLoggingInfo(FOG_HALO_CHANGE);
						}
					}
					if (data[2].trim().equals("IN")) {// Handling Fogs Red
						if (Interior_On != areON) { // Current Data is different
							Interior_On = areON;
							DisplayLoggingInfo(INTERIOR_CHANGE);
						}
					}
					if (data[2].trim().equals("FS")) {// Handling Fogs Red
						if (Fog_On != areON) { // Current Data is different
							Fog_On = areON;
							DisplayLoggingInfo(FOG_CHANGE);
						}
					}
				} catch (Exception e) {
				}
			} else if (data[1].trim().equals("B")) { // / 'B' for Brightness
				try {
					Brightness = Integer.parseInt(data[3].trim());
					if (Brightness >= 0 || Brightness <= 255) {
						if (data[2].trim().equals("HB")) {
							if (Halo_Brightness != Brightness) {
								Halo_Brightness = Brightness;
								DisplayLoggingInfo(BRIGHTNESS_HALO_CHANGE);
							}
						} else if (data[2].trim().equals("IB")) {
							if (Interior_Brightness != Brightness) {
								Interior_Brightness = Brightness;
								DisplayLoggingInfo(BRIGHTNESS_INTERIOR_CHANGE);
							}
						} else if (data[2].trim().equals("FB")) {
							if (Fog_Brightness != Brightness) {
								Fog_Brightness = Brightness;
								DisplayLoggingInfo(BRIGHTNESS_FOG_CHANGE);
							}
						}
					}
				} catch (Exception e) {
				}
			}
		}
	}
	
	
	public static void sendADB(String send){
		try{
			server.send(send);
			list.add("-->" + send);
		}catch (IOException e){
			list.add("**>" + send);
			Log.e("microbridge", "problem sending TCP message", e);
		}	
	}
	
	
	
	private void DisplayLoggingInfo(int bTAG) {
		// Log.d(TAG, "entered DisplayLoggingInfo");
		intent.putExtra("tag", bTAG);
		String data = null;

		if (bTAG == CONNECTION_STATE_CHANGE) {
			data = currentConnectionState;
		} else if (bTAG == LOG_CHANGE) {
			for (int i = 0; i < list.size(); i++) {
				data += list.get(i) + "\n";
			}
		} else if (bTAG == RPM_CHANGE) {
			data = RPM+"";
		} else if (bTAG == RUNTIME_CHANGE) {
			data = RunTime + " sec";
		} else if (bTAG == SPEED_CHANGE) {
			data = Speed + " K/H";
		}

		intent.putExtra("data", data);
		sendBroadcast(intent);
	}
	
	int[] stringArrays = {
			R.array.said_shakira,
			R.array.said_no,
			R.array.said_bad,
			R.array.said_good,
			R.array.prepare_battle,
			R.array.power_down,
			R.array.cancel,
			R.array.error_not_connected,
			R.array.error_got_nothing,
			R.array.error_vehicle_off, 
			R.array.error_no_power_command, 
			R.array.all_halos_activated,
			R.array.red_halos_activated,
			R.array.white_halos_activated,
			R.array.head_all_halos_activated,
			R.array.head_red_halos_activated,
			R.array.head_white_halos_activated,
			R.array.all_halos_activated,
			R.array.fog_all_halos_activated,
			R.array.fog_red_halos_activated,
			R.array.fog_white_halos_activated,
			R.array.all_halos_deactivated,
			R.array.red_halos_deactivated,
			R.array.white_halos_deactivated,
			R.array.head_all_halos_deactivated,
			R.array.head_red_halos_deactivated,
			R.array.head_white_halos_deactivated,
			R.array.fog_all_halos_deactivated,
			R.array.fog_red_halos_deactivated,
			R.array.fog_white_halos_deactivated,
			R.array.follow_rpm_both,
			R.array.follow_rpm_red,
			R.array.follow_rpm_white
			};
	
	
	public void handleVoiceData(int arrayIndex){

		
		if(arrayIndex >= stringArrays.length || arrayIndex < 0) return;
		String[] arrayUsed = getResources().getStringArray(stringArrays[arrayIndex]);
		String SAY = arrayUsed[(int) Math.ceil(Math.random()* arrayUsed.length - 1)];
		list.add("Going to say" + SAY);
		tts.speak(SAY, TextToSpeech.QUEUE_ADD, null);
	}
	
	
	
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		server.stop();
		server = null;
	}

	public static String getConnectionState() {
		return currentConnectionState;
	}

	public static List<String> getWordList() {
		return list;
	}
}
