#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#include "LibLlaveTresEstados.h"

int ledAlarma=13; // Asignación de la salida ditital para el encendido del led lado izquierdo. // Solo para prueba, visualización de alarma armada
unsigned long INICIO_PD;
unsigned long INICIO_PI;
unsigned long INICIO_DERECHA;
unsigned long INICIO_IZQUIERDA;
unsigned long PULSOS_DERECHA;
unsigned long PULSOS_IZQUIERDA;
unsigned long ACTUAL;
unsigned int CLAVE_ARMAR_ALARMA=3;
unsigned int CLAVE_DESARMAR_ALARMA=2;
unsigned int TIEMPO_INGRESO_EXCEDIDO=4000;
unsigned long INTERVALO_TIEMPO=300; // Variable que establece el tiempo necesario para el cambio de estado del led. El objetivo es que                               // el mismo encienda y apague (parpadeo)
unsigned int EFECTO_REBOTE=500;

LibLlaveTresEstados::LibLlaveTresEstados(int pinIzquierda, int pinDerecha) {
	this->pinIzq = pinIzquierda;
  this->pinDer = pinDerecha;
  pinMode(pinDerecha, INPUT);
  pinMode(pinIzquierda, INPUT);
  pinMode(ledAlarma, OUTPUT);
}

estadoActual LibLlaveTresEstados::leerEstado() {

    // Para la funcionalidad luz de giro (encender/apagar) no es necesario
    // tener en cuenta el rebote ocasionado por la llave. Éste existe pero
    // su impacto no altera a la funcionalidad.
    if (digitalRead(this->pinDer)==LOW) {
        return derecha;
    } else if (digitalRead(this->pinIzq)==LOW) {
        return izquierda;
    } else {
        return neutro;
    }
}

combinacion LibLlaveTresEstados::leerCombinacion() {

// Lado Derecho

if (digitalRead(this->pinDer)==LOW) { // de acuerdo a si el pulsador es NA o NC se pone alto o bajo
    if (PULSOS_DERECHA==0) {
       INICIO_DERECHA=millis();
       INICIO_PD=millis();
       PULSOS_DERECHA=1;
     }
     else {
         // EVITAR EFECTO REBOTE EN EL PULSADOR O SELECTOR //
        if ( millis() > (ACTUAL + EFECTO_REBOTE)) {
           PULSOS_DERECHA++;
        }
     }

//// Funcion de parpadeo - Inicio ////////////////
     ACTUAL=millis();

     if (ACTUAL < INTERVALO_TIEMPO + INICIO_PD) {// Primer Estado hasta tanto se cumpla el tiempo establecido
        //digitalWrite(RIGHT_LED, HIGH);
     }
     else {
        //digitalWrite(RIGHT_LED, LOW);
        if (ACTUAL > ((INTERVALO_TIEMPO * 2)+ INICIO_PD)) {
            INICIO_PD= millis();
        }
     }
//// Funcion de parpadeo - fin ////////////////

 }
 else {
      //digitalWrite(RIGHT_LED, LOW);
 }

//// Analisis de combinación - Inicio ////////////

if (PULSOS_DERECHA==CLAVE_ARMAR_ALARMA) {
    // ARMAR ALARMA
    digitalWrite(ledAlarma, HIGH);  //solo para visualizar un cambio
    return ddd;
}
else {
    // Resetear combinación
    if ((PULSOS_DERECHA > CLAVE_ARMAR_ALARMA) or (millis() > (INICIO_DERECHA + TIEMPO_INGRESO_EXCEDIDO)))
    {
      PULSOS_DERECHA=0;
    }
}

////// Analisis de combinación - Fin ///////////////

///////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////

// Lado Izquierdo

if (digitalRead(this->pinIzq)==LOW) { // de acuerdo a si el pulsador es NA o NC se pone alto o bajo

  if (PULSOS_IZQUIERDA==0) {
      INICIO_IZQUIERDA=millis();
      INICIO_PI=millis();
       PULSOS_IZQUIERDA=1;
   }
  else {
   // EVITAR EFECTO REBOTE EN EL PULSADOR O SELECTOR //
      if ( millis() > (ACTUAL + EFECTO_REBOTE)) {
          PULSOS_IZQUIERDA++;
      }
   }
  //PULSOS_IZQUIERDA++;

//// Funcion de parpadeo - Inicio ////////////////

  ACTUAL=millis();
  if (ACTUAL < INTERVALO_TIEMPO + INICIO_PI) { // Primer Estado hasta tanto se cumpla el tiempo establecido
     //digitalWrite(LEFT_LED, HIGH);
  }
  else {
      //digitalWrite(LEFT_LED, LOW);
      if (ACTUAL > ((INTERVALO_TIEMPO * 2)+ INICIO_PI)) {
          INICIO_PI= millis();
      }
   }

 //// Funcion de parpadeo - fin ////////////////
}
else {
  //digitalWrite(LEFT_LED, LOW);
}

//// Analisis de combinación DESARMAR ALARMA - Inicio ////////////

 if (/*PULSOS_DERECHA==(CLAVE_ARMAR_ALARMA-1) and */ PULSOS_IZQUIERDA==CLAVE_DESARMAR_ALARMA) {

    // DESARMAR ALARMA
    digitalWrite(ledAlarma, LOW); //solo para visualizar un cambio
    return ii;
  }
  else {
    // Resetear combinación
    if ((PULSOS_IZQUIERDA > CLAVE_DESARMAR_ALARMA) /*or (PULSOS_DERECHA > CLAVE_ARMAR_ALARMA)*/ or (millis() > (INICIO_IZQUIERDA + TIEMPO_INGRESO_EXCEDIDO))) {
        PULSOS_IZQUIERDA=0;
    }
  }

////// Analisis de combinación - Fin ///////////////

         Serial.print("Marca de tiempo: ");
         Serial.println(millis());
         Serial.print("pulsador Izquierda: ");
         Serial.println(digitalRead(this->pinIzq));
         Serial.print("pulsador Derecha: ");
         Serial.println(digitalRead(this->pinDer));
         Serial.print("Contador de pulsos derecha: ");
         Serial.println(PULSOS_DERECHA);
         Serial.print("Contador de pulsos Izquierda: ");
         Serial.println(PULSOS_IZQUIERDA);
         Serial.print("Inicio derecha: ");
         Serial.println(INICIO_DERECHA);
         Serial.print("Inicio parcial lado izquierda: ");
         Serial.println(INICIO_PI);

    return ninguna;
}
