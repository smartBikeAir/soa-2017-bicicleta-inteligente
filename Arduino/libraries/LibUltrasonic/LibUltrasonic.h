#ifndef LibUltrasonic_h
#define LibUltrasonic_h

#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

// Tiempos mínimos requeridos para el funcionamiento del sensor HC-SR04.
#define TRIG_PIN_CLEANING_DURATION 2 // microsegundos
#define TRIG_PIN_HIGH_STATE_DURATION 10 // microsegundos

#define UNDEFINED_DISTANCE -1

/**
 * Referida al sensor ultrasónico HC-SR04.
 */
class LibUltrasonic {
  public:

    /**
     * Construye sensor ultrasónico.
     * @param tP: número de pin digital para Trig.
     * @param eP: número de pin digital para Echo.
     */
    LibUltrasonic(int tP, int eP);

    /**
     * Evalua a qué distancia se encuentra el objeto en cuestión.
     * @return: la distancia en cm (de 2cm a 400 cm) o
     * UNDEFINED_DISTANCE si no se encuentra disponible.
     */
    int checkDistance();

  private:

    int trigPin;
    int echoPin;
    long duration;
    long distance;

    // Marca de tiempo en microsegundos.
    unsigned long timestamp;

    bool shouldClearTrigPin = true;
    bool shouldSetTrigPinHigh = true;
};

#endif
