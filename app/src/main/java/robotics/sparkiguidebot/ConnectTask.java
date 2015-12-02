package robotics.sparkiguidebot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Emily on 11/16/2015.
 */
public class ConnectTask extends AsyncTask<Void, Void, Boolean> {

    private final BluetoothSocket socket;
    private final BluetoothAdapter adapter;
    private final Context context;
    private final Handler handler;

    public ConnectTask(BluetoothSocket socket, BluetoothAdapter adapter, Context context, Handler handler) {
        this.adapter = adapter;
        this.socket = socket;
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // Cancel discovery because it will slow down the connection
        adapter.cancelDiscovery();
        Log.d("Thread", "Trying to connect");

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            socket.connect();
            Log.d("Thread", "Connected");
            return true;
        } catch (IOException connectException) {
            Log.d("Thread", connectException.getMessage());
            // Unable to connect; close the socket and get out
            try {
                socket.close();
            } catch (IOException closeException) { }
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean isConnected) {
        String message = isConnected ? context.getResources().getString(R.string.found_sparki)
                : context.getResources().getString(R.string.no_sparki_found);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        handler.obtainMessage(0, message).sendToTarget();
    }
}
