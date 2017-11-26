/*
* Objetivo: medir distancias usando un sensor ultrasónico,
* Para ejecutarlo, podemos armar el circuito como se
* indica en: http://howtomechatronics.com/tutorials/arduino/ultrasonic-sensor-hc-sr04/
*/

#include <UltrasonicSensor.h>

#define MIN_DISTANCE_TO_OBJECT 50

UltrasonicSensor ultrasonicSensor(9, 10); // (Trig PIN, Echo PIN)
    
void setup() {
  Serial.begin(9600);
}

void loop() {
  
  long distanceToObject = ultrasonicSensor.checkDistance();
  if (distanceToObject != UNDEFINED_DISTANCE && distanceToObject < MIN_DISTANCE_TO_OBJECT) {
     #ifdef VERBOSE
     Serial.println("¡TENEMOS UN OBJETO MUY CERCA!");
     #endif
  }
}

