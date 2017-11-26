#ifndef LibLuzDeGiro_h
#define LibLuzDeGiro_h

/*
Esta clase me permite encender las luces de giro,
la izquierda o la derecha (nunca las dos juntas ya
que no soporta balisas).
*/
class LibLuzDeGiro {

	public:
		LibLuzDeGiro(int pinIzquierda, int pinDerecha);

		void encenderLuzIzquierda();
		void encenderLuzDerecha();
		void apagarLuces();

	private:
		int pinIzq;
		int pinDer;
		unsigned long ultimoCambio;
		void alternarEstadoDePin(int pin);
};
#endif
