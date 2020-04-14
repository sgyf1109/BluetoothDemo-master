package com.jash.bluetoothdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by jash
 * Date: 15-9-23
 * Time: 下午2:13
 */
public class ClientSocketThread extends Thread {
    public static UUID uuid = UUID.fromString("2562ca85-59cf-4fb4-b10c-262a52723a43");
    public BluetoothDevice device;
    //获取到一个Socket对象
    BluetoothSocket socket = null;
    public Handler handler;
    public ClientSocketThread(Handler handler,BluetoothDevice device) {
        this.device=device;
        this.handler=handler;
    }

    public void send(final String msg){
        new Thread(){
            @Override
            public void run() {
                try {
                    Log.d("端","发送数据");
                    if(socket!=null){
                        Log.d("端","发送成功");
                        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
                        os.writeUTF(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void run() {
        try {
            Log.d("端","客户端连接");
            socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            ClientListener clientListener=new ClientListener(socket);
            clientListener.register(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
