package ar.so_unlam.edu.sba;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.content.DialogInterface;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static ar.so_unlam.edu.sba.AppConstants.RECIEVE_MESSAGE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ArduinoCon_BT";

    private static final String BT_DEVICE_NAME = "SBA-2017"; // Default to find Bluetooth Name SBA-2017

    private static final int REQUEST_ENABLE_BT = 1;

    private boolean isSBA2017On = false;

    private boolean isBluetoothConnected = false;

    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothSocket bluetoothSocket = null;
    private boolean isDiscoverOn = false;

    private List<String> mArrayAdapter = new ArrayList<>();

    private ConnectedThread connectedThread;

    private static final AppService APP_SERVICE = AppServiceImpl.getInstance();

    private String address = null;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final UUID APP_UUID;

    static {
        APP_UUID = UUID.randomUUID();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = MainActivity.this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
      /*  if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }*/

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();		// get Bluetooth adapter
        checkBTState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(MainActivity.this.getClass().toString(), "Continue Bluetooth Connection!");

        getBoundedDevices();

        isDiscoverOn = false;
        if (isBluetoothConnected && !isSBA2017On) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            mArrayAdapter.clear();
            registerReceiver(btReceiver, filter);
            isDiscoverOn = bluetoothAdapter.startDiscovery();
        }

        if(isDiscoverOn) {
            return;
        }
        processConnection();
    }

    private void processConnection() {
        // Set up a pointer to the remote node using its address.
        if(isDiscoverOn) {
            unregisterReceiver(btReceiver);
        }
        bluetoothAdapter.cancelDiscovery();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(APP_UUID);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Establish the connection.  This will block until it connects.
        try {
            bluetoothSocket.connect();
            Log.d(TAG, "...Connection ok...");
        } catch (IOException e) {
            try {
                // If fail connection, so it will try native connection.
                BluetoothSocket tmp = device.createRfcommSocketToServiceRecord(APP_UUID);;
                Class<?> clazz = tmp.getRemoteDevice().getClass();
                Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[] {Integer.valueOf(1)};
                bluetoothSocket = (BluetoothSocket) m.invoke(tmp.getRemoteDevice(), params);
                Thread.sleep(500);
                bluetoothSocket.connect();

            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e2) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e2);
                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }



        connectedThread = new ConnectedThread(bluetoothSocket);
        APP_SERVICE.setConnectedThread(connectedThread);
        connectedThread.start();

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(bluetoothAdapter == null) {
            errorExit("Fatal Error", "Bluetooth are not supported!");
        } else {
            if (bluetoothAdapter.isEnabled()) {
                isBluetoothConnected = true;
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth

                Log.d(TAG, "bluetoothAdapter no está habilitado");

                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    // This method verifies If there are paired devices
    public void getBoundedDevices() {
        Log.d(TAG, "getBoundedDevices()");
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                if (device.getName().equals(BT_DEVICE_NAME)) {
                    isSBA2017On = true;
                    address = device.getAddress();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // this method checks when the enableBtIntent activity finished
        isBluetoothConnected = false;
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_CANCELED) {
                // the Bluetooth Enable Request was rejected
                Log.d(TAG, "...Bluetooth OFF...");
            } else if(resultCode == RESULT_OK) {
                // the Bluetooth Enable Request was accepted
                isBluetoothConnected = true;
                Log.d(TAG, "...Bluetooth ON...");
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver btReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device, it sends ACTION_FOUND
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                Log.d(TAG, "ACTION_STATE_CHANGED!");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                // Device is now connected
                Log.d(TAG, "ACTION_DISCOVERY_STARTED!");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // Done searching
                Log.d(TAG, "ACTION_DISCOVERY_FINISHED!");
                processConnection();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d(TAG, "Continue Bluetooth Connection!");
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                if (device.getName().equals(BT_DEVICE_NAME)) {
                    isSBA2017On = true;
                    address = device.getAddress();
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

}
