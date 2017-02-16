package com.whhx.ipcbymessager;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by green on 2017/2/16.
 */

public class MessagerService extends Service {

    Messenger messenger = new Messenger(new MyMessagerHandler());
    Messenger servrer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    class MyMessagerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("abc", "msg = " + msg.obj);
            Toast.makeText(getApplicationContext(), "msg=" + msg.obj, Toast.LENGTH_LONG).show();
            servrer=msg.replyTo;
            Message message = Message.obtain();
            message.obj = "faf";
            try {
                servrer.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    Message message = Message.obtain();
                    message.arg1 = i;
                    try {
                        messenger.send(message);
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();
    }
}
