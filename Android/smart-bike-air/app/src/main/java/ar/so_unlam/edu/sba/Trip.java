package ar.so_unlam.edu.sba;

public class Trip {

    private long duration; // Duración del viaje en segundos.

    public long getDuration() {
        return duration;
    }

    Trip(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "\n" +
                super.toString() +
                ", Duración: " + getDuration() + " seg";
    }
}
