package lt.ismaniojikuprine.smartbackpack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class MainActivity extends AppCompatActivity implements BluetoothSPP.OnDataReceivedListener, BluetoothSPP.BluetoothStateListener {

    private BluetoothSPP bluetooth;
    private Toast connectingToast;
    private boolean isStatsOpen = false;

    private ImageView connecting, leftChild, rightChild, swing, smile, leftSad, rightSad;
    private Button stats;
    private SwingAnimator swingAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetooth = new BluetoothSPP(this);
        bluetooth.setBluetoothStateListener(this);
        bluetooth.setOnDataReceivedListener(this);
        bluetooth.setupService();

        connecting = (ImageView) findViewById(R.id.connecting);
        leftChild = (ImageView) findViewById(R.id.left_child);
        rightChild = (ImageView) findViewById(R.id.right_child);
        swing = (ImageView) findViewById(R.id.swing);
        smile = (ImageView) findViewById(R.id.smile);
        leftSad = (ImageView) findViewById(R.id.sad_left);
        rightSad = (ImageView) findViewById(R.id.sad_right);

        stats = (Button) findViewById(R.id.stats);
        stats.setOnClickListener(this::onStatsButtonClicked);

        swingAnimator = new SwingAnimator(this);
    }

    private void onStatsButtonClicked(View view) {
        isStatsOpen = !isStatsOpen;
        if (isStatsOpen) {
            Toast.makeText(this, R.string.history, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.real_time, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (bluetooth.isBluetoothEnabled()) {
            bluetooth.startService(BluetoothState.DEVICE_OTHER);
            bluetooth.autoConnect(getString(R.string.device_name));
        } else {
            Toast.makeText(this, R.string.enable_bluetooth, Toast.LENGTH_SHORT).show();
            openBluetoothSettings();
        }
    }

    private void openBluetoothSettings() {
        Intent intentBluetooth = new Intent();
        intentBluetooth.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentBluetooth);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetooth.stopAutoConnect();
        bluetooth.stopService();
        showConnecting();
    }

    private void showConnecting() {
        swingAnimator.balanceCenter();
        connecting.setVisibility(View.VISIBLE);
        leftChild.setVisibility(View.GONE);
        rightChild.setVisibility(View.GONE);
        swing.setVisibility(View.GONE);
        smile.setVisibility(View.GONE);
        leftSad.setVisibility(View.GONE);
        rightSad.setVisibility(View.GONE);
        stats.setVisibility(View.GONE);
    }

    private void showSwing() {
        swingAnimator.balanceCenter();
        connecting.setVisibility(View.GONE);
        leftChild.setVisibility(View.VISIBLE);
        rightChild.setVisibility(View.VISIBLE);
        swing.setVisibility(View.VISIBLE);
        smile.setVisibility(View.VISIBLE);
        stats.setVisibility(View.VISIBLE);
    }

    @Override
    public void onServiceStateChanged(int state) {
        if (state == BluetoothState.STATE_CONNECTED) {
            connectingToast.cancel();
            Toast.makeText(this, R.string.connected, Toast.LENGTH_SHORT).show();
            showSwing();
        } else if (state == BluetoothState.STATE_CONNECTING) {
            showConnecting();
            connectingToast = Toast.makeText(this, R.string.connecting, Toast.LENGTH_SHORT);
            connectingToast.show();
        }
    }

    @Override
    public void onDataReceived(byte[] data, String message) {
        if (message.startsWith("STAT:")) {
            String[] statuses = message.substring(5).split(";");
            if (isStatsOpen) {
                balanceSwingByHistory(statuses);
            } else {
                balanceSwingByCurrentStatus(statuses);
            }
        }
    }

    private void balanceSwingByCurrentStatus(String[] statuses) {
        if (statuses[2].equals("0")) {
            swingAnimator.balanceCenter();
        } else {
            if (statuses[0].equals("1")) {
                swingAnimator.lowerRight();
            } else {
                swingAnimator.lowerLeft();
            }
        }
    }

    private void balanceSwingByHistory(String[] statuses) {
        long leftTime = Long.parseLong(statuses[4]);
        long rightTime = Long.parseLong(statuses[5]);
        long bothTime = Long.parseLong(statuses[6]);
        long timeSum = leftTime + rightTime + bothTime;
        long leftRightDiff = Math.abs(leftTime - rightTime);
        if (((double) leftRightDiff / timeSum) > 0.05) {
            if (leftTime > rightTime) {
                swingAnimator.lowerLeft();
            } else {
                swingAnimator.lowerRight();
            }
        } else {
            swingAnimator.balanceCenter();
        }
    }
}
