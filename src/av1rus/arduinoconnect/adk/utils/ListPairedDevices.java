package av1rus.arduinoconnect.adk.utils;
/*
 * Created by Nick Maiello (aV1rus)
 * January 2, 2013
 * 
 */
import java.util.Set;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListPairedDevices extends ListActivity {
	String deviceBTName;
	public static final String PREFS_NAME = "myBluetoothDevice";
	SharedPreferences settings;
	Utils utils;
	ArrayAdapter<String> btArrayAdapter;
	
	
	
 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  utils = (Utils) getApplicationContext();
  settings = getSharedPreferences("bluetoothName" , 0);
  
  
  BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
  Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
  btArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

  if (pairedDevices.size() > 0) 
  {
	  
      for (BluetoothDevice device : pairedDevices) 
      {
    if(device != null && device.getName() != null){
       deviceBTName = device.getName();
       //String deviceBTMajorClass = getBTMajorDeviceClass(device.getBluetoothClass().getMajorDeviceClass());
       btArrayAdapter.add(deviceBTName);
    }
      
      }
      setListAdapter(btArrayAdapter);
  }
  else
  {
	  Toast.makeText(this, "You must pair a device first", Toast.LENGTH_LONG).show();
	  Intent dialogIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
      dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(dialogIntent);
  }
  }
 
 private String getBTMajorDeviceClass(int major){
  switch(major){ 
  case BluetoothClass.Device.Major.AUDIO_VIDEO:
   return "Audio_Video";
  case BluetoothClass.Device.Major.COMPUTER:
   return "Computer";
  case BluetoothClass.Device.Major.HEALTH:
   return "Health";
  case BluetoothClass.Device.Major.IMAGING:
   return "Imaging"; 
  case BluetoothClass.Device.Major.MISC:
   return "*Prob the Arduino*";
  case BluetoothClass.Device.Major.NETWORKING:
   return "Networking"; 
  case BluetoothClass.Device.Major.PERIPHERAL:
   return "Peripheral";
  case BluetoothClass.Device.Major.PHONE:
   return "Phone";
  case BluetoothClass.Device.Major.TOY:
   return "Toy";
  case BluetoothClass.Device.Major.UNCATEGORIZED:
   return "Uncategorized";
  case BluetoothClass.Device.Major.WEARABLE:
   return "Audio_Video";
  default: return "unknown!";
  }
 }

 @Override
 protected void onListItemClick(ListView l, View v, int position, long id) {
  // TODO Auto-generated method stub
  super.onListItemClick(l, v, position, id);
  BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
  Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
  int i = 0;
  for (BluetoothDevice device : pairedDevices) 
  {
	  if (i == position){
   deviceBTName = device.getName();
   utils.setBluetoothDevice(device);}
   i++;
  }
  //String device = deviceBTName;
  //SettingsMainActivity.btnListPairedDevices.setText(deviceBTName);
  //Saving the preferences
  String stringBluetooth = deviceBTName;
  SharedPreferences.Editor editor = settings.edit();
  editor.putString("sharedBluetooth", stringBluetooth);
  
  //Commit the edits!
  editor.commit();
     Intent intent = new Intent();
     intent.putExtra("bluetoothName", stringBluetooth);
     setResult(RESULT_OK, intent);
     finish();
 }
 
}