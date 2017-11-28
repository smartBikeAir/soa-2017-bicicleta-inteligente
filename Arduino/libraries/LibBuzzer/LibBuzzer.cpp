#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif


#include "LibBuzzer.h"

LibBuzzer::LibBuzzer(int iPBuzzer){

	this->iPBuzzer = iPBuzzer;
	this->frequency = 3500;
	this->cycleCounter = 0;

	ulBuzzerSonar = 0;
	pinMode(iPBuzzer, OUTPUT);
}

void LibBuzzer::activarBuzzer(int Value){
	 analogWrite(iPBuzzer, Value);
}

void LibBuzzer::desactivarBuzzer(){
	analogWrite(iPBuzzer,0);
	noTone(iPBuzzer);
}

void LibBuzzer::sonarAlarmaAlternada() {
	tone(iPBuzzer, frequency, 100);
	if(cycleCounter > 200) {
		// Momento de cambiar de tono
		cycleCounter = 0; // reseteo el contador
		// Cambio la frecuencia
		if(frequency == 2000) {
			frequency = 3500;
		} else {
			frequency = 2000;
		}
	} else {
		  cycleCounter++;
	}
}
