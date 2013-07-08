package av1rus.arduinoconnect.adk.MainFragments;

/*
 * Created by Nick Maiello (aV1rus)
 * January 2, 2013
 * 
 */
import java.util.ArrayList;
import java.util.Locale;

import av1rus.arduinoconnect.adk.R;
import av1rus.arduinoconnect.adk.MActivity;
import av1rus.arduinoconnect.adk.Main;
import android.graphics.Color;
import android.os.Bundle;  
import android.support.v4.app.Fragment;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;  
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TableRow.LayoutParams;

import static av1rus.arduinoconnect.adk.utils.Uses.*;
  
public class PresetsFragment extends Fragment {  
	ArrayList<CheckBox> presetBoxs;
	
     public static PresetsFragment newInstance(int index) {
 
    	 PresetsFragment pageFragment = new PresetsFragment();
         Bundle bundle = new Bundle();
         bundle.putInt("index", index);
         pageFragment.setArguments(bundle);
         return pageFragment;
     }
       
     @Override  
     public void onCreate(Bundle savedInstanceState) {  
         super.onCreate(savedInstanceState);  
     }  
       
     @Override  
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { 
    	int index = getArguments().getInt("index");
    	View view = inflater.inflate(R.layout.frag_layout, container,	false);
    	
    	presetBoxs = new ArrayList<CheckBox>();    	
    	//final MActivity activity = new MActivity();
 		/**********HEADER**************************ACTIONS***************************************************/
		

    	LinearLayout main = (LinearLayout) view.findViewById(R.id.flayout);
		
		
		String[] titles = {"Emergency 1", "Emergency 2", "Emergency 3", "Emergency 4", "HeartBeat Red", "HeartBeat White", "Follow RPM Mix", "Follow RPM Red", "Follow RPM White", "Follow RPM Red Smooth", "Follow RPM White Smooth"};
		int[] colors = {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE};
		final String[] sendActions = {Emergency1, Emergency2, Emergency3, Emergency4, HeartBeatRed, HeartBeatWhite, FollowRPMMix, FollowRPMRed, FollowRPMWhite, FollowRPMRedSmooth, FollowRPMWhiteSmooth};
		
		for(int i = 0; i<titles.length;i++){
			CheckBox actionBox = new CheckBox(getActivity());
			actionBox.setText(titles[i]);
			actionBox.setTextColor(colors[i]);
			presetBoxs.add(actionBox);
			
			final int ii = i;
			actionBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						resetBoxs(ii);
						MActivity.send(sendActions[ii].toUpperCase(Locale.US));
						
					}else{
						MActivity.send(sendActions[ii].toLowerCase(Locale.US));
					}
				}
				
			});

			main.addView(actionBox);
		}
		
 		return view;
     } 
     
     public void resetBoxs(int f){
    	 for(int i = 0; i<presetBoxs.size();i++){
    		 if(f != i)
				presetBoxs.get(i).setChecked(false);
			}
     }
}  