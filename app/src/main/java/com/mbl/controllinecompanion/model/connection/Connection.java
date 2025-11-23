package com.mbl.controllinecompanion.model.connection;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mbl.controllinecompanion.MainActivity;
import com.mbl.controllinecompanion.model.Payload;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Connector class that handles the communication socket with the CLDevice
 */
public class Connection implements Runnable {
    private static final int SERVERPORT = 6666;
    private static final String SERVERIP = "192.168.4.66";
    //private static final String SERVERIP = "10.0.2.2";
    private List<ConnectionListener> listeners;
    DataOutputStream dataStreamOut = null;
    DataInputStream dataStreamIn = null;
    Payload payload;
    //private boolean sentFlag;
    private boolean readyFlag;
    private boolean sentFlag = false;
    private boolean status;
    private static Connection instance;
    private Connection() {
            status = false;
            payload = Payload.getInstance();
            listeners = new ArrayList<>();
    }
    public static Connection getInstance(){
        if (instance == null){
            instance = new Connection();
        }
        return instance;
    }

    @Override
    public void run() {

        try(Socket socket = new Socket();  ){
            Log.d("socket thread", "Trying to connect");
            // Creating the socket this way, permits to set a timeout
            //socket = new Socket();
            socket.connect(new InetSocketAddress(SERVERIP, SERVERPORT), 2000);
            //socket = new Socket(SERVERIP, SERVERPORT);
            DataOutputStream dataStreamOut = new DataOutputStream(socket.getOutputStream());
            //for (ConnectionListener l : listeners) l.onConnected(); //Listeners notify
            notifyConnected(); //Notification extracted to a method
            Log.d("socket thread", "Started connection");
            status = true;
            //dataStreamOut = new DataOutputStream(socket.getOutputStream());
            //dataStreamIn = new DataInputStream(socket.getInputStream());
            readyFlag = false;
            String msg = "";

            //Status property advises from running state, but also works as disconnection signal
            while(status){
                if(readyFlag){
                    payload.lock();
                    msg = payload.getPayload() + "\n";
                    payload.unlock();
                    Log.d("tcp socket", "starting to send payload");
                    Log.d("tcp payload", msg);
                    dataStreamOut.writeBytes(msg);
                    dataStreamOut.flush();
                    Log.d("tcp socket", "payload sent");
                    /*if(dataStreamIn.available() > 0)
                        msg = dataStreamIn.readUTF();           //Warning!, blocking function
                    Log.d("tcp socket", "Device response: " + msg);*/
                    msg = "";

                    readyFlag = false;
                }
                Thread.sleep(100); //Some wait time to avoid excessive CPU usage
            }

        } catch (UnknownHostException e){
            status = false;
            Log.d("connection", "Error, host desconocido");
            //for (ConnectionListener l : listeners) l.onError("Unable to connect to receiver"); //Listeners notify
            notifyError("Unable to connect to receiver");
        } catch (IOException e) {
            status = false;
            Log.d("connection", e.getMessage());
            notifyError("Unable to connect to receiver");
            //for (ConnectionListener l : listeners) l.onError("Unable to connect to receiver"); //Listeners notify
        } catch (Exception e) {
            status = false;
            Log.d("connection", e.toString());
        }
    }

    public void sendPayload(){
        Log.d("connection", "Sending payload");
        readyFlag = true;
    }

    public boolean getStatus(){
        return status;
    }

    /**
     * Method to stop the connection in a safe manner, ensuring the resources
     * are closed and thread finishes
     */
    public void shutDown(){
        this.status = false;
    }

    public void addListener(ConnectionListener listener){
        listeners.add(listener);
    }

    public void removeListener(ConnectionListener listener){
        listeners.remove(listener);
    }


    /**
     * Notify error to registered listeners
     * @param message
     */
    public void notifyError(String message) {
        // Ensure execution on main loop, needed to update UI
        new Handler(Looper.getMainLooper()).post(() -> {
            for (ConnectionListener listener : listeners) {
                listener.onError(message);
            }
        });
    }


    /**
     * Notify connected to registered listeners
     */
    public void notifyConnected() {
        // Ensure this is executed on main loop.
        new Handler(Looper.getMainLooper()).post(() -> {
            for (ConnectionListener listener : listeners) {
                listener.onConnected();
            }
        });
    }


}
