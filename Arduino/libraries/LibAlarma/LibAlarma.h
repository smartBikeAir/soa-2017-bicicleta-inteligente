#ifndef LibAlarma_h
#define LibAlarma_h

#include "LibBuzzer.h"

#define VERBOSE

#define CICLO_ALARMA 100


class LibAlarma {
  public:
	
	// Definimos el constructor de la clase se debe pasar los PIN correspondientes.
	LibAlarma(int iPBuzzer);
  	// Definimos el destructor de la clase.
	
	~LibAlarma(){};

	int  activarAlarmaSonando();	
	int  desactivarAlarmaSonando();
	
	void sonarAlarmaAlternada();
	
  private:

    // Pin asignado a cada elemento de la alarma
	LibBuzzer *Buzzer;
 
	// control de los estados de la alarma	
	int alarmDutyCycle;
	
    
};

#endif
