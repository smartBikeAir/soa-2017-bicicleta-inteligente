#ifndef LibVelocidad_h
#define LibVelocidad_h
/*
 * Cuenta cantidad de vueltas por minuto.
 * Muestra por serial: Vueltas: v - Tiempo medición: t - RPM: r
 *
 * Rueda de 26" de ISO 50-559 (559mm diametro)
 * Max speed: 50 km/h (474,522 RPM) - Read time: 100 ms
 * Min speed: 3 km/h  (28,471 RPM)  - Read time: 2000 ms
 *
 * Input: digital read A0
 *
 * Sensor: 3144 402 digital.
 *        G: GND
 *        R: 5v
 *        Y: A0
 */

#define VERBOSE

#define MIN_TIME 100 // se define la maxima velocidad medible. aprox 50 km/h
#define RESET_TIME 2000 // limita la velicidad minima medible 3km
#define MEDIDA_RADIO 0.556 //Constante 0.559 medida estandar del radio de una rueda estandar.
#define AJUSTE_UNIDADES 0.376991118 //Constante de ajuste de unidades de medidas
#define MINUTOS 60


class LibVelocidad {
  public:

	LibVelocidad(int iPin);

	// Definimos el destructor de la clase.

	~LibVelocidad(){};


	unsigned long medirVelocidad();

  private:

    // Pin asignado
    int iPin;

	unsigned long startTime;
	unsigned long currentTime;
	unsigned long lastResetTime;
	unsigned long timeSinceLastRead;
	unsigned long uVelocidad;

	int currentValue;
	int lastValue;
	int revolutions;
	float currentRPM;

};

#endif
