package robotics.sparkiguidebot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MazeSearchActivity extends AppCompatActivity {
    private BluetoothAdapter adapter;
    private BluetoothSocket socket;
    private Set<BluetoothDevice> pairedDevices;
    private Handler handler;
    private ArrayList<MazeNode> path;
    private int moveNumber = 0;
    private Orientation orientation = Orientation.EAST;
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805f9b34fb");
    private ReadSparkiTask readTask;

    private enum Orientation {
        NORTH,
        EAST
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maze_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MazeSearch maze = new MazeSearch();
        path = maze.search();

        adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_SHORT).show();
        }

        handler = new Handler () {
            @Override
            public void handleMessage(Message msg) {
                String msgReceived = (String) msg.obj;
                Log.d("Handler", msgReceived);
                Button button = (Button) findViewById(R.id.continue_nav_button);
                if (msgReceived == getResources().getString(R.string.found_sparki)) {
                    button.setEnabled(true);
                }
                else if (msgReceived != null && msgReceived != getResources().getString(R.string.no_sparki_found)) {
                    button.announceForAccessibility(getResources().getString(R.string.move_finished));
                    readTask = null;
                }
            }
        };

        pairedDevices = adapter.getBondedDevices();
        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) {
            list.add(bt.getName());
            if (bt.getName().contains("ArcBotics")) {
                // Get a BluetoothSocket to connect with the given BluetoothDevice
                try {
                    // MY_UUID is the app's UUID string, also used by the server code
                    socket = bt.createRfcommSocketToServiceRecord(MY_UUID);
                    ConnectTask task = new ConnectTask(socket, adapter, getApplicationContext(), handler);
                    task.execute();
                } catch (IOException e) { }
            }
        }
    }

    public void move(View v){
        String move = getNextMove();
        if (move != "" && socket.isConnected()) {
            WriteToSparkiTask writeTask = new WriteToSparkiTask(socket);
            writeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, move);
            if (readTask == null) {
                readTask = new ReadSparkiTask(socket, handler);
                readTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            String message = getResources().getString(R.string.move_forward);
            if (move.contains("l")) {
                message = getResources().getString(R.string.turn_left);
            }
            else if (move.contains("r")) {
                message = getResources().getString(R.string.turn_right);
            }
            v.announceForAccessibility(message);
        }
    }

    private String getNextMove() {
        String move = "";
        if (moveNumber < path.size() - 1) {
            move = "f99";
            MazeNode current = path.get(moveNumber);
            MazeNode end = path.get(moveNumber + 1);
            if (current.getX() > end.getX() && orientation == Orientation.EAST) {
                move = "l \n" + move;
                orientation = Orientation.NORTH;
            }
            else if (current.getY() < end.getY() && orientation == Orientation.NORTH) {
                move = "r \n" + move;
                orientation = Orientation.EAST;
            }
        }
        moveNumber++;
        return move;
    }

}
