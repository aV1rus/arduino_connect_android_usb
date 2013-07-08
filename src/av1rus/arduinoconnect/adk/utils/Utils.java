package av1rus.arduinoconnect.adk.utils;
/*
 * Created by Nick Maiello (aV1rus)
 * January 2, 2013
 * 
 */
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;

public class Utils extends Application{
	public static final int NOTIFICATION_ID = 0;
	public static boolean IsServiceRunning = false;
	private SharedPreferences app_settings;
	private SharedPreferences.Editor editor;

	public static BluetoothSocket socket = null;
	public static OutputStream outputStream;
	public static InputStream inputStream;
    public static boolean activityActive = false;
	
	
	BluetoothDevice _bdevice;

	public void setBluetoothDevice(BluetoothDevice bdevice){
		editor = app_settings.edit();
		editor.putString("defaultBluetooth", bdevice.getAddress());
		editor.commit();
		this._bdevice = bdevice;
	}
	public BluetoothDevice getBluetoothDevice(){
		if(this._bdevice == null){
			app_settings = getSharedPreferences("app_settings", Activity.MODE_PRIVATE);
			String s = app_settings.getString("defaultBluetooth", null);
			if(s != null){
				try{
					BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					this._bdevice = bluetoothAdapter.getRemoteDevice(s);
				}catch(Exception e){
					setBluetoothDevice(null);
				}
			}
		}
		return this._bdevice;
	}
	public boolean getActivated() {
		// TODO Auto-generated method stub
		return true;
	}
	public boolean getHideNotification() {
		// TODO Auto-generated method stub
		return false;
	}
}
