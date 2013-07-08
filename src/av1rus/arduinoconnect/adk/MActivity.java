package av1rus.arduinoconnect.adk;
/*
 * Created by Nick Maiello (aV1rus)
 * January 2, 2013
 * 
 */
import java.util.Locale;

import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.*;

import av1rus.arduinoconnect.adk.R;
import av1rus.arduinoconnect.adk.MainFragments.AutoDataFragment;
import av1rus.arduinoconnect.adk.MainFragments.BrightnessFragment2;
import av1rus.arduinoconnect.adk.MainFragments.MyFragment;
import av1rus.arduinoconnect.adk.MainFragments.PresetsFragment;
import av1rus.arduinoconnect.adk.MainFragments.VehicleDisplay;
import av1rus.arduinoconnect.adk.utils.Utils;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import static av1rus.arduinoconnect.adk.utils.Uses.*;

public class MActivity extends Main {

	private final String TAG = "MAIN ACTIVITY";
	Utils utils;
	TextView selectedBluetooth;
	private ViewPager mViewPager;
	private MyFragmentPagerAdapter mMyFragmentPagerAdapter;
	// Tabs
	private static final String[] CONTENT = new String[] { "Presets",
			"Brightness", "Vehicle", "OBD Data", "Data Log" };

	public boolean isTablet(Context context) {
		boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
		boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		return (xlarge || large);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Utils.activityActive = true;
		// Intent mService = new Intent(this, BluetoothService.class);
		// startService(mService);
		// Intent service = new Intent(this, MonitorService.class);
		// startService(service);
		if (isTablet(this)) {
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		utils = (Utils) getApplicationContext();

		selectedBluetooth = (TextView) findViewById(R.id.bluetoothDevice);
		
		setupSlider(selectedBluetooth);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(
				getSupportFragmentManager());
		mViewPager.setAdapter(mMyFragmentPagerAdapter);

		TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mViewPager);
		/*
		final float density = getResources().getDisplayMetrics().density;
        indicator.setBackgroundColor(0x22FF0000);
        indicator.setFooterColor(getResources().getColor(R.color.gr_lnk));
        indicator.setFooterLineHeight(1 * density); //1dp
        indicator.setFooterIndicatorHeight(3 * density); //3dp
        indicator.setFooterIndicatorStyle(IndicatorStyle.Underline);
        indicator.setTextColor(getResources().getColor(R.color.drkbl));
        indicator.setSelectedColor(getResources().getColor(R.color.gr_lnk));
        indicator.setSelectedBold(true);
*/
		final float density = getResources().getDisplayMetrics().density;
		indicator.setBackgroundColor(0x00000000);
		indicator.setFooterColor(0xFFAA2222);
		indicator.setFooterLineHeight(1 * density); // 1dp
		indicator.setFooterIndicatorHeight(3 * density); // 3dp
		indicator.setFooterIndicatorStyle(IndicatorStyle.Underline);
		indicator.setTextColor(0xAA000000);
		indicator.setSelectedColor(getResources().getColor(R.color.drkbl));
		indicator.setSelectedBold(true);
		// Setup CheckBoxs
		setupCheckBoxs();
		Log.d("Screen Width: ", "" + screenWidth);
		checkBluetoothDefault();
		if (hasBluetoothDevice) {
			final BluetoothDevice device = utils.getBluetoothDevice();
			new Thread() {
				public void run() {
					connectToBluetooth(selectedBluetooth);
				};
			}.start();
		}
	}

	private static class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {
			if (index == 0) {
				return PresetsFragment.newInstance(index);

			} else if (index == 1) {
				return BrightnessFragment2.newInstance(index);

			}else if (index == 2) {
				return VehicleDisplay.newInstance(index);

			} else if (index == 3) {
				return AutoDataFragment.newInstance(index);

			} else {
				return MyFragment.newInstance(index);
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length].toUpperCase(Locale.US);
		}

