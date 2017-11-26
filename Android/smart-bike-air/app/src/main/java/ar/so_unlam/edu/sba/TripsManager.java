package ar.so_unlam.edu.sba;

import android.util.Log;

import java.util.ArrayList;

public class TripsManager {

    private static TripsManager instance;
    private ArrayList<Trip> trips;

    private TripsManager() {
        trips = new ArrayList<Trip>();
    }

    public static synchronized TripsManager getInstance() {
        if (instance == null) {
            instance = new TripsManager();
        }
        return instance;
    }

    public ArrayList<Trip> getTrips() {
        return trips;
    }

    /*
        Agrega un Trip.
        Por el momento solo existe durante la sesión
        (no hay persistencia).
        Luego, puede ser consultado invocando a
        ArrayList<Trip> getTrips()
     */
    public void saveTrip(Trip trip) {
        trips.add(trip);

        // Lo siguiente me sirve para saber cuántos Trips tengo creados en la sesión actual.
        // Imprimimos el estado del array trips.
        Log.d("TripsManager", "Trips de la sesión actual:" + getTrips().toString());
    }
}
