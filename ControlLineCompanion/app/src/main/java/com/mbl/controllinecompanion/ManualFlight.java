package com.mbl.controllinecompanion;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import androidx.appcompat.app.AppCompatActivity;

public class ManualFlight extends AppCompatActivity {

    Handler UIHandler;
    Thread Thread1 = null;
    public static final int SERVERPORT = 6666;
    public static final String SERVERIP = "192.168.1.105";
    TextView btn_connect, txt_status;
    private TcpClient mTcpClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_flight);

        this.Thread1 = new Thread(new Thread1());

        txt_status = findViewById(R.id.txt_status);
        btn_connect = findViewById(R.id.btn_connect);
        UIHandler =  new Handler();

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               txt_status.setText("Connecting...");
               Thread1.start();
            }
        });
    }

    class Thread1 implements Runnable {
        public void run() {
            Socket socket = null;
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVERIP);
                socket = new Socket(serverAddr, SERVERPORT);
                Log.d("socket thread", "Started connection");
                UIHandler.post(new updateUIThread("Connected"));
                Thread2 commThread = new Thread2(socket);
                new Thread(commThread).start();
                return;
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    class Thread2 implements Runnable {
        private Socket clientSocket;
        private BufferedReader input;
        public Thread2(Socket clientSocket){
            this.clientSocket = clientSocket;
            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                
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
    }

    class updateUIThread implements Runnable {
        private String msg;

        public updateUIThread(String str){
            this.msg = str;
        }

        @Override
        public void run() {

        }
    }

}
