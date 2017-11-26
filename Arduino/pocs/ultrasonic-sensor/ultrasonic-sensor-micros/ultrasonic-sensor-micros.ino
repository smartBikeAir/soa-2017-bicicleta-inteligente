/*
* Objetivo: medir distancias usando un sensor ultrasónico,
* Para simular delays, utilizo la función micros().
* Para ejecutarlo, podemos armar el circuito como se
* indica en: http://howtomechatronics.com/tutorials/arduino/ultrasonic-sensor-hc-sr04/
*/

// Si definimos VERBOSE, entonces se van a compilar las sentencias asociadas al DEBUG 
// (en este Sketch serían las que utilizan al Monitor Serie).
// Comentar la siguiente línea para evitar el modo VERBOSE.
#define VERBOSE

// Pines digitales utilizados.
#define TRIG_PIN 9
#define ECHO_PIN 10

// Tiempos mínimos requeridos para el funcionamiento del sensor HC-SR04.
#define TRIG_PIN_CLEANING_DURATION 2 // microsegundos
#define TRIG_PIN_HIGH_STATE_DURATION 10 // microsegundos

// Estos booleanos estan para que ciertas líneas solo se llamen una vez. //TODO: Chequear si realmente hacen falta.
boolean shouldClearTrigPin = true;
boolean shouldSetTrigPinHigh = true;

unsigned long timestamp; // microsegundos

long duration;
int distance;

void setup() {
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);

  #ifdef VERBOSE
  Serial.begin(9600);
  #endif
}

void loop() {
  if (shouldClearTrigPin == true) {
     digitalWrite(TRIG_PIN, LOW);
     shouldClearTrigPin = false;

     // Guardo una marca de tiempo que me dice
     // cuándo "se comenzó a limpiar" el trigPin.
     timestamp = micros();
  }

  // Chequeamos si pasó el tiempo necesario para limpiar el TrigPin.
  if ((micros() - timestamp) >= TRIG_PIN_CLEANING_DURATION) {

     if (shouldSetTrigPinHigh == true) {
        digitalWrite(TRIG_PIN, HIGH);
        shouldSetTrigPinHigh = false;

        // Guardo una marca de tiempo que me dice
        // cuándo se puso en alto el trigPin.
        timestamp = micros();
     }

     // Chequeamos si pasó el tiempo necesario para poder bajar al TrigPin.
     if ((millis() - timestamp) >= TRIG_PIN_HIGH_STATE_DURATION) {
         digitalWrite(TRIG_PIN, LOW);
         
         duration = pulseIn(ECHO_PIN, HIGH);

         // Calculamos la distancia.
         distance= duration * 0.034/2;

         // La "tarea ppal" terminó, resetteamos variables.
         shouldClearTrigPin = true;
         shouldSetTrigPinHigh = true;

         #ifdef VERBOSE
         Serial.print("Distancia: ");
         Serial.println(distance);
         #endif
     }
  }
}

