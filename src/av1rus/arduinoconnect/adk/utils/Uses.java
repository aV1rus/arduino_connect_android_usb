package av1rus.arduinoconnect.adk.utils;
/*
 * Created by Nick Maiello (aV1rus)
 * January 2, 2013
 * 
 */
public class Uses {

	public static final String HEADS_RED = "A";
	public static final String HEADS_WHITE = "B";
	public static final String HEADS_BOTH = "C";

	public static final String FOGS_RED = "D";
	public static final String FOGS_WHITE = "E";
	public static final String FOGS_BOTH = "F";

	public static final String FOGS = "G";
	public static final String INTERIOR = "H";
	
	
	//PRESETS
	public static final String Emergency1= "Z";
	public static final String Emergency2= "Y";
	public static final String Emergency3= "X";
	public static final String Emergency4= "W";
	public static final String HeartBeatRed= "V";
	public static final String HeartBeatWhite= "U";
	public static final String FollowRPMMix= "T";
	public static final String FollowRPMWhite= "S";
	public static final String FollowRPMRed= "R";
	public static final String FollowRPMWhiteSmooth= "Q";
	public static final String FollowRPMRedSmooth= "P";
	

	public static final char B_HALOS = 'A';
	public static final char B_INTERIOR = 'B';
	public static final char B_FOGS = 'C';
	


	public static final int CONNECTION_STATE_CHANGE = 0;
	public static final int LOG_CHANGE = 1;
	public static final int RPM_CHANGE = 2;
	public static final int RUNTIME_CHANGE = 3;
	public static final int SPEED_CHANGE = 4;

	public static final int HEAD_HALO_CHANGE = 5;
	public static final int FOG_HALO_CHANGE = 6;
	public static final int FOG_CHANGE = 7;
	public static final int INTERIOR_CHANGE = 8;

	public static final int BRIGHTNESS_HALO_CHANGE = 9;
	public static final int BRIGHTNESS_INTERIOR_CHANGE = 10;
	public static final int BRIGHTNESS_FOG_CHANGE = 11;

	public static boolean Head_Halo_Red_On = false;
	public static boolean Head_Halo_White_On = true;
	public static boolean Head_Halo_Both_On = false;
	public static boolean Fog_Halo_Red_On = false;
	public static boolean Fog_Halo_White_On = true;
	public static boolean Fog_Halo_Both_On = false;
	public static boolean Fog_On = false;
	public static boolean Interior_On = false;

	public static int Halo_Brightness = 255;
	public static int Interior_Brightness = 255;
	public static int Fog_Brightness = 255;
	
	public static boolean vehicleisOn = false;
	
	public static Boolean isConnected = false;
	
}
