#ifndef LibBuzzer_h
#define LibBuzzer_h


#define  TIME_BUZZER_LOW 	0
#define  TIME_BUZZER_HIGH	0



class LibBuzzer{

	public:
		
		LibBuzzer(int iPBuzzer);
		~LibBuzzer(){};
		
		void setBuzzer(int iPBuzzer);
		int  getBuzzer();
		
		void activarBuzzer(int Value);
		void desactivarBuzzer();
		
	private:
		
		int iPBuzzer;

		//Nos Permite controlar el sonido de Alarma.
		unsigned long ulBuzzerSonar;
	
};

#endif