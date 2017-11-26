#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#include "LibAlarma.h"

#define VERBOSE

	// Definimos el constructor de la clase se debe pasar los PIN correspondientes.
LibAlarma::LibAlarma(int iPBuzzer){
	
	this->Buzzer = new LibBuzzer(iPBuzzer);

	this->alarmDutyCycle = 0;
	
}

		
int  LibAlarma::activarAlarmaSonando(){

	// Tenemos que cumplir 2 condiciones para que la alarma comience a sonar
	// estar en modo alarma y que la misma se encuentre activa
	
		if(alarmDutyCycle >= CICLO_ALARMA) {
			alarmDutyCycle = 50;
		} else {
			alarmDutyCycle += 5;
		}
	  
		Buzzer->activarBuzzer(alarmDutyCycle);

}	
	
int  LibAlarma::desactivarAlarmaSonando(){
	
		alarmDutyCycle = 0;
		Buzzer->desactivarBuzzer();
}	
	
void LibAlarma::sonarAlarmaAlternada() {
	Buzzer->sonarAlarmaAlternada();
}
