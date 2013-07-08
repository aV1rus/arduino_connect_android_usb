package av1rus.arduinoconnect.adk.background;
/*
 * Created by Nick Maiello (aV1rus)
 * January 2, 2013
 * 
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyStartupIntentReceiver extends BroadcastReceiver{
	final String TAG = "Startup Receiver";
@Override
public void onReceive(Context context, Intent intent) {
	Log.d(TAG, "Boot request received");
	//Intent mService = new Intent(context, BluetoothService.class);
	//context.startService(mService);
	
	Intent service = new Intent(context, ADKService.class);
	context.startService(service);
	
	/*
	 Intent serviceIntent = new Intent();
		serviceIntent.setAction("com.wissen.startatboot.MyService");
		context.startService(serviceIntent);
	 */
}
}
