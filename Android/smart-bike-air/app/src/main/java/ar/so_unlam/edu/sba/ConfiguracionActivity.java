package ar.so_unlam.edu.sba;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by irmab on 7/11/2017.
 */

public class ConfiguracionActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new FragmentConfiguracion())
                .commit();
    }


}
