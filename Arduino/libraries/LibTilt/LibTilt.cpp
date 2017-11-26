
#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#include "LibTilt.h"

LibTilt::LibTilt(int p) {
   pin=p;
   value=0;
   pinMode(pin, INPUT);
}

bool LibTilt::isTilted() {
   if(digitalRead(pin) == HIGH) {
      value +=1;
    } else {
       if(value > 0) {
          value--;
       }
    }
    //Serial.println(value);
    return value > VALUE_TILT;
}
