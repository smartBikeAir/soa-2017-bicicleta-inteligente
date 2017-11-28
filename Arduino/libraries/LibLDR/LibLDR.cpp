#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#include "LibLDR.h"

bool bEstado = false;

LibLDR::LibLDR(int iPin){

	this->iPin = iPin;

  // Umbrales definidos para utilizar histéris.
  this->minVal = 500;
  this->maxVal = 700;

  pinMode(iPin, INPUT);
}

bool LibLDR::hayLuz() {

	  val = analogRead(iPin);

		if(val < minVal) bEstado = true;
		if(val > maxVal) bEstado = false;

		return bEstado;
}
