package ar.so_unlam.edu.sba;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by A646241 on 11/11/2017.
 */

public interface AppService {

    AtomicInteger getAtomicInteger();

    Thread getConnectedThread();

    void setConnectedThread(Thread connectedThread);

    int getDeviceStatus();

    void setDeviceStatus(int deviceStatus);

    int getRealTimeStatus();

    void setRealTimeStatus(int realTimeSatus);

}
