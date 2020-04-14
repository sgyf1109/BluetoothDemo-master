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
public class ServiceSocketThread extends Thread {
    private BluetoothServerSocket serverSocket;
    public static UUID uuid = UUID.fromString("2562ca85-59cf-4fb4-b10c-262a52723a43");
    private static Map<BluetoothDevice, ServiceListener> map = new HashMap<>();
    private Handler handler;
    public BluetoothSocket socket;
    public ServiceSocketThread(Handler handler) {
        this.handler = handler;
        try {
            serverSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord("", uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (serverSocket != null) {

            Log.d("端","服务端开启监听");
            try {
                while ((socket = serverSocket.accept()) != null){//服务端线程,获取到连接到客户端socket
                    Log.d("端","服务端接收到客户端");
                    BluetoothDevice device = socket.getRemoteDevice();
                    ServiceListener client = new ServiceListener(socket);
                    client.register(handler);
                    map.put(device, client);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
