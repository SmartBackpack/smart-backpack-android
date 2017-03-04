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

public class MainActivity extends AppCompatActivity implements BluetoothSPP.OnDataReceivedListener, BluetoothSPP.BluetoothStateListener {

    private BluetoothSPP bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener(this::onConnectButtonClicked);

        Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener(this::onSendButtonClicked);

        bluetooth = new BluetoothSPP(this);
        bluetooth.setBluetoothStateListener(this);
        bluetooth.setOnDataReceivedListener(this);
    }

    private void onConnectButtonClicked(View v) {
        if (bluetooth.isBluetoothEnabled()) {
            bluetooth.setupService();
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

    private void onSendButtonClicked(View view) {
        bluetooth.send("HELLO WORLD", true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE && resultCode == Activity.RESULT_OK) {
            bluetooth.connect(data);
        }
    }

    @Override
    public void onDataReceived(byte[] data, String message) {
        Log.e("Received", message);
    }

    @Override
    public void onServiceStateChanged(int state) {
        if (state == BluetoothState.STATE_CONNECTED) {
            Toast.makeText(this, R.string.connected, Toast.LENGTH_SHORT).show();
        }
        else if (state == BluetoothState.STATE_CONNECTING) {
            Toast.makeText(this, R.string.connecting, Toast.LENGTH_SHORT).show();
        }
    }
}
