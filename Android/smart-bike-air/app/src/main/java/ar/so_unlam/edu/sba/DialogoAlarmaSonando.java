package ar.so_unlam.edu.sba;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class DialogoAlarmaSonando extends Activity {

    private Button offAlarm;

    private static final AppService APP_SERVICE = AppServiceImpl.getInstance();

    private ConnectedThread connectedThread = ((ConnectedThread)APP_SERVICE.getConnectedThread());

    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogo_alarma_sonando);

        offAlarm = (Button)findViewById(R.id.turn_Alarm_Off);

        offAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectedThread.write(AppConstants.turnAlarmOff+"\n");
                finish();
            }
        });

    }

    public void onResume() {
        super.onResume();

        connectedThread.setHandler(handler);

    }
}
