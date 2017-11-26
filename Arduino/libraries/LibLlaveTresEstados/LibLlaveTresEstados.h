#ifndef LibLlaveTresEstados_h
#define LibLlaveTresEstados_h

enum estadoActual {
  neutro,
  izquierda,
  derecha
};

enum combinacion {
  ninguna,
  ddd, // derecha - derecha - derecha
  ii // izquierda - izquierda
};

class LibLlaveTresEstados {

	public:
		LibLlaveTresEstados(int pinIzquierda, int pinDerecha);
		estadoActual leerEstado();
		combinacion leerCombinacion();
	private:
		int pinIzq;
		int pinDer;
};

#endif
