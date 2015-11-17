package robotics.sparkiguidebot;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Emily on 11/16/2015.
 */
public class WriteToSparkiTask extends AsyncTask<String, Void, Boolean> {
    private final OutputStream outputStream;


    public WriteToSparkiTask(BluetoothSocket socket) {
        OutputStream tmpOut = null;

        // Get the output stream, using temp object because
        // member streams are final
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        outputStream = tmpOut;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            byte[] bytes = params[0].getBytes();
            outputStream.write(bytes);
            return true;
        } catch (IOException e) { }
        return false;
    }
}
