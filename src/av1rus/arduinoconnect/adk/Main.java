package av1rus.arduinoconnect.adk;
/*
 * Created by Nick Maiello (aV1rus)
 * January 2, 2013
 * 
 */
import static av1rus.arduinoconnect.adk.utils.Uses.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TableRow.LayoutParams;
import av1rus.arduinoconnect.adk.R;
import av1rus.arduinoconnect.adk.background.ADKService;
import av1rus.arduinoconnect.adk.utils.Utils;
import av1rus.arduinoconnect.adk.widget.AnimationLayout;
import android.view.View.OnClickListener;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class Main extends FragmentActivity implements AnimationLayout.Listener,
		OnInitListener {
	public final static String TAG = "Nav Menu";
	private Intent bIntent;
	public static boolean shouldBeSend = true; // Turn this false if switches
	ScrollView scroll;											// are being updated
	private final int SETTING_ACTIVITY = 0;
	Utils utils;
	LinearLayout slider;
	LinearLayout navlayout;
	LinearLayout dataLayout;
	ImageView imgView;
	LayoutInflater inflater;
	TextView VenueRequest;
	int screenWidth;
	int screenHeight;

	Typeface heads;
	Typeface headsBold;
	Typeface selects;
	Typeface userFont;

	TextView bluetoothStatTV;
	TextView RPM_TV;

	Boolean hasBluetoothDevice = false;
	public static Boolean BluetoothConnected = false;

	protected ListView mList;
	protected AnimationLayout mLayout;

	static Context activityContext;
	static ArrayList<String> INPUTLIST;
	ArrayList<String> OUTPUTLIST;

	TextView bluetoothDisplay;

	boolean DirectHaloCommand = false;
	boolean Red = true;
	boolean White = true;
	boolean Heads = true;
	boolean Fogs = true;
	int powerCommanded = 0; // 1 = ON 2 = OFF 0 = No Response
	// Speech
	private TextToSpeech tts;
	private TextView Inform;
	private SpeechRecognizer sr;
	public Intent SPEAKINTENT;
	
	
	int vehicleRPM = 0;
	

	public void setupSlider(final TextView selectedBluetooth) {
		bluetoothDisplay = selectedBluetooth;
		activityContext = getApplicationContext();
		// this.log = log;
		utils = (Utils) getApplicationContext();
		INPUTLIST = new ArrayList<String>();
		OUTPUTLIST = new ArrayList<String>();
		// Get display Data
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		int layoutWidth = screenWidth / 2;

		if (screenWidth > screenHeight)
			layoutWidth = screenWidth / 4;
		
		scroll = (ScrollView) findViewById(R.id.scrollView1);
		final CheckBox showSlider = (CheckBox) findViewById(R.id.switchs_show);
		final LinearLayout switches_layout = (LinearLayout) findViewById(R.id.switches_layout);
		showSlider.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked)
					switches_layout.setVisibility(View.GONE);
				else
					switches_layout.setVisibility(View.VISIBLE);
			}
		});
		heads = Typeface.createFromAsset(getAssets(),"fonts/transroboticsbolditalic.ttf");
		headsBold = Typeface.createFromAsset(getAssets(),
				"fonts/BenchNine-Bold.ttf");
		selects = Typeface.createFromAsset(getAssets(),
				"fonts/MontserratSubrayada-Bold.ttf");
		userFont = Typeface.createFromAsset(getAssets(),
				"fonts/Akronim-Regular.ttf");

		mLayout = (AnimationLayout) findViewById(R.id.animation_layout);
		mLayout.setListener(this);

		slider = (LinearLayout) findViewById(R.id.animation_layout_sidebar);
		slider.setLayoutParams(new LayoutParams(layoutWidth,
				LayoutParams.MATCH_PARENT));
		slider.setPadding(3, 3, 3, 3);

		navlayout = (LinearLayout) findViewById(R.id.nav_layout);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		dataLayout = (LinearLayout) findViewById(R.id.currentData);
		bluetoothStatTV = new TextView(this);
		bluetoothStatTV.setText(status);
		bluetoothStatTV.setTextColor(Color.WHITE);
		bluetoothStatTV.setTypeface(heads);
		dataLayout.addView(bluetoothStatTV);

		RPM_TV = new TextView(this);
		RPM_TV.setText(status);
		RPM_TV.setTextColor(Color.WHITE);
		RPM_TV.setTypeface(headsBold);
		dataLayout.addView(RPM_TV);

		transfered = new ArrayList<String>();
		// adapter = new ArrayAdapter<String>(getApplicationContext(),
		// android.R.layout.simple_list_item_1, android.R.id.text1,
		// transfered);
		// setListAdapter(adapter);
		if (utils.getBluetoothDevice() == null)
			Toast.makeText(getApplicationContext(),
					"Set bluetooth device in application settings!!",
					Toast.LENGTH_LONG).show();

		bIntent = new Intent(this, ADKService.class);
		// doBindService();

		// Speech Recognition
		tts = new TextToSpeech(this, this);
		Button speakButton = (Button) findViewById(R.id.btn_spk);
		Inform = (TextView) findViewById(R.id.inform);
		Inform.setTypeface(userFont);
		Inform.setTextSize(19);
		speakButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SpeechBtn();
			}
		});
		

		selectedBluetooth.setTypeface(heads);
		sr = SpeechRecognizer.createSpeechRecognizer(this);
		sr.setRecognitionListener(new listener());
		// Make speakintent public
		SPEAKINTENT = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		SPEAKINTENT.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		SPEAKINTENT.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
				"av1rus.arduinoconnect");
		SPEAKINTENT.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
	}

	public TextView addTextView(TableRow toView, String text, Boolean isLabel) {
		TextView tvIs = new TextView(this);

		if (isLabel) {
			int hscreenWidth = (screenWidth / 2) - 40;
			tvIs.setLayoutParams(new LayoutParams(hscreenWidth / 2,
					LayoutParams.FILL_PARENT));
			tvIs.setGravity(Gravity.LEFT);
		} else
			tvIs.setGravity(Gravity.CENTER);

		tvIs.setText(text);
		toView.addView(tvIs);
		return tvIs;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent myIntent = new Intent(getApplicationContext(),
					SettingsActivity.class);
			startActivityForResult(myIntent, SETTING_ACTIVITY);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == SETTING_ACTIVITY) {
			if (!hasBluetoothDevice)
				restartActivity(this);
		}
	}

	public static void restartActivity(Activity act) {

		Intent intent = new Intent();
		intent.setClass(act, act.getClass());
		act.startActivity(intent);
		act.finish();

	}

	public void onResume() {
		super.onResume();
		startService(bIntent);
		registerReceiver(broadcastReceiver, new IntentFilter(ADKService.BROADCAST_ACTION));
	}

	public void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
		stopService(bIntent);
		if (mLayout.isOpening()) {
			mLayout.closeSidebar();
		}
	}

	public void onClickContentButton(View v) {
		mLayout.toggleSidebar();
	}

	@Override
	public void onBackPressed() {
		if (mLayout.isOpening()) {

			mLayout.closeSidebar();
		} else {
			finish();
		}
	}

	/* Callback of AnimationLayout.Listener to monitor status of Sidebar */
	public void onSidebarOpened() {
		Log.d(TAG, "opened");
		updateData();
		
		
	}

	/* Callback of AnimationLayout.Listener to monitor status of Sidebar */
	public void onSidebarClosed() {
		Log.d(TAG, "closed");
	}

	/* Callback of AnimationLayout.Listener to monitor status of Sidebar */
	public boolean onContentTouchedWhenOpening() {
		// the content area is touched when sidebar opening, close sidebar
		Log.d(TAG, "going to close sidebar");
		mLayout.closeSidebar();
		return true;
	}

	public static void sendToBluetooth(final String s) {
		if (shouldBeSend) ADKService.sendADB(s);
	}

	public void connectToBluetooth(final TextView selectedBluetooth) {
		bluetoothDisplay = selectedBluetooth;
	}

	public void receivedData(String s) {
	}

	public void updateLog(String s) {
	}

	/*
	 * private ServiceConnection mConnection = new ServiceConnection() {
	 * 
	 * public void onServiceConnected(ComponentName className, IBinder binder) {
	 * s = ((MyBinder) binder).getService();
	 * Toast.makeText(getApplicationContext(), "Connected to service",
	 * Toast.LENGTH_SHORT).show(); if(s != null && bluetoothDisplay != null){
	 * status = s.getBluetoothState();
	 * bluetoothDisplay.setText(s.getBluetoothState()); } }
	 * 
	 * public void onServiceDisconnected(ComponentName className) {
	 * Toast.makeText(getApplicationContext(), "Service Disconnected",
	 * Toast.LENGTH_SHORT).show(); s = null; } };
	 * 
	 * void doBindService() { bindService(new Intent(this,
	 * class), mConnection, Context.BIND_AUTO_CREATE);
	 * 
	 * }
	 * 
	 * public void showServiceData(View view) { if (s != null) {
	 * 
	 * Toast.makeText(this, "Number of elements" +
	 * s.getWordList().size(),Toast.LENGTH_SHORT).show(); status =
	 * s.getBluetoothState(); if(bluetoothDisplay!=null)
	 * bluetoothDisplay.setText(status); transfered.clear();
	 * transfered.addAll(s.getWordList()); adapter.notifyDataSetChanged();
	 * navlayout.notifyAll(); //updateData(s.getBluetoothState(),
	 * s.getWordList()); } }
	 */
	private ArrayAdapter<String> adapter;
	private StringReader sAdapter;
	private List<String> transfered;
	private String status;
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI(intent);
		}
	};

	private void updateUI(Intent intent) {
		int tag = intent.getIntExtra("tag", LOG_CHANGE);
		String data = intent.getStringExtra("data");
		if (tag == CONNECTION_STATE_CHANGE) {
			status = data;
			if (isConnected)
				new Thread() {
					public void run() {
						try {
							Thread.sleep(200);
							sendToBluetooth("!");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}.start();
			
			if (bluetoothDisplay != null)
				bluetoothDisplay.setText(status);
			
			bluetoothStatTV.setText(status);
			
			if(status.toLowerCase().contains("error")){
				bluetoothDisplay.setTextColor(getResources().getColor(R.color.red));
				bluetoothStatTV.setTextColor(getResources().getColor(R.color.red));
			}else{
				bluetoothDisplay.setTextColor(getResources().getColor(R.color.gr_lnk));
				bluetoothStatTV.setTextColor(getResources().getColor(R.color.gr_lnk));
			}

		} else if (tag == LOG_CHANGE) {
			updateData();
		} else if (tag == RPM_CHANGE) {
			try {
				if (vehicleRPM == 0 && Integer.parseInt(data) > 0)
					tts.speak("Engine Engaged", TextToSpeech.QUEUE_FLUSH, null);
				else{ 
					vehicleisOn = false;
					vehicleRPM = 0;
				}
				vehicleRPM = Integer.parseInt(data);
			} catch (Exception e) {
			}

			RPM_TV.setText(data + " RPMs");
		} else if (tag == RUNTIME_CHANGE) {

		} else if (tag == SPEED_CHANGE) {

		}
		updateMain(intent);
		updateData();
	}

	public void updateMain(Intent intent) { //OVERRIDE IN MACTIVITY
	}

	public void updateData() {
		Boolean currentScoolBottom = false;
		transfered.clear();
		transfered.addAll(ADKService.getWordList());

		//currentScoolBottom = !scroll.canScrollVertically(ScrollView.FOCUS_DOWN);
		
		if (mLayout.isOpening()) {

			navlayout.removeAllViews();
			TextView text;

			View customView;
			TextView actionName;
			// header
			customView = inflater.inflate(R.layout.settings_item_head, null);
			actionName = (TextView) customView.findViewById(R.id.headtv);
			actionName.setText("Transfers");
			actionName.setTypeface(selects);
			actionName.setTextSize(20);
			navlayout.addView(customView);

			LinearLayout actionLayout = new LinearLayout(this);
			actionLayout.setOrientation(LinearLayout.VERTICAL);
			actionLayout.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			actionLayout.setBackgroundColor(Color.DKGRAY);
			for (int i = 0; i < transfered.size(); i++) {
				text = new TextView(this);
				text.setText(transfered.get(i));
				text.setTextColor(Color.WHITE);
				actionLayout.addView(text);
			}
			navlayout.addView(actionLayout);
			
			
			//if(currentScoolBottom){
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						scroll.fullScroll(ScrollView.FOCUS_DOWN);
					}
				}, 100);
			//}
		}
	}

	public Boolean checkBluetoothDefault() {
		BluetoothDevice bd = utils.getBluetoothDevice();
		if (bd != null) {
			hasBluetoothDevice = true;
			// bluetoothDisplay.setText("Waiting for service");
			return true;
		} else
			// bluetoothDisplay.setText("No Device Selected");
			return false;
	}

	// SPEECH
	class listener implements RecognitionListener {
		public void onReadyForSpeech(Bundle params) {
			// Log.d(TAG, "onReadyForSpeech");
		}

		public void onBeginningOfSpeech() {
			// Log.d(TAG, "onBeginningOfSpeech");
		}

		public void onRmsChanged(float rmsdB) {
			// Log.d(TAG, "onRmsChanged");
		}

		public void onBufferReceived(byte[] buffer) {
			// Log.d(TAG, "onBufferReceived");
		}

		public void onEndOfSpeech() {
			// Log.d(TAG, "onEndofSpeech");
		}

		public void onError(int error) {
			/*
			Log.d(TAG, "error " + error);
			Inform.setText("error " + error);
			tts.speak("Error", TextToSpeech.QUEUE_FLUSH, null);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					sr.startListening(SPEAKINTENT);
				}
			}, 1700);*/
		}

		public void onResults(Bundle results) {
			String str = new String();
			Log.d(TAG, "onResults " + results);
			ArrayList<String> data = results
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			/*
			for (int i = 0; i < data.size(); i++) {
				Log.d(TAG, "result " + data.get(i));
				str += data.get(i);
			}*/
			Inform.setText("results: " + String.valueOf(data.size()));
			handleReceived(data);
		}

		public void onPartialResults(Bundle partialResults) {
			Log.d(TAG, "onPartialResults");
		}

		public void onEvent(int eventType, Bundle params) {
			Log.d(TAG, "onEvent " + eventType);
		}
	}

	public void SpeechBtn() {
		sr.startListening(SPEAKINTENT);
		Log.i("111111", "11111111");
	}

	@Override
	public void onInit(int status) {
		Inform.setText("Status: " + status);
		//tts.speak("Ready Captain", TextToSpeech.QUEUE_ADD, null);
	}
	public void handleReceived(ArrayList<String> commands) {

		final String[] sendActions = { Emergency1, Emergency2, Emergency3, Emergency4, HeartBeatRed };
		String command = "";
		Boolean GetAnswer = false;
		Boolean RESET = false;
		Boolean Shakira = false;
		Boolean Bad = false;
		Boolean Good = false;
		Boolean Battle = false;
		Boolean Cancel = false;
		Boolean ShutDown = false;
		Boolean getStatus = false;
		Boolean RPM = false;
		Boolean NO = false;
		//String SAY = "";
		int voiceCmd = -1;
		if (isConnected) {
			for (int i = 0; i < commands.size(); i++) {
				command = commands.get(i).toLowerCase(Locale.US);
				if(i==0)
				Inform.setText(command);
				Log.d(TAG, "result:  " + command);
				if (command.contains("shakira")) {
					Shakira = true;
				}
				if (command.contains("refresh")) {
					RESET = true;
				}
				if (command.contains("no")) {
					NO = true;
				}
				if (command.contains("status")) {
					getStatus = true;
				}
				if (command.contains("battle") || command.contains("war")) {
					Battle = true;
				}
				if (command.contains("cancel") || command.contains("nothing")) {
					Cancel = true;
				}
				if (command.contains("down") || command.contains("disengage") || command.contains("deactivate") || (command.contains("all") && command.contains("off"))) {
					ShutDown = true;
				}
				if (command.contains("rpm") || command.contains("rev")) {
					RPM = true;
				}
				if (command.contains("halos") || command.contains("halo") || command.contains("weapons") || command.contains("weapon")) { // Is halo command
					DirectHaloCommand = true;
				}
				// Color selector
				if (command.contains("red"))
					White = false;
				else if (command.contains("white"))
					Red = false;
				else if (command.contains("bad"))
					Bad = true;
				else if (command.contains("good"))
					Good = true;

				if (command.contains("head") || command.contains("heads")) {
					Fogs = false;
				} // Is halo command
				if (command.contains("fog") || command.contains("fogs")) {
					Heads = false;
				} // Is halo command
				if (command.contains("on") || command.contains("activate") || (command.contains("engage") && !command.contains("disengage"))) { // Is halo command
					if (powerCommanded != 2) // Make sure a more accurate response hasn't already been used
						powerCommanded = 1;
				} else if (command.contains("off") || command.contains("deactivate") || command.contains("disengage")) { // Is halo command
					if (powerCommanded != 1) // Make sure a more accurate response hasn't already been used
						powerCommanded = 2;
				}
			}
			if (RESET) {
				tts.speak("Resetting", TextToSpeech.QUEUE_FLUSH, null);
				resetSpeech();
			}  else if (Bad) {
				voiceCmd = R.array.said_bad;
				setHalos(1, 0, 1, 0);
			} else if (Good) {
				voiceCmd = R.array.said_good;
				setHalos(0, 1, 0, 1);
			} else if (NO) {
				voiceCmd = R.array.said_no;
				GetAnswer = true;
			}else if (Shakira) {
				voiceCmd = R.array.said_shakira;
				GetAnswer = true;
			} else if (getStatus) {
				currentStatus();
			} else if (RPM) {
				if (vehicleRPM > 0) {
					if (Red && White) {
						voiceCmd = R.array.follow_rpm_both;
						sendToBluetooth(FollowRPMMix.toUpperCase(Locale.US));
					} else if (Red) {
						voiceCmd = R.array.follow_rpm_both;
						sendToBluetooth(FollowRPMRedSmooth.toUpperCase(Locale.US));
					} else if (White) {
						voiceCmd = R.array.follow_rpm_both;
						sendToBluetooth(FollowRPMWhiteSmooth.toUpperCase(Locale.US));
					}
				} else {
					voiceCmd = R.array.error_vehicle_off;
					GetAnswer = true;
				}
			} else if (Battle) {
				voiceCmd = R.array.prepare_battle;
				voiceCmd=4;
				sendToBluetooth(sendActions[(int) Math.ceil(Math.random() * sendActions.length - 1)].toUpperCase(Locale.US));
			} else if (Cancel) {
				voiceCmd = R.array.cancel;
				GetAnswer = false;
			} else if (DirectHaloCommand) {
				if (powerCommanded == 1) {
					if (Heads && Fogs) {
						if (Red && White) {
							voiceCmd = R.array.all_halos_activated;
							setHalos(1, 1, 1, 1);
						} else if (Red) {
							voiceCmd = R.array.red_halos_activated;
							setHalos(1, 0, 1, 0);
						} else if (White) {
							voiceCmd = R.array.white_halos_activated;
							setHalos(0, 1, 0, 1);
						}
					} else if (Heads) {
						if (Red && White) {
							voiceCmd = R.array.head_all_halos_activated;
							setHalos(1, 1, 0, 0);
						} else if (Red) {
							voiceCmd = R.array.head_red_halos_activated;
							setHalos(1, 0, 0, 0);
						} else if (White) {
							voiceCmd = R.array.head_white_halos_activated;
							setHalos(0, 1, 0, 0);
						}
					} else if (Fogs) {
						if (Red && White) {
							voiceCmd = R.array.fog_all_halos_activated;
							setHalos(0, 0, 1, 1);
						} else if (Red) {
							voiceCmd = R.array.fog_red_halos_activated;
							setHalos(0, 0, 1, 0);
						} else if (White) {
							voiceCmd = R.array.fog_white_halos_activated;
							setHalos(0, 0, 0, 1);
						}
					}
				} else if (powerCommanded == 2) {
					if (Heads && Fogs) {
						if (Red && White) {
							voiceCmd = R.array.all_halos_deactivated;
							setHalos(0, 0, 0, 0);
						} else if (Red) {
							voiceCmd = R.array.red_halos_deactivated;
							setHalos(0, 2, 0, 2);
						} else if (White) {
							voiceCmd = R.array.white_halos_deactivated;
							setHalos(2, 0, 2, 0);
						}
					} else if (Heads) {
						if (Red && White) {
							voiceCmd = R.array.head_all_halos_deactivated;
							setHalos(0, 0, 2, 2);
						} else if (Red) {
							voiceCmd = R.array.head_red_halos_deactivated;
							setHalos(0, 2, 2, 2);
						} else if (White) {
							voiceCmd = R.array.head_white_halos_deactivated;
							setHalos(2, 0, 2, 2);
						}
					} else if (Fogs) {
						if (Red && White) {
							voiceCmd = R.array.fog_all_halos_deactivated;
							setHalos(2, 2, 0, 0);
						} else if (Red) {
							voiceCmd = R.array.fog_red_halos_deactivated;
							setHalos(2, 2, 0, 2);
						} else if (White) {
							voiceCmd = R.array.fog_white_halos_deactivated;
							setHalos(2, 2, 2, 0);
						}
					}
				} else if (powerCommanded == 0) {
					voiceCmd = R.array.error_no_power_command;
					GetAnswer = true;
				}

			}else if (ShutDown) {
				voiceCmd = R.array.power_down;
				setHalos(0, 0, 0, 0);
			} else {
				voiceCmd = R.array.error_got_nothing;
				GetAnswer = true;
			}
		} else {
			voiceCmd = R.array.error_not_connected;
		}
		if(voiceCmd != -1)
		handleVoiceData(voiceCmd);

		if (GetAnswer) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					sr.startListening(SPEAKINTENT);
				}
			}, 2400);
		} else {
			resetSpeech();
		}
	}
	public void handleVoiceData(int idOfArray){
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
		
		int index = -1;
		for(int i=0; i< stringArrays.length;i++){
			index = i;
			if(stringArrays[i] == idOfArray) break;
		}
		
		if(index!=-1)
			sendToBluetooth("$"+index+"<");
			
		String[] arrayUsed = getResources().getStringArray(idOfArray);
		String SAY = arrayUsed[(int) Math.ceil(Math.random()* arrayUsed.length - 1)];
		tts.speak(SAY, TextToSpeech.QUEUE_ADD, null);
	}
	public void setHalos(int headRed, int headWhite, int fogRed, int fogWhite) {
		Head_Halo_Both_On = false;
		Fog_Halo_Both_On = false;

		if (headRed == 0) Head_Halo_Red_On = false;
		else if (headRed == 1) Head_Halo_Red_On = true;

		if (headWhite == 0) Head_Halo_White_On = false;
		else if (headWhite == 1) Head_Halo_White_On = true;

		if (fogRed == 0) Fog_Halo_Red_On = false;
		else if (fogRed == 1) Fog_Halo_Red_On = true;

		if (fogWhite == 0) Fog_Halo_White_On = false;
		else if (fogWhite == 1) Fog_Halo_White_On = true;

		if (Head_Halo_White_On && Head_Halo_Red_On) {
			Head_Halo_Both_On = true;
			Head_Halo_White_On = false;
			Head_Halo_Red_On = false;
		}
		if (Fog_Halo_White_On && Fog_Halo_Red_On) {
			Fog_Halo_Both_On = true;
			Fog_Halo_White_On = false;
			Fog_Halo_Red_On = false;
		}

		updateHeadHalos();
		updateFogHalos();
	}

	public void resetSpeech() {
		powerCommanded = 0;
		DirectHaloCommand = false;
		Red = true;
		White = true;
		Heads = true;
		Fogs = true;
	}
	public void currentStatus(){
		String SAY = "";
		if(vehicleisOn){
			if(vehicleRPM>0){
				SAY += "Vehicle is Currently on. and running at "+vehicleRPM +" RPM.";
			}else{
				SAY += "Vehicle is Currently on but not running.";
			}
		}else{
			SAY += "Vehicle is Currently Off.";
		}
		
		if(Fog_Halo_Both_On && Head_Halo_Both_On){
			SAY += " All Halos are on.";
		}else if(Fog_Halo_Red_On && Head_Halo_Red_On){
			SAY += " All Red Halos are on.";
		}else if(Fog_Halo_White_On && Head_Halo_White_On){
			SAY += " All White Halos are on.";
		}else if(!Fog_Halo_Both_On && !Head_Halo_Both_On){
			SAY += " All Halos are off.";
		}else{ 
			if(Fog_Halo_Both_On){
			SAY += " All Fog Halos are on.";
			}else if(Fog_Halo_Red_On){
				SAY += " All Red Fog Halos are on.";
			}else if(Fog_Halo_White_On){
				SAY += " All White Fog Halos are on.";
			}
			if(Head_Halo_Both_On){
				SAY += " All Head Halos are on.";
				}else if(Head_Halo_Red_On){
					SAY += " All Red Head Halos are on.";
				}else if(Head_Halo_White_On){
					SAY += " All White Head Halos are on.";
				}
		}
		Log.d("Saying", SAY);
		tts.speak(SAY, TextToSpeech.QUEUE_ADD, null);
	}
	public void updateFogHalos() {
		CheckBox FogsRed = (CheckBox) findViewById(R.id.checkFogRed);
		CheckBox FogsWhite = (CheckBox) findViewById(R.id.checkFogWhite);
		CheckBox FogsBoth = (CheckBox) findViewById(R.id.checkFogBoth);
		FogsWhite.setChecked(Fog_Halo_White_On);
		FogsRed.setChecked(Fog_Halo_Red_On);
		FogsBoth.setChecked(Fog_Halo_Both_On);
	}

	public void updateHeadHalos() {
		CheckBox HeadsRed = (CheckBox) findViewById(R.id.checkHeadRed);
		CheckBox HeadsWhite = (CheckBox) findViewById(R.id.checkHeadWhite);
		CheckBox HeadsBoth = (CheckBox) findViewById(R.id.checkHeadBoth);
		HeadsWhite.setChecked(Head_Halo_White_On);
		HeadsRed.setChecked(Head_Halo_Red_On);
		HeadsBoth.setChecked(Head_Halo_Both_On);
	}

	public void updateInterior() {
		CheckBox Interior = (CheckBox) findViewById(R.id.interiorCheck);
		Interior.setChecked(Interior_On);
	}

	public void updateFogs() {
		CheckBox Fogs = (CheckBox) findViewById(R.id.fogCheck);
		Fogs.setChecked(Fog_On);

	}

}
