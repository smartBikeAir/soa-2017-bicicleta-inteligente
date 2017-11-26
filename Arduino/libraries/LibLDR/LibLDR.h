#ifndef LibLDR_h
#define LibLDR_h

class LibLDR{

	public:
		LibLDR(int iPin);
	
		bool hayLuz();
		
	private:
		int iPin;
		int minVal;
		int maxVal;
		int val;
		
};

#endif
		
