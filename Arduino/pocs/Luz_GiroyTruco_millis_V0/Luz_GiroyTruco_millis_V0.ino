// Implementación luz de giro.
// El cambio de estado en el led es establecido mediante el uso de la funcion millis(). La función millis() devuelve el tiempo transcurrido desde
// el inicio del programa.

// Resumen de funcionamiento
// Luz de giro. Se divide en lado derecho y lado izquierdo. Una vez accionado el interruptor el led del lado correspondiente comienza a parpadear. 
// 
// Conteo de pulsaciones para verificar el armado y desarmado de alarma.
// Para el Armado: Se utiliza conteo de pulsos del interruptor lado derecho. Se puede definir la cantidad de pulsaciones CLAVE_ARMAR_ALARMA.
// Para el DesArmado: Se utiliza conteo de pulsos del interruptor lado izquierdo. Se puede definir la cantidad de pulsaciones CLAVE_DESARMAR_ALARMA.
// En ambos casos, se toma un lapso de tiempo para evitar el efecto rebote del pulsador o interruptor utilizado EFECTO_REBOTE
// Tanto para el armado como desarme de la combinación realizada por el pulsador, se resetea el contador si ha pasado un determinado tiempo TIEMPO_INGRESO_EXCEDIDO.

// Si definimos VERBOSE, entonces se van a compilar las sentencias asociadas al DEBUG 
// (en este Sketch serían las que utilizan al Monitor Serie).
// Comentar la siguiente línea para evitar el modo VERBOSE.
#define VERBOSE

// Asignación de las Entradas y salidas digitales
int pulsadorDerecha=2; // Asignación de entrada digital para el ingreso de la señal del pulsador lado derecho.
int ledDerecha=13; // Asignación de la salida ditital para el encendido del led lado derecho.
int pulsadorIzquierda=4; // Asignación de entrada digital para el ingreso de la señal del pulsador lado izquierdo.
int ledIzquierda=8; // Asignación de la salida ditital para el encendido del led lado izquierdo.

// Declaración de variables globales


unsigned long MARCA;
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
unsigned long INTERVALO_TIEMPO=300; // Variable que establece el tiempo necesario para el cambio de estado del led. El objetivo es que 
                                    // el mismo encienda y apague (parpadeo)
unsigned int EFECTO_REBOTE=500;

void setup() {
  // Asignacion de E/S:
 pinMode(pulsadorDerecha, INPUT);
 pinMode(ledDerecha, OUTPUT);
 pinMode(pulsadorIzquierda, INPUT);
 pinMode(ledIzquierda, OUTPUT);

 #ifdef VERBOSE
  Serial.begin(9600);
  #endif

}   

void loop() {
// Ciclo:



// Lado Derecho

if (digitalRead(pulsadorDerecha)==LOW) // de acuerdo a si el pulsador es NA o NC se pone alto o bajo
 {
  if (PULSOS_DERECHA==0)
  {
    INICIO_DERECHA=millis();
    INICIO_PD=millis();
    PULSOS_DERECHA=1;
   }
  else
  {
   // EVITAR EFECTO REBOTE EN EL PULSADOR O SELECTOR //
   if ( millis() > (ACTUAL + EFECTO_REBOTE))
    {
     PULSOS_DERECHA++;
    }
  }
  
//// Funcion de parpadeo - Inicio ////////////////
    ACTUAL=millis();

  if (ACTUAL < INTERVALO_TIEMPO + INICIO_PD) // Primer Estado hasta tanto se cumpla el tiempo establecido
   { 
     digitalWrite(ledDerecha, HIGH);
   }
  else
   {
    digitalWrite(ledDerecha, LOW);
    if (ACTUAL > ((INTERVALO_TIEMPO * 2)+ INICIO_PD))
    {
      INICIO_PD= millis();
    }
   }
//// Funcion de parpadeo - fin ////////////////
 
 }
 else
 {
  digitalWrite(ledDerecha, LOW);
 }

//// Analisis de combinación - Inicio ////////////

 if (PULSOS_DERECHA==CLAVE_ARMAR_ALARMA)
  {
    // ARMAR ALARMA
    //digitalWrite(ledIzquierda, HIGH);  //solo para visualizar un cambio
    //delay(1000); // solo para visualizar un cambio
  }
 else
  {
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

if (digitalRead(pulsadorIzquierda)==LOW) // de acuerdo a si el pulsador es NA o NC se pone alto o bajo
 {
  if (PULSOS_IZQUIERDA==0)
  {
    INICIO_IZQUIERDA=millis();
    INICIO_PI=millis();
     PULSOS_IZQUIERDA=1;
   }
  else
  {
   // EVITAR EFECTO REBOTE EN EL PULSADOR O SELECTOR //
   if ( millis() > (ACTUAL + EFECTO_REBOTE))
    {
     PULSOS_IZQUIERDA++;
    }
  }
  //PULSOS_IZQUIERDA++;

//// Funcion de parpadeo - Inicio ////////////////
  
  ACTUAL=millis();
  if (ACTUAL < INTERVALO_TIEMPO + INICIO_PI) // Primer Estado hasta tanto se cumpla el tiempo establecido
   { 
     digitalWrite(ledIzquierda, HIGH);
   }
  else
   {
    digitalWrite(ledIzquierda, LOW);
    if (ACTUAL > ((INTERVALO_TIEMPO * 2)+ INICIO_PI))
    {
      INICIO_PI= millis();
    }
   }

 //// Funcion de parpadeo - fin ////////////////
 
 }
 else
 {
  digitalWrite(ledIzquierda, LOW);
 }

//// Analisis de combinación DESARMAR ALARMA - Inicio ////////////

 if (PULSOS_DERECHA==(CLAVE_ARMAR_ALARMA-1) and PULSOS_IZQUIERDA==CLAVE_DESARMAR_ALARMA)
  {
    // DESARMAR ALARMA
    digitalWrite(ledDerecha, HIGH); //solo para visualizar un cambio
    delay(1000); // solo para visualizar un cambio
    
  }
 else
  {
    // Resetear combinación
    if ((PULSOS_IZQUIERDA > CLAVE_DESARMAR_ALARMA) or (PULSOS_DERECHA > CLAVE_ARMAR_ALARMA) or (millis() > (INICIO_IZQUIERDA + TIEMPO_INGRESO_EXCEDIDO)))
    {
      PULSOS_IZQUIERDA=0;
    }
  }

////// Analisis de combinación - Fin ///////////////


 #ifdef VERBOSE
         Serial.print("Marca de tiempo: ");
         Serial.println(millis());
         Serial.println(digitalRead(pulsadorIzquierda));
         Serial.println(digitalRead(pulsadorDerecha));
         Serial.print("Contador de pulsos derecha: ");
         Serial.println(PULSOS_DERECHA);
         Serial.print("Contador de pulsos Izquierda: ");
         Serial.println(PULSOS_IZQUIERDA);
         Serial.print("Inicio derecha: ");
         Serial.println(INICIO_DERECHA);
         Serial.print("Inicio parcial lado izquierda: ");
         Serial.println(INICIO_PI);
         
         #endif


}
