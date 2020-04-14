package com.jash.bluetoothdemo;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jash
 * Date: 15-9-23
 * Time: 下午2:55
 */
public class ClientListener implements Runnable{
    private final BluetoothDevice device;
    private BluetoothSocket socket;
    private List<Handler> list = new ArrayList<>();

    public ClientListener(BluetoothSocket socket) {
        this.socket = socket;
        device = socket.getRemoteDevice();
        new Thread(this).start();
    }

    public void register(Handler handler){
        list.add(handler);
    }
    public void unregister(Handler handler){
        list.remove(handler);
    }
    @Override
    public void run() {
        try {
            DataInputStream is = new DataInputStream(socket.getInputStream());
            String msg;
            while ((msg = is.readUTF()) != null) {
                Log.d("端","客户端收到服务端数据");
                Message message = Message.obtain();
                message.what = 0;
                message.obj = device.getName() + ":" + msg;
                for (Handler h : list) {
                    h.sendMessage(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
