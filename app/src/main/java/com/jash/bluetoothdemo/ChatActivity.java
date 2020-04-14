package com.jash.bluetoothdemo;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private BluetoothDevice device;

    private EditText edit;
    ClientSocketThread clientSocketThread;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(ChatActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        device = getIntent().getParcelableExtra("device");
        if (TextUtils.isEmpty(device.getName())) {
            setTitle("没有名字");
        } else {
            setTitle(device.getName());
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        clientSocketThread=new ClientSocketThread(handler,device);
        clientSocketThread.start();

        edit = ((EditText) findViewById(R.id.chat_edit));
        findViewById(R.id.chat_send).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chat_send:
                String s = edit.getText().toString();
                if (!TextUtils.isEmpty(s)) {
                    clientSocketThread.send(s);
                    edit.setText("");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(clientSocketThread.socket!=null){//必须先要断开连接才能再次发送数据
                clientSocketThread.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
