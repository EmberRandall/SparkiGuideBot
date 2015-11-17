package robotics.sparkiguidebot;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Emily on 11/16/2015.
 */
public class ReadSparkiTask extends AsyncTask<Void, Void, String> {
    private final InputStream inputStream;
    private final Handler handler;


    public ReadSparkiTask(BluetoothSocket socket, Handler handler) {
        InputStream tmpIn = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) { }

        inputStream = tmpIn;
        this.handler = handler;
    }

    @Override
    protected String doInBackground(Void... params) {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = inputStream.read(buffer);
                // Send the obtained bytes to the UI activity
                Log.d("SparkiRead", buffer.toString());
                return buffer.toString();
            } catch (IOException e) {
                Log.d("SparkiRead", "Exception caught");
                break;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        handler.obtainMessage(0, result).sendToTarget();
    }
}
