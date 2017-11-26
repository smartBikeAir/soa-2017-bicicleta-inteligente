#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#include "LibRele.h"


	// Definimos el constructor de la clase se debe pasar los PIN correspondientes.
LibRele::LibRele(int iPin){

	this->iPin = iPin;
	estaAbierto = true;
	pinMode(iPin, OUTPUT);
}

void LibRele::openRele(){
	estaAbierto = true;
    digitalWrite(this->iPin, LOW);
}

void LibRele::closeRele(){
	estaAbierto = false;
    digitalWrite(this->iPin, HIGH);
}

bool LibRele::isOpen() {
	return estaAbierto;
}

bool LibRele::isClosed() {
	return !estaAbierto;
}
