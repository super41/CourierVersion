package com.example.courierversion.Util;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

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
    private static final String HOST = "192.168.4.1";
    private static final int PORT = 6341;
    private static final int TIMEOUT = 5000;
    static final String TAG="xjp";

    static final int MSG_CONNECT = 0;
    static final int MSG_RESULT = 1;
    static final int MSG_SEND=2;
    byte[] result=null;

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
                                result= (byte[]) msg.obj;
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
                    Log.d("xjpll", "run: 0");
                    byte[] b=new byte[1024];
                    Log.d("xjpll", "run: 1");
                    int size=dis.read(b);//
                    if(size<0){
                        return;
                    }
                    result=new byte[size];
                    for(int i=0;i<size;i++){
                        result[i]=b[i];
                    }
                    Log.d("xjpll", "run: 2");
                    sendMsg(MSG_RESULT,CALL_RESULT,0,result);
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
            byte[] b=getByte(s);
            dos.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  static byte[]  getByte(String s){
        int length=s.length()+7;
        byte[] b=new byte[length];
        b[0]= (byte) 0xFF;
        b[1]= (byte) 0xAA;
        b[2]= (byte)(length-3);
        b[3]= (byte)0x08;
        char[] c=s.toCharArray();
        for(int i=0;i<s.length();i++){
            b[4+i]=(byte)c[i];
        }
        b[s.length()+4]=(byte)(s.length()+4);
        b[s.length()+5]=(byte)0xFF;
        b[s.length()+6]=(byte)0x55;
        SocketUtil.printHexString(b);
        return  b;
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
    void sendMsg(int what,int arg1,int arg2,Object o){
        mHandler.sendMessage(mHandler.obtainMessage(what,arg1,0,o));
    }

   public  interface SocketImp {
        void onSuccess();

        void onTimeout();

        void onFail();

        void onResult(byte[] s);
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

    public static void printHexString( byte[] b) {
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            Log.i(TAG, hex.toUpperCase());
        }

    }

}
