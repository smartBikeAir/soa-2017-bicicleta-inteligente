package ar.so_unlam.edu.sba;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import static ar.so_unlam.edu.sba.AppConstants.RECIEVE_MESSAGE;

/**
 * Created by A646241 on 11/11/2017.
 */
public class ConnectedThread extends Thread {
    private static final String TAG = "ArduinoCon_BT";
    private final InputStream inputStream;
    private final OutputStream outputStream;

    private Handler handler;

    private AtomicInteger atomicInteger;
    private static final AppService APP_SERVICE = AppServiceImpl.getInstance();

    public ConnectedThread(BluetoothSocket socket) {
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        handler = new Handler();
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        inputStream = tmpIn;
        outputStream = tmpOut;

        atomicInteger = APP_SERVICE.getAtomicInteger();
    }

    public void run() {
        byte[] buffer = new byte[256];  // buffer store for the stream
        int bytes; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                // Get number of bytes and message in "buffer"
                bytes = inputStream.read(buffer);

                // Send to message queue Handler
                handler.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();
                /* synchronized (atomicInteger) {
                    while (atomicInteger.get() == MAX_READERS) {
                        atomicInteger.wait();
                    }
                    handler.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();
                    atomicInteger.notifyAll();
                    atomicInteger.incrementAndGet();
                } */
            } catch (IOException e) {
                String error = e.getMessage();
                byte[] errorBytes = error.getBytes();
                // Send to message queue Handler
                handler.obtainMessage(RECIEVE_MESSAGE, errorBytes.length, -1, errorBytes).sendToTarget();
                break;
            } /* catch (InterruptedException e) {
                String error = e.getMessage();
                byte[] errorBytes = error.getBytes();
                // Send to message queue Handler
                handler.obtainMessage(RECIEVE_MESSAGE, errorBytes.length, -1, errorBytes).sendToTarget();
                break;
            } */

        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(String message) {

        Log.d(TAG, "...Data to send: " + message + "...");
        byte[] msgBuffer = message.getBytes();
        try {
            outputStream.write(msgBuffer);
            atomicInteger.decrementAndGet();
        } catch (IOException e) {
            Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
