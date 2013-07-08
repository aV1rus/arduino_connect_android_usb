package av1rus.arduinoconnect.adk.MainFragments;
/*
 * Created by Nick Maiello (aV1rus)
 * January 2, 2013
 * 
 */

import av1rus.arduinoconnect.adk.R;
import av1rus.arduinoconnect.adk.background.ADKService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;  
import android.support.v4.app.Fragment;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.TextView;  
  
import static av1rus.arduinoconnect.adk.utils.Uses.*;
public class AutoDataFragment extends Fragment{  
	private Intent bIntent;
	TextView RPM_TV;
     public static AutoDataFragment newInstance(int index) {
 
    	 AutoDataFragment pageFragment = new AutoDataFragment();
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
    	View view = inflater.inflate(R.layout.frag, container,	false);
    	RPM_TV = (TextView) view.findViewById(R.id.textView1);
    	RPM_TV.setText(ADKService.getConnectionState());
 		
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
	    	String data = intent.getStringExtra("data");	    	
	    	if(tag == CONNECTION_STATE_CHANGE){
	    		RPM_TV.setText(data);
	    	}else if(tag == LOG_CHANGE){
	    	}else if(tag == RPM_CHANGE){
	    		RPM_TV.setText(data);
	    	}else if(tag == RUNTIME_CHANGE){
	    		
	    	}else if(tag == SPEED_CHANGE){
	    		
	    	}
	    }
}  