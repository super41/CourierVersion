package com.example.courierversion.Util;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by XJP on 2017/11/15.
 */
public class SocketUtil {
    HandlerThread mHandlerThread;
    Handler mHandler;
    Socket socket;
    DataOutputStream dos = null;
    DataInputStream dis = null;
    static SocketUtil INSTANCE;
    boolean work = false;
    Activity activity;
    SocketImp socketImp;
    private static final String HOST = "172.23.21.1";
    private static final int PORT = 6341;
    private static final int TIMEOUT = 5000;

    static final int MSG_CONNECT = 0;
    static final int MSG_RESULT = 1;
    static final int MSG_SEND=2;
    String result="";

    final int CALL_SUCCESS = 0;
    final int CALL_RESULT = 1;
    final int CALL_TIMEOUT = 2;
    final int CALL_FAIL = 3;

    public SocketUtil(Activity activity, SocketImp socketImp) {
        this.activity = activity;
        this.socketImp = socketImp;
        mHandlerThread = new HandlerThread("socket");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_CONNECT:
                        doConnect();
                        break;
                    case MSG_RESULT:
                        int i= msg.arg1;
                        switch (i){
                            case CALL_SUCCESS:
                                onCall(CALL_SUCCESS);
                                break;
                            case CALL_TIMEOUT:
                                onCall(CALL_TIMEOUT);
                                break;
                            case CALL_RESULT:
                                result= (String) msg.obj;
                                onCall(CALL_RESULT);
                                break;
                            case CALL_FAIL:
                                onCall(CALL_FAIL);
                                break;
                        }
                        break;
                    case MSG_SEND:
                        try {
                            String s = (String) msg.obj;
                            doSend(s);
                        }catch (NullPointerException e){
                             break;
                        }
                        break;
                }
            }
        };
    }



    public void connect() {
        sendMsg(MSG_CONNECT);
    }


    private void doConnect() {
        try {
            Socket s = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(HOST, PORT);
            s.connect(socketAddress, TIMEOUT);
            dos = new DataOutputStream(s.getOutputStream());
            dis = new DataInputStream(s.getInputStream());
            sendMsg(MSG_RESULT,CALL_SUCCESS);
            work = true;
            new ReadClass().start();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            sendMsg(MSG_RESULT,CALL_TIMEOUT);
        } catch (IOException e) {
            e.printStackTrace();
            sendMsg(MSG_RESULT,CALL_FAIL);
        }
    }

    class ReadClass extends Thread {
        @Override
        public void run() {
            super.run();
            while (work) {
                try {
                    result = dis.readUTF();
                    sendMsg(MSG_RESULT,CALL_RESULT,result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void send(String s){
        sendMsg(MSG_SEND,0,s);
    }

    public void doSend(String s){
        try {
            //dos.writeUTF(s);
            byte[] b=s.getBytes();
            dos.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendMsg(int what){
        mHandler.sendMessage(mHandler.obtainMessage(what));
    }
    void sendMsg(int what,int arg1){
        mHandler.sendMessage(mHandler.obtainMessage(what,arg1,0,null));
    }
    void sendMsg(int what,int arg1,String s){
        mHandler.sendMessage(mHandler.obtainMessage(what,arg1,0,s));
    }

   public  interface SocketImp {
        void onSuccess();

        void onTimeout();

        void onFail();

        void onResult(String s);
    }

    void onCall(int TYPE) {
        switch (TYPE) {
            case CALL_SUCCESS:
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        socketImp.onSuccess();
                    }
                });
                break;
            case CALL_RESULT:
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        socketImp.onResult(result);
                    }
                });
                break;
            case CALL_TIMEOUT:
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        socketImp.onTimeout();
                    }
                });
                break;
            case CALL_FAIL:
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        socketImp.onFail();
                    }
                });
                break;
        }
    }

}