		@Override
		public int getCount() {

			return CONTENT.length;
		}
	}

	public void setupCheckBoxs(){
	////CHECKBOXS
		//Heads
		final CheckBox HeadsRed = (CheckBox) findViewById(R.id.checkHeadRed);
		final CheckBox HeadsWhite = (CheckBox) findViewById(R.id.checkHeadWhite);
		final CheckBox HeadsBoth = (CheckBox) findViewById(R.id.checkHeadBoth);
		final Button HeadsOff = (Button) findViewById(R.id.btnHeadOff);
		//Fogs
		final CheckBox FogsRed = (CheckBox) findViewById(R.id.checkFogRed);
		final CheckBox FogsWhite = (CheckBox) findViewById(R.id.checkFogWhite);
		final CheckBox FogsBoth = (CheckBox) findViewById(R.id.checkFogBoth);
		final Button FogsOff = (Button) findViewById(R.id.btnFogOff);
		
		updateHeadHalos();
		updateFogHalos();
		
		HeadsRed.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					HeadsWhite.setChecked(false);
					HeadsBoth.setChecked(false);
					sendToBluetooth(HEADS_RED.toUpperCase(Locale.US));
				}else{
					sendToBluetooth(HEADS_RED.toLowerCase(Locale.US));
				}
			}
			
		});
		HeadsWhite.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					HeadsRed.setChecked(false);
					HeadsBoth.setChecked(false);
					sendToBluetooth(HEADS_WHITE.toUpperCase(Locale.US));
				}else{
					sendToBluetooth(HEADS_WHITE.toLowerCase(Locale.US));
				}
			}
		});
		HeadsBoth.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					HeadsWhite.setChecked(false);
					HeadsRed.setChecked(false);
					sendToBluetooth(HEADS_BOTH.toUpperCase(Locale.US));
				}else{
					sendToBluetooth(HEADS_BOTH.toLowerCase(Locale.US));
				}
			}
		});
		FogsRed.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					FogsWhite.setChecked(false);
					FogsBoth.setChecked(false);
					sendToBluetooth(FOGS_RED.toUpperCase(Locale.US));
				}else{
					sendToBluetooth(FOGS_RED.toLowerCase(Locale.US));
				}
			}
			
		});
		FogsWhite.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					FogsRed.setChecked(false);
					FogsBoth.setChecked(false);
					sendToBluetooth(FOGS_WHITE.toUpperCase(Locale.US));
				}else{
					sendToBluetooth(FOGS_WHITE.toLowerCase(Locale.US));
				}
			}
		});
		FogsBoth.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					FogsWhite.setChecked(false);
					FogsRed.setChecked(false);
					sendToBluetooth(FOGS_BOTH.toUpperCase(Locale.US));
				}else{
					sendToBluetooth(FOGS_BOTH.toLowerCase(Locale.US));
				}
			}
		});
		
		
		
		//TURN OFF CHECKS
		HeadsOff.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				HeadsRed.setChecked(false);
				HeadsWhite.setChecked(false);
				HeadsBoth.setChecked(false);
			}
		});
		FogsOff.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				FogsRed.setChecked(false);
				FogsWhite.setChecked(false);
				FogsBoth.setChecked(false);
			}
		});
		
		/**********************************
		 *  TextViews Main Click Actions
		 */
		final TextView Red = (TextView) findViewById(R.id.red);
		final TextView White = (TextView) findViewById(R.id.white);
		final TextView Both = (TextView) findViewById(R.id.both);
		final TextView Off = (TextView) findViewById(R.id.off);
		
		Red.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(HeadsRed.isChecked() || FogsRed.isChecked()){
					HeadsRed.setChecked(false);
					FogsRed.setChecked(false);
				}else{
					HeadsRed.setChecked(true);
					FogsRed.setChecked(true);
				}
			}
		});
		White.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(HeadsWhite.isChecked() || FogsWhite.isChecked()){
					HeadsWhite.setChecked(false);
					FogsWhite.setChecked(false);
				}else{
					HeadsWhite.setChecked(true);
					FogsWhite.setChecked(true);
				}
			}
		});
		Both.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(HeadsBoth.isChecked() || FogsBoth.isChecked()){
					HeadsBoth.setChecked(false);
					FogsBoth.setChecked(false);
				}else{
					HeadsBoth.setChecked(true);
					FogsBoth.setChecked(true);
				}
			}
		});
		Off.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				HeadsOff.performClick();
				FogsOff.performClick();
			}
		});
		
		/******************
		 * Extras
		 */
		final CheckBox Interior = (CheckBox) findViewById(R.id.interiorCheck);
		final CheckBox Fogs = (CheckBox) findViewById(R.id.fogCheck);
		
		updateInterior();
		updateFogs();
		
		Interior.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					sendToBluetooth(INTERIOR.toUpperCase(Locale.US));
				}else{
					sendToBluetooth(INTERIOR.toLowerCase(Locale.US));
				}
			}
		});
		Fogs.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					sendToBluetooth(FOGS.toUpperCase(Locale.US));
				}else{
					sendToBluetooth(FOGS.toLowerCase(Locale.US));
				}
			}
		});
	}

	

	public void onPause() {
		super.onPause();
		Utils.activityActive = false;
	}

	public void onDestroy() {
		super.onDestroy();
		Utils.activityActive = false;
	}

	public void onResume() {
		super.onResume();
		Utils.activityActive = true;
	}

	public static void send(String s) {
		sendToBluetooth(s);
	}

	@Override
	public void receivedData(String s) {
		Log.d(TAG, "in Recived Data: " + s);

	}

	@Override
	public void updateMain(Intent intent) { // Overriding from Mains Broadcast
											// receiver
		int tag = intent.getIntExtra("tag", LOG_CHANGE);
		String data = intent.getStringExtra("data");

		if (tag == HEAD_HALO_CHANGE) {
			shouldBeSend = false;
			updateHeadHalos();
			shouldBeSend = true;
		} else if (tag == FOG_HALO_CHANGE) {
			shouldBeSend = false;
			updateFogHalos();
			shouldBeSend = true;
		} else if (tag == INTERIOR_CHANGE) {
			shouldBeSend = false;
			updateInterior();
			shouldBeSend = true;
		} else if (tag == FOG_CHANGE) {
			shouldBeSend = false;
			updateFogs();
			shouldBeSend = true;
		}
	}
}
