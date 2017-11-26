#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#include "LibLDR.h"

bool bEstado;

	// Definimos el constructor de la clase se debe pasar los PIN correspondientes.
LibLDR::LibLDR(int iPin){

	this->iPin = iPin;
  this->minVal = 500;
  this->maxVal = 700;
  pinMode(iPin, INPUT);
}

bool LibLDR::hayLuz(){

	  val = analogRead(iPin);

		if(val < minVal) bEstado = true; // cierro el rele (enciendo la tira de LED)
		if(val > maxVal) bEstado = false;

		return bEstado;

}
