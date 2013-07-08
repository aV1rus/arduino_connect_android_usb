package av1rus.arduinoconnect.adk;
/*
 * Created by Nick Maiello (aV1rus)
 * January 2, 2013
 * 
 */
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;
import av1rus.arduinoconnect.adk.R;
import av1rus.arduinoconnect.adk.utils.ListPairedDevices;
import av1rus.arduinoconnect.adk.utils.Utils;

public class SettingsActivity extends Activity {
	 private static final int MY_DATA_CHECK_CODE = 3;
	 private static final int REQUEST_ENABLE_BT = 1;
	 private static final int REQUEST_PAIRED_DEVICE = 2;
	 
	int screenWidth;
	int screenHeight;
	Typeface heads;
	Typeface headsBold;
	Typeface selects;
	Typeface userFont;
	LinearLayout layout;
	LayoutInflater inflater;
	
	TextView DefaultBluetooth;
	Utils utils;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_layout);
		//Get display Data
	    DisplayMetrics dm = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(dm);
	    screenWidth = dm.widthPixels;
	    screenHeight = dm.heightPixels;
	    utils = (Utils) getApplicationContext();

		heads = Typeface.createFromAsset(getAssets(),"fonts/MontserratSubrayada-Bold.ttf");
		headsBold = Typeface.createFromAsset(getAssets(),"fonts/BenchNine-Bold.ttf");
		selects = Typeface.createFromAsset(getAssets(),"fonts/BenchNine-Light.ttf");
		userFont = Typeface.createFromAsset(getAssets(),"fonts/Akronim-Regular.ttf");
		
		layout = (LinearLayout) findViewById(R.id.layout);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.FILL_PARENT));
		
        layout.removeAllViews();
		View customView;
		LinearLayout rowLayout;
		CheckBox checkbox;
		TextView label;
		TextView subText;
		
		
		/**********HEADER**************************Title***************************************************/
		customView = inflater.inflate(R.layout.settings_item_head, null);
		label = (TextView) customView.findViewById(R.id.headtv);
		label.setText("Bluetooth Settings");
		label.setTypeface(heads);
		label.setTextColor(Color.RED);
		layout.addView(customView);
		
		rowLayout = getNewRow();
			label = addTextView(rowLayout, "Default Device", true);
			if(utils.getBluetoothDevice() != null){
				DefaultBluetooth = addTextView(rowLayout, utils.getBluetoothDevice().getName(), false);
			}else{
				DefaultBluetooth = addTextView(rowLayout, "No Device", false);
				
			}
		rowLayout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				try{
				Intent intent = new Intent();
				   intent.setClass(SettingsActivity.this, ListPairedDevices.class);
				   startActivityForResult(intent, REQUEST_PAIRED_DEVICE); 
				}catch(Exception e){
					Log.e("ArduinoConnect", "Error getting paired devices");
				}
			}
		});
		layout.addView(rowLayout);
		
	}
	public LinearLayout getNewRow(){
		LinearLayout l = new LinearLayout(this);
		l.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.FILL_PARENT));
		return l;
	}
	public TextView addTextView(LinearLayout toView, String text, Boolean isLabel){
		TextView tvIs = new TextView(this);
		if(isLabel){
			int hscreenWidth= (screenWidth/2)-40;
	        tvIs.setLayoutParams(new LayoutParams(hscreenWidth,LayoutParams.FILL_PARENT));
			tvIs.setTypeface(selects);
			tvIs.setTextSize(24);
			tvIs.setGravity(Gravity.LEFT);
		}else
			tvIs.setGravity(Gravity.CENTER);
		tvIs.setText(text);
		toView.addView(tvIs);
		return tvIs;
	}
	
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  // TODO Auto-generated method stub
	  if(requestCode == REQUEST_ENABLE_BT){
	   //CheckBlueToothState();
	  }
	  if (requestCode == REQUEST_PAIRED_DEVICE){
	   if(resultCode == RESULT_OK && data != null){
		   DefaultBluetooth.setText(data.getStringExtra("bluetoothName"));
	   }
	  }
	 }
}
