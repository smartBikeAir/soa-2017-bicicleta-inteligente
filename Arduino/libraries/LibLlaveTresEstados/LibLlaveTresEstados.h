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

    /**
     * Construye una instancia de LibLDR.
     * @param pinIzquierda: número de pin digital asociado a la posición izquierda.
     * @param pinDerecha: número de pin digital asociado a la posición derecha.
     */
		LibLlaveTresEstados(int pinIzquierda, int pinDerecha);

    // @return: me devuelve si la llave se encuentra en la posición neutro,
    // izquierda o derecha.
		estadoActual leerEstado();

    // @return: me devuelve la última combinación detectada.
    // Podría ser ddd, ii o ninguna.
		combinacion leerCombinacion();

	private:
		int pinIzq;
		int pinDer;
    void reiniciarCombinacionDerecha();
    void reiniciarCombinacionIzquierda();
};

#endif
