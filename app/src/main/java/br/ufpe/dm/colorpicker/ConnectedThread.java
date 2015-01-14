package br.ufpe.dm.colorpicker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.os.Handler;

import android.util.Log;

import android.bluetooth.BluetoothSocket;

public class ConnectedThread extends Thread {

    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    final int RECIEVE_MESSAGE = 1;
    private static final String TAG = "bluetooth2";

    private Handler handler;


    private StringBuilder sb;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.sb = new StringBuilder();
        this.handler = handler;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[256];
        int bytes;

        while (true) {
            try {
                bytes = mmInStream.read(buffer);
                handler.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

    public void write(String message) {

        byte[] msgBuffer = new byte[1];
        msgBuffer[0] = (byte) Integer.parseInt(message);

        try {
            mmOutStream.write(msgBuffer);
        } catch (IOException e) {
            Log.d(TAG, "...Error data send: " + e.getMessage() + "...");

        }
    }


}
