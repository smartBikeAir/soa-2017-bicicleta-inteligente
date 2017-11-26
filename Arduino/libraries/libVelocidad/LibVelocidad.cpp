#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#include "LibVelocidad.h"

// se toma como constante el radio de una rueda normal de bicicleta

#define VERBOSE

	// Definimos el constructor de la clase se debe pasar los PIN correspondientes.
LibVelocidad::LibVelocidad(int iPin){

	this->startTime =  millis();
	this->iPin = iPin;
	this->currentTime = 0;
	this->lastResetTime = 0;
	this->timeSinceLastRead = 0;
	this->uVelocidad = 0;

	this->currentValue = 0;
	this->lastValue = 0;
	this->revolutions = 0;
	this->currentRPM = 0.0;

}

unsigned long  LibVelocidad::medirVelocidad(){


	// Time since last read
	  timeSinceLastRead = millis() - currentTime;
	  uVelocidad = 0;
	  // Read
	  currentTime = millis();
	  currentValue = digitalRead(iPin);

	  // Check if should count revolution
	  if(lastValue > currentValue && lastResetTime >= MIN_TIME) {
		revolutions++;
	  }

	  // Reset RPM counter (after 2 second)
	  if(currentTime - lastResetTime > RESET_TIME) {
		/*Serial.println("==== RESET ====");
		Serial.print("Vueltas: ");
		Serial.println(revolutions);

		Serial.print("RPM: ");*/
		currentRPM = (float)(revolutions*MINUTOS)/2;
		//Serial.println(currentRPM);



		/*Serial.print("Velocidad: ");
		Serial.println(MEDIDA_RADIO*currentRPM*AJUSTE_UNIDADES);*/
		uVelocidad = MEDIDA_RADIO*currentRPM*AJUSTE_UNIDADES;

		currentTime = 0;
		revolutions = 0;
		lastResetTime = millis();

		return uVelocidad;
	  }

	  lastValue = currentValue;

	  return uVelocidad;

}
