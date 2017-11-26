#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#include "LibRele.h"


	// Definimos el constructor de la clase se debe pasar los PIN correspondientes.
LibRele::LibRele(int iPin){

	this->iPin = iPin;
	pinMode(iPin, OUTPUT);
}

void LibRele::openRele(){
    digitalWrite(this->iPin, LOW);
}

void LibRele::closeRele(){
    digitalWrite(this->iPin, HIGH);
}
