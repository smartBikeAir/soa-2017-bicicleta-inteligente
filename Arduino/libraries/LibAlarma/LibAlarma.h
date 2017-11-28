#ifndef LibAlarma_h
#define LibAlarma_h

#include "LibBuzzer.h"
#define CICLO_ALARMA 100

class LibAlarma {
  public:

  /**
   * Construye instancia de alarma.
   * @param iPBuzzer: número de pin digital asociado.
   */
	LibAlarma(int iPBuzzer);

	~LibAlarma(){};

	int  activarAlarmaSonando();
	int  desactivarAlarmaSonando();
	void sonarAlarmaAlternada();

  private:

	LibBuzzer *Buzzer;

	// control de los estados de la alarma
	int alarmDutyCycle;


};

#endif
