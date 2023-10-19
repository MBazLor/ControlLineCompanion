package com.mbl.controllinecompanion;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import androidx.appcompat.app.AppCompatActivity;

public class ManualFlight extends AppCompatActivity {

    Handler UIHandler;
    Thread socketCreationThread = null;
    public static final int SERVERPORT = 6666;
    public static final String SERVERIP = "192.168.1.105";
    TextView btn_connect, txt_status, btn_motorStart, btn_motorStop;
    private TcpClient mTcpClient;
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
                payload = "thr=1800";
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
                payload = "thr=1000";
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
                                msg = payload;
                                dataStreamOut.writeUTF(msg);
                                dataStreamOut.flush();
                                sendFlag = false;
                            }
                        }
                    }catch(Exception e){
                        System.out.println(e);
                    }
                }).start();


                /*Thread2 commThread = new Thread2(socket);
                Thread3 sendThread = new Thread3(socket);
                new Thread(commThread).start();
                new Thread(sendThread).start();*/
                return;
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /*class Thread2 implements Runnable {
        private Socket clientSocket;
        private BufferedReader input;

        public Thread2(Socket clientSocket){
            this.clientSocket = clientSocket;
            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                //this.output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream())), true);
            }   catch (IOException e){
                e.printStackTrace();
            }
        }

        public void run(){
            while (!Thread.currentThread().isInterrupted()){
                try {
                    String read = input.readLine();
                    if(read != null){
                        //UIHandler.post(new updateUIThread(read));
                        Log.d("Server say", read);
                    }
                    else {
                        Thread1 = new Thread(new Thread1());
                        Thread1.start();
                        return;
                    }

                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }*/

    /*class Thread3 implements Runnable {
        private Socket clientSocket;

        private PrintWriter output;
        public Thread3(Socket clientSocket){
            this.clientSocket = clientSocket;
            try {

                this.output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream())), true);
            }   catch (IOException e){
                e.printStackTrace();
            }
        }

        public void run(){
            while (!Thread.currentThread().isInterrupted()){

                    if(sendFlag == true){
                        Log.d("socket output", "Sending...");
                        output.write(msg);
                        sendFlag = false;
                        Log.d("socket output", "Data sent, flag set to false, ready to new message");
                    }
            }
        }
    }*/

    class updateUIThread implements Runnable {
        private String data;

        public updateUIThread(String str){
            this.data = data;
        }

        @Override
        public void run() {
            txt_status.setText("Connected");
        }
    }

}
