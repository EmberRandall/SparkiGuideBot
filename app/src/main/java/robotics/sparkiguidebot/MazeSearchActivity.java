package robotics.sparkiguidebot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MazeSearchActivity extends AppCompatActivity {
    private BluetoothAdapter mAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private Handler mHandler;
    private ConnectThread mThread;
    private ArrayList<MazeNode> mPath;
    private int moveNumber = 0;
    private Orientation orientation = Orientation.EAST;

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
        mPath = maze.search();
        /*String message = "";
        for (MazeNode node : mPath) {
            message += "X = " + node.getX() + ", Y = " + node.getY() + "\n";
        }
        TextView textView = (TextView) findViewById(R.id.path);
        textView.setText(message);*/

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_SHORT).show();
        }

        mHandler = new Handler () {
            @Override
            public void handleMessage(Message msg) {
                String msgReceived = (String) msg.obj;
                Log.d("Handler", msgReceived);
                if (!msgReceived.contains("command")) {
                    View view = findViewById(R.id.continue_nav_button);
                    view.announceForAccessibility(getResources().getString(R.string.move_finished));
                }
            }
        };

        pairedDevices = mAdapter.getBondedDevices();
        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) {
            list.add(bt.getName());
            if (bt.getName().contains("ArcBotics")) {
                Toast.makeText(getApplicationContext(), "Found Sparki", Toast.LENGTH_SHORT).show();
                mThread = new ConnectThread(bt,  mAdapter, mHandler);
                mThread.start();
            }
        }
    }

    public void move(View v){
        String move = getNextMove();
        if (mThread != null && move != "") {
            mThread.write(move);
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
        if (moveNumber < mPath.size() - 1) {
            move = "f99";
            MazeNode current = mPath.get(moveNumber);
            MazeNode end = mPath.get(moveNumber + 1);
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
