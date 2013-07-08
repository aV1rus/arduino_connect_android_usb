package av1rus.arduinoconnect.adk.MainFragments;

/*
 * Created by Nick Maiello (aV1rus)
 * January 2, 2013
 * 
 */
import av1rus.arduinoconnect.adk.R;
import av1rus.arduinoconnect.adk.MActivity;
import av1rus.arduinoconnect.adk.background.ADKService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;  
import android.support.v4.app.Fragment;  
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;  
import android.widget.TableRow.LayoutParams;
import static av1rus.arduinoconnect.adk.utils.Uses.*;
  
public class BrightnessFragment2 extends Fragment { 
	private Intent bIntent; 
    TextView HaloBright;
    TextView IntBright;
    TextView FogBright;
     public static BrightnessFragment2 newInstance(int index) {
 
    	 BrightnessFragment2 pageFragment = new BrightnessFragment2();
         Bundle bundle = new Bundle();
         bundle.putInt("index", index);
         pageFragment.setArguments(bundle);
         return pageFragment;
     }
       
     @Override  
     public void onCreate(Bundle savedInstanceState) {  
         super.onCreate(savedInstanceState);  
         bIntent = new Intent(getActivity(), ADKService.class);
     }  
       
     @Override  
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { 
    	int index = getArguments().getInt("index");
    	View view = inflater.inflate(R.layout.frag_brightnesss, container,false);
 		HaloBright = (TextView) view.findViewById(R.id.halobright);
 		IntBright = (TextView) view.findViewById(R.id.interiobright);
 		FogBright = (TextView) view.findViewById(R.id.fogbright);
 		
 		SeekBar haloBrightbar = (SeekBar) view.findViewById(R.id.seekBar1);
 		SeekBar interiorBrightbar = (SeekBar) view.findViewById(R.id.seekBar2);
 		SeekBar fogsBrightbar = (SeekBar) view.findViewById(R.id.seekBar3);
 		
 		haloBrightbar.setMax(255);
 		interiorBrightbar.setMax(255);
 		fogsBrightbar.setMax(255);
 		
 		haloBrightbar.setProgress(255);
 		interiorBrightbar.setProgress(255);
 		fogsBrightbar.setProgress(255);

		 HaloBright.setText(""+Halo_Brightness);
		 IntBright.setText(""+Interior_Brightness);
		 FogBright.setText(""+Fog_Brightness);
		 
 		haloBrightbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				 Halo_Brightness = progress;
		 		 HaloBright.setText(""+Halo_Brightness);
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				MActivity.send("+"+B_HALOS+Halo_Brightness+"-");
				Log.d("HAlo Bright", "+"+B_HALOS+Halo_Brightness+"-");
			}
 		}); // set seekbar listener.
 		interiorBrightbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
 			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
 				 Interior_Brightness = progress;
 		 		 IntBright.setText(""+Interior_Brightness);
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				MActivity.send("+"+B_INTERIOR+Halo_Brightness+"-");
			}
 		});
 		fogsBrightbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				 Fog_Brightness = progress;
			 	 FogBright.setText(""+Fog_Brightness);
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				MActivity.send("+"+B_FOGS+Halo_Brightness+"-");
				
			}
 		});
 		return view;
     }  
     @Override
     public void onResume(){
    	 super.onResume();
 		//getActivity().startService(bIntent);
 		getActivity().registerReceiver(broadcastReceiver, new IntentFilter(ADKService.BROADCAST_ACTION));
     }
     public void onPause(){
 		super.onPause();
 		getActivity().unregisterReceiver(broadcastReceiver);
 		//stopService(bIntent); 
 	}
     private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	        	updateUI(intent); 
	        }
	    }; 
	private void updateUI(Intent intent) {
	    	int tag = intent.getIntExtra("tag", LOG_CHANGE);	
	    	if(tag == BRIGHTNESS_HALO_CHANGE){
	    		HaloBright.setText(""+Halo_Brightness);
	    	}else if(tag == BRIGHTNESS_FOG_CHANGE){
	    		FogBright.setText(""+Fog_Brightness);
	    	}else if(tag == BRIGHTNESS_INTERIOR_CHANGE){
	    		IntBright.setText(""+Interior_Brightness);
	    	}
	    }
}  