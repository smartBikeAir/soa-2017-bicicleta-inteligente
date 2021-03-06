#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#include "LibAlarma.h"

LibAlarma::LibAlarma(int iPBuzzer){
	 this->Buzzer = new LibBuzzer(iPBuzzer);
	 this->alarmDutyCycle = 0;
}

int  LibAlarma::activarAlarmaSonando(){
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
