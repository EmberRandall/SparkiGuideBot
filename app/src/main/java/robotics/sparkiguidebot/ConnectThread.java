package robotics.sparkiguidebot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Emily on 11/16/2015.
 */
public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothThread mThread;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter adapter, Handler handler) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mAdapter = adapter;
        mHandler = handler;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mAdapter.cancelDiscovery();
        Log.d("Thread", "Trying to connect");

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
            Log.d("Thread", "Connected");
        } catch (IOException connectException) {
            Log.d("Thread", "Caught exception");
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) {
            }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket);
    }

    // Will cancel an in-progress connection, and close the socket
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        mThread = new BluetoothThread(socket, mHandler);
        mThread.start();
    }

    public void write(String command) {
        Message message = Message.obtain();
        if (mThread != null) {
            mThread.write(command.getBytes());
            message.obj = "Wrote command";
        }
        else {
            message.obj = "Can't write command";
        }
        message.setTarget(mHandler);
        message.sendToTarget();
    }
}
