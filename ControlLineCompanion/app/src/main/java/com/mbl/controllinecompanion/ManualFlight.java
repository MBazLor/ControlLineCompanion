package com.mbl.controllinecompanion;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;

public class ManualFlight extends AppCompatActivity {

    Handler UIHandler;
    Thread socketCreationThread = null;
    public static final int SERVERPORT = 6666;
    public static final String SERVERIP = "192.168.4.66";
    TextView btn_connect, txt_status, btn_motorStart, btn_motorStop;
    Slider sld_throttle;
    boolean sendFlag = false;
    String msg = "";
    String payload= "probando!!";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_flight);

        this.socketCreationThread = new Thread(new SocketCreationThread());

        txt_status = findViewById(R.id.txt_status);
        btn_connect = findViewById(R.id.btn_connect);
        btn_motorStart = findViewById(R.id.btn_motorStart);
        btn_motorStop = findViewById(R.id.btn_motorStop);
        sld_throttle = findViewById(R.id.sld_throttle);

        sld_throttle.setValue(1900); //Initial value
        UIHandler =  new Handler();

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               txt_status.setText("Connecting...");
               socketCreationThread.start();
            }
        });

        btn_motorStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("socket output", "Waiting...");
                while(sendFlag == true){
                    //wait if data is being sent, we do not want to alter msg
                }
                Log.d("slider value", Integer.toString((int) sld_throttle.getValue() ));
                payload = "thr="+ Integer.toString( (int) sld_throttle.getValue() ) +";\n";
                sendFlag = true;
                Log.d("socket output", "data ready to be sent");
            }
        });

        btn_motorStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("socket output", "Waiting...");
                while(sendFlag == true){
                    //wait if data is being sent, we do not want to alter msg
                }
                payload = "thr=1000;\n";
                sendFlag = true;
                Log.d("socket output", "data ready to be sent");
            }
        });

    }

    class SocketCreationThread implements Runnable {

        public void run() {
            Socket socket = null;
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVERIP);
                socket = new Socket(serverAddr, SERVERPORT);

                Log.d("socket thread", "Started connection");
                UIHandler.post(new updateUIThread("Connected"));

                DataOutputStream dataStreamOut = new DataOutputStream(socket.getOutputStream());
                //DataInputStream dataStreamIn = new DataInputStream(socket.getInputStream());

                new Thread(() -> {
                    try{
                        String msg = "";
                        while(true){
                            if(sendFlag){
                                Log.d("tcp socket", "starting to send payload");
                                Log.d("tcp payload", payload);
                                msg = payload;
                                dataStreamOut.writeBytes(msg);
                                dataStreamOut.flush();
                                sendFlag = false;
                                Log.d("tcp socket", "payload sent");
                            }
                        }
                    }catch(Exception e){
                        System.out.println(e);
                    }
                }).start();

                return;
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    class updateUIThread implements Runnable {
        private String data;

        public updateUIThread(String str){
            this.data = data;
        }

        @Override
        public void run() {
            txt_status.setText("Connected");
            txt_status.setTextColor(Color.rgb(0,255,0));
        }
    }

}
