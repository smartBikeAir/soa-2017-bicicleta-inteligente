
#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#include "LibUltrasonic.h"

LibUltrasonic::LibUltrasonic(int tP, int eP) {
   trigPin=tP;
   echoPin=eP;
   pinMode(trigPin, OUTPUT);
   pinMode(echoPin, INPUT);
}

int LibUltrasonic::checkDistance() {

  if (shouldClearTrigPin == true) {
     digitalWrite(trigPin, LOW);
     shouldClearTrigPin = false;

     // Guardo una marca de tiempo que me dice
     // cuándo "se comenzó a limpiar" el trigPin.
     timestamp = micros();
  }

  // Chequeamos si pasó el tiempo necesario para limpiar el TrigPin.
  if ((micros() - timestamp) >= TRIG_PIN_CLEANING_DURATION) {

     if (shouldSetTrigPinHigh == true) {
        digitalWrite(trigPin, HIGH);
        shouldSetTrigPinHigh = false;

        // Guardo una marca de tiempo que me dice
        // cuándo se puso en alto el trigPin.
        timestamp = micros();
     }

     // Chequeamos si pasó el tiempo necesario para poder bajar al TrigPin.
     if ((millis() - timestamp) >= TRIG_PIN_HIGH_STATE_DURATION) {
         digitalWrite(trigPin, LOW);

         duration = pulseIn(echoPin, HIGH);

         if (duration == 0) { // timeout. No se pudo determinar la duración.
            shouldClearTrigPin = true;
            shouldSetTrigPinHigh = true;
            return UNDEFINED_DISTANCE;
         }

         // Calculamos la distancia.
         distance = duration * 0.034/2;

         // La "tarea ppal" terminó, resetteamos variables.
         shouldClearTrigPin = true;
         shouldSetTrigPinHigh = true;

         #ifdef VERBOSE
         Serial.print("Distancia: ");
         Serial.println(distance);
         #endif
         return distance;
     }

     #ifdef VERBOSE
     Serial.print("UNDEFINED_DISTANCE");
     #endif

     return UNDEFINED_DISTANCE;
  }
}
