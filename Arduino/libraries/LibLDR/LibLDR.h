#ifndef LibLDR_h
#define LibLDR_h

class LibLDR{

	public:

		/**
		 * Construye una instancia de LibLDR.
		 * @param iPin: número de pin análogico asociado.
		 */
		LibLDR(int iPin);

		// @return: me devuelve si hay o no luz ambiente.
		bool hayLuz();

	private:
		int iPin;
		int minVal;
		int maxVal;
		int val;
};

#endif
