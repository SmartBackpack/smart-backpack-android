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

    private ImageView connecting, leftChild, rightChild, swing;
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

        swingAnimator = new SwingAnimator(this);

        Button left = (Button) findViewById(R.id.left);
        left.setOnClickListener((View v) -> swingAnimator.lowerLeft());

        Button center = (Button) findViewById(R.id.center);
        center.setOnClickListener((View v) -> swingAnimator.balanceCenter());

        Button right = (Button) findViewById(R.id.right);
        right.setOnClickListener((View v) -> swingAnimator.lowerRight());
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
        connecting.setVisibility(View.VISIBLE);
        leftChild.setVisibility(View.GONE);
        rightChild.setVisibility(View.GONE);
        swing.setVisibility(View.GONE);
    }

    private void showSwing() {
        connecting.setVisibility(View.GONE);
        leftChild.setVisibility(View.VISIBLE);
        rightChild.setVisibility(View.VISIBLE);
        swing.setVisibility(View.VISIBLE);
    }

    @Override
    public void onServiceStateChanged(int state) {
        if (state == BluetoothState.STATE_CONNECTED) {
            connectingToast.cancel();
            Toast.makeText(this, R.string.connected, Toast.LENGTH_SHORT).show();
            showSwing();
        } else if (state == BluetoothState.STATE_CONNECTING) {
            connectingToast = Toast.makeText(this, R.string.connecting, Toast.LENGTH_SHORT);
            connectingToast.show();
        }
    }

    @Override
    public void onDataReceived(byte[] data, String message) {
        if (message.startsWith("STAT:")) {
            String[] statuses = message.substring(4).split(";");
            if (statuses[2].equals("0")) {

            }
        }
    }
}
