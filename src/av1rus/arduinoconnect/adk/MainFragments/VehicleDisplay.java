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
public class VehicleDisplay extends Fragment{  
	private Intent bIntent;
     public static VehicleDisplay newInstance(int index) {
 
    	 VehicleDisplay pageFragment = new VehicleDisplay();
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
    	View view = inflater.inflate(R.layout.frag_layout, container,	false);
 		
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
	public void updateUI(Intent intent) { // Overriding from Mains Broadcast
												// receiver
			int tag = intent.getIntExtra("tag", LOG_CHANGE);
			String data = intent.getStringExtra("data");

			if (tag == HEAD_HALO_CHANGE || tag == FOG_HALO_CHANGE || tag == INTERIOR_CHANGE || tag == FOG_CHANGE) {
			}
		}
	public void updateImageVehicle(){
		String file = "z";
		if(Head_Halo_Both_On) file+="b";
		else if(Head_Halo_Red_On) file+="r";
		else if(Head_Halo_White_On) file+="w";
		
		if(Fog_Halo_Both_On) file+="b";
		else if(Fog_Halo_Red_On) file+="r";
		else if(Fog_Halo_White_On) file+="w";
		
		if(file.length() == 3){
			
		}
		
	}
}  