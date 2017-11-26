#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif


#include "LibBuzzer.h"

LibBuzzer::LibBuzzer(int iPBuzzer){

	this->iPBuzzer = iPBuzzer;

	ulBuzzerSonar = 0;

	pinMode(iPBuzzer, OUTPUT);

}

void LibBuzzer::setBuzzer(int iPBuzzer){

	this->iPBuzzer = iPBuzzer;


}

int LibBuzzer::getBuzzer(){

	return this->iPBuzzer;

}

void LibBuzzer::activarBuzzer(int Value){

	//Guardo una marca de tiempo para controlar el sonido del buzzer
	analogWrite(iPBuzzer, Value);

}

void LibBuzzer::desactivarBuzzer(){

	analogWrite(iPBuzzer,0);
}
