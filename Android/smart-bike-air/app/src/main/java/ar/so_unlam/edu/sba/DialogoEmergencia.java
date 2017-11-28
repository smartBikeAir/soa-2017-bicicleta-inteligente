package ar.so_unlam.edu.sba;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by irmab on 14/11/2017.
 */

public class DialogoEmergencia extends Activity {

    private Button startCall;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogo_emergencia);

        startCall = (Button)findViewById(R.id.startCall);

        startCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DialogoEmergencia", "SIN PERMISO");
            }
        });
    }



    public void llamadaTelefono() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:962849347"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        startActivity(intent);
    }
}
