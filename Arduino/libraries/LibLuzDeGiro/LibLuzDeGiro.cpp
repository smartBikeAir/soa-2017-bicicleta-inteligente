#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#include "libLuzDeGiro.h"

LibLuzDeGiro::LibLuzDeGiro(int pinIzquierda, int pinDerecha){
	this->pinIzq = pinIzquierda;
  this->pinDer = pinDerecha;
  this->ultimoCambio = 0;
  pinMode(pinIzquierda, OUTPUT);
  pinMode(pinDerecha, OUTPUT);
}

void LibLuzDeGiro::encenderLuzIzquierda() {
    alternarEstadoDePin(this->pinIzq);
}

void LibLuzDeGiro::encenderLuzDerecha() {
    alternarEstadoDePin(this->pinDer);
}

void LibLuzDeGiro::apagarLuces() {
  digitalWrite(this->pinIzq, LOW);
  digitalWrite(this->pinDer, LOW);
}

void LibLuzDeGiro::alternarEstadoDePin(int pin) {
  // Si pasaron al menos 500 ms desde el ultimoCambio de estado,
  // alternamos el estado del pin (de LOW a HIGH, o viceversa).
  if ((millis() - ultimoCambio) > 500) {

      // Alternamos estado, de HIGH a LOW, o de LOW a HIGH,
      // según corresponda.
      digitalWrite(pin, !digitalRead(pin));

      // Guardamos una marca de tiempo que representa el último
      // cambio de estado del pin.
      ultimoCambio = millis();
  }
}
