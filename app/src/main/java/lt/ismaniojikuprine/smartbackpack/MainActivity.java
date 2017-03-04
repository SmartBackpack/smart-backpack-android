package lt.ismaniojikuprine.smartbackpack;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity implements BluetoothSPP.OnDataReceivedListener, BluetoothSPP.BluetoothStateListener, BluetoothSPP.BluetoothConnectionListener {

    private BluetoothSPP bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener(this::onConnectButtonClicked);

        bluetooth = new BluetoothSPP(this);
        bluetooth.setBluetoothStateListener(this);
        bluetooth.setBluetoothConnectionListener(this);
        bluetooth.setOnDataReceivedListener(this);
    }

    private void onConnectButtonClicked(View v) {
        if (bluetooth.isBluetoothEnabled()) {
            Intent intent = new Intent(this, DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE && resultCode == Activity.RESULT_OK) {
            bluetooth.setupService();
            bluetooth.startService(BluetoothState.DEVICE_OTHER);
            bluetooth.connect(data);
        }
    }

    @Override
    public void onDataReceived(byte[] data, String message) {
        Log.e("Received", "Received");
    }

    @Override
    public void onServiceStateChanged(int state) {
        if (state == BluetoothState.STATE_CONNECTED) {
            Log.e("STATE", "CONNECTED");
        }
        else if (state == BluetoothState.STATE_CONNECTING) {
            Toast.makeText(this, R.string.connecting, Toast.LENGTH_SHORT).show();
            Log.e("STATE", "CONNECTING");
        }
        else if (state == BluetoothState.STATE_LISTEN) {
            Log.e("STATE", "LISTEN");
        }
        else if (state == BluetoothState.STATE_NONE) {
            Log.e("STATE", "NONE");
        }
    }

    @Override
    public void onDeviceConnected(String name, String address) {
        Log.e("DEVICE", "CONNECTED");
        Toast.makeText(this, R.string.connected, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceDisconnected() {
        Log.e("DEVICE", "DISCONNECTED");
        Toast.makeText(this, R.string.disconnected, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceConnectionFailed() {
        Log.e("DEVICE", "CONNECTION FAILED");
        Toast.makeText(this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
    }
}
