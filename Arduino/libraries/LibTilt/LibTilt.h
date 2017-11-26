#ifndef LibTilt_h
#define LibTilt_h

#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#define VALUE_TILT   100

class LibTilt {
  public:

    /**
     * Construye sensor tilt.
     * @param p: número de pin digital asociado.
     */
    LibTilt(int p);

    bool isTilted();

  private:
    int pin;
    int value;
};

#endif
