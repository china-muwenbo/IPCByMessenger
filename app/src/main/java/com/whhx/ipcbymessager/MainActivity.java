package com.whhx.ipcbymessager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @InjectView(R.id.button_send)
    Button buttonSend;
    @InjectView(R.id.editText_send)
    EditText editTextSend;
    @InjectView(R.id.activity_main)
    RelativeLayout activityMain;
    private Messenger messenger, mMessenger;
    private ServiceConnection mConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mMessenger=new Messenger(new MainHandler());
    }

    class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Snackbar.make(activityMain, ""+ msg.obj, Snackbar.LENGTH_SHORT).show();
            Log.i("abc",""+msg.obj);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                messenger = new Messenger(service);

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(new Intent(this, MessagerService.class), mConn, Context.BIND_AUTO_CREATE);
    }

    @OnClick(R.id.button_send)
    public void onClick() {
        String sendText = editTextSend.getText().toString();
        if (!TextUtils.isEmpty(sendText)) {
            Message message = Message.obtain();
            message.obj = sendText;
            message.replyTo=mMessenger;
            try {
                // 注意如果service没有启动，messenger是null 会抛出 NullPointerException
                if (messenger != null) {
                    messenger.send(message);
                } else {
                    Log.i("abc", "服务没有启动");
                }

            } catch (RemoteException e) {
                e.printStackTrace();
                Log.i("abc", "send failed");
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConn);
    }
}
