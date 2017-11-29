#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#include "LibLlaveTresEstados.h"

// Variables para pulsos derecha.
unsigned long INICIO_COMBINACION_DERECHA;
unsigned long PULSOS_DERECHA = 0;
unsigned long PULSO_DERECHA_TIMESTAMP;
unsigned int CLAVE_ARMAR_ALARMA=3;
bool ACTUALIZAR_PULSO_DERECHA_TIMESTAMP = true;
unsigned long ULTIMO_EXITO_COMB_DERECHA;

// Variable para pulsos izquierda.
unsigned long INICIO_COMBINACION_IZQUIERDA;
unsigned long PULSOS_IZQUIERDA = 0;
unsigned long PULSO_IZQUIERDA_TIMESTAMP;
unsigned int CLAVE_DESARMAR_ALARMA=2;
bool ACTUALIZAR_PULSO_IZQUIERDA_TIMESTAMP = true;
unsigned long ULTIMO_EXITO_COMB_IZQUIERDA;

// El usuario tiene 4 segundos para poder completar la combinación.
unsigned int TIEMPO_INGRESO_EXCEDIDO=4000;

// Tiempo anti-rebote. Me permite ignorar el ruido.
unsigned int EFECTO_REBOTE=500;

LibLlaveTresEstados::LibLlaveTresEstados(int pinIzquierda, int pinDerecha) {
	this->pinIzq = pinIzquierda;
  this->pinDer = pinDerecha;
  pinMode(pinDerecha, INPUT);
  pinMode(pinIzquierda, INPUT);
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

// millis() - ULTIMO_EXITO_COMB_DERECHA > EFECTO REBOTE.  Esta condición sirve
// para ignorar los proximos 500ms después de un caso de éxito. El motivo es
// porque el último puso que completa la combinación también genera rebote y
// ensucia las variables.
if (digitalRead(this->pinDer)==LOW && millis() - ULTIMO_EXITO_COMB_DERECHA > EFECTO_REBOTE) {

    if (ACTUALIZAR_PULSO_DERECHA_TIMESTAMP == true) {
        ACTUALIZAR_PULSO_DERECHA_TIMESTAMP = false;
        PULSO_DERECHA_TIMESTAMP = millis();
    }

    if (PULSOS_DERECHA==0) {

        // Inicio de la combinación derecha.
        reiniciarCombinacionIzquierda();
        INICIO_COMBINACION_DERECHA = millis();
        PULSOS_DERECHA++;
    } else {
        // EVITAR EFECTO REBOTE EN EL PULSADOR O SELECTOR //
        if (millis() - PULSO_DERECHA_TIMESTAMP > EFECTO_REBOTE) {
            PULSOS_DERECHA++;
            ACTUALIZAR_PULSO_DERECHA_TIMESTAMP = true;
        }
    }

    Serial.print("Contador de pulsos derecha: ");
    Serial.println(PULSOS_DERECHA);
}

//// Analisis de combinación - Inicio ////////////
if (PULSOS_DERECHA==CLAVE_ARMAR_ALARMA) {
    // ARMAR ALARMA
    reiniciarCombinacionDerecha();
    reiniciarCombinacionIzquierda();
    ULTIMO_EXITO_COMB_DERECHA = millis();
    return ddd;
}

if (PULSOS_DERECHA > 0 && (millis() - INICIO_COMBINACION_DERECHA) > TIEMPO_INGRESO_EXCEDIDO) {
    reiniciarCombinacionDerecha();
    reiniciarCombinacionIzquierda();
}

// --------- Lado Izquierdo ---------- //
if (digitalRead(this->pinIzq) == LOW && millis() - ULTIMO_EXITO_COMB_IZQUIERDA > EFECTO_REBOTE) {

    if (ACTUALIZAR_PULSO_IZQUIERDA_TIMESTAMP == true) {
        ACTUALIZAR_PULSO_IZQUIERDA_TIMESTAMP = false;
        PULSO_IZQUIERDA_TIMESTAMP=millis();
    }

    if (PULSOS_IZQUIERDA == 0) {

      // Inicio de la combinación izquierda.
       reiniciarCombinacionDerecha();
       INICIO_COMBINACION_IZQUIERDA = millis();
       PULSOS_IZQUIERDA++;
     }
     else {
         // EVITAR EFECTO REBOTE EN EL PULSADOR O SELECTOR //
        if (millis() - PULSO_IZQUIERDA_TIMESTAMP > EFECTO_REBOTE) {
           PULSOS_IZQUIERDA++;
           ACTUALIZAR_PULSO_IZQUIERDA_TIMESTAMP = true;
        }
     }

     Serial.print("Contador de pulsos Izquierda: ");
     Serial.println(PULSOS_IZQUIERDA);
 }

 //// Analisis de combinación ////
 if (PULSOS_IZQUIERDA==CLAVE_DESARMAR_ALARMA) {
     reiniciarCombinacionIzquierda();
     reiniciarCombinacionDerecha();
     ULTIMO_EXITO_COMB_IZQUIERDA = millis();
     return ii;
 }

if (PULSOS_IZQUIERDA > 0 && (millis() - INICIO_COMBINACION_IZQUIERDA) > TIEMPO_INGRESO_EXCEDIDO) {
    reiniciarCombinacionIzquierda();
    reiniciarCombinacionDerecha();
}
    return ninguna;
}

void LibLlaveTresEstados::reiniciarCombinacionDerecha() {
  PULSOS_DERECHA=0;
  ACTUALIZAR_PULSO_DERECHA_TIMESTAMP = true;
}

void LibLlaveTresEstados::reiniciarCombinacionIzquierda() {
  PULSOS_IZQUIERDA=0;
  ACTUALIZAR_PULSO_IZQUIERDA_TIMESTAMP = true;
}
