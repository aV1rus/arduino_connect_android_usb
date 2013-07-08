package av1rus.arduinoconnect.adk.MainFragments;

/*
 * Created by Nick Maiello (aV1rus)
 * January 2, 2013
 * 
 */
import av1rus.arduinoconnect.adk.R;
import android.os.Bundle;  
import android.support.v4.app.Fragment;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.TextView;  
  
public class PageFragment2 extends Fragment {  
       
     public static PageFragment2 newInstance(int index) {
 
    	 PageFragment2 pageFragment = new PageFragment2();
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
    	View view = inflater.inflate(R.layout.frag, container,	false);
 		TextView textView1 = (TextView) view.findViewById(R.id.textView1);
 		textView1.setText(""+index);
 		
 		return view;
     }  
}  