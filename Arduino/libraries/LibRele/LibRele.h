#ifndef LibRele_h
#define LibRele_h

// se toma como constante el radio de una rueda normal de bicicleta

// Definimos el constructor de la clase se debe pasar los PIN correspondientes.

class  LibRele{

	public:
		LibRele(int iPin);
	
		void openRele();
		void closeRele();
		
	private:
		
		int iPin;
};

#endif
		
