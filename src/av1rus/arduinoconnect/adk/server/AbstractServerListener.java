package av1rus.arduinoconnect.adk.server;
/*
 * Created by Nick Maiello (aV1rus)
 * January 2, 2013
 * 
 */
import av1rus.arduinoconnect.adk.background.ADKService;

public class AbstractServerListener implements ServerListener{

	public void onServerStarted(Server server){
		//ADKService.getWordList().add("Server Started");
	}

	public void onServerStopped(Server server){
		//ADKService.getWordList().add("Server Stopped");
	}

	public void onClientConnect(Server server, Client client){
		//ADKService.getWordList().add("Client Connected");
	}

	public void onClientDisconnect(Server server, Client client){
		//ADKService.getWordList().add("Client Disconnected");
	}

	public void onReceive(Client client, byte[] data){
		//ADKService.getWordList().add("on Received");
	}

}
