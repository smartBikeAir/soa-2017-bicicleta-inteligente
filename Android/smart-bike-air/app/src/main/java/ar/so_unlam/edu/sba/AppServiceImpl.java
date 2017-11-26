package ar.so_unlam.edu.sba;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by A646241 on 11/11/2017.
 */

public class AppServiceImpl implements AppService {

    private Thread connectedThread = null;

    private static final AtomicInteger atomicInteger = new AtomicInteger(0);

    private static final AppServiceImpl INSTANCE = new AppServiceImpl();

    private int deviceStatus = AppConstants.STOPPED;

    private  int mapSatus = AppConstants.MAP_STATUS_HIDDEN;

    private AppServiceImpl() {}

    public static AppServiceImpl getInstance() {
        return INSTANCE;
    }

    public AtomicInteger getAtomicInteger() {
        return AppServiceImpl.atomicInteger;
    }


    public Thread getConnectedThread() {
        return connectedThread;
    }

    public void setConnectedThread(Thread connectedThread) {
        this.connectedThread = connectedThread;
    }


    public int getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(int deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    @Override
    public int getMapStatus() {
        return mapSatus;
    }

    public void setMapStatus(int mapSatus) {
        this.mapSatus = mapSatus;
    }

    private void errorExit(Context context, String title, String message){
        Toast.makeText(context, title + " - " + message, Toast.LENGTH_LONG).show();
        ((Activity)context).finish();
    }

}
