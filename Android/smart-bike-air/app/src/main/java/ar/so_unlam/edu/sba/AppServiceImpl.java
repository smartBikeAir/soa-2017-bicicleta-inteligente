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

    private  String alarmSatus = AppConstants.DEACTIVATE_ALARM;

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

    public String getAlarmaStatus() {
        return alarmSatus;
    }

    public void setAlarmaStatus(String alarmSatus) {
        this.alarmSatus = alarmSatus;
    }
}
