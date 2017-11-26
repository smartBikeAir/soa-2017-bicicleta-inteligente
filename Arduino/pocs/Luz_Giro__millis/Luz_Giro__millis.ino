// Implementación preliminar de luz de giro.
// El cambio de estado en el led es establecido mediante el uso de la funcion millis(). La función millis() devuelve el tiempo transcurrido desde
// el inicio del programa.

int pulsadorDerecha=2; // Asignación de entrada digital 2 para el ingreso de la señal del pulsador.
int ledDerecha=13; // Asignación de la salida ditital 13 para el encendido del led.
int pulsadorIzquierda=4; // Asignación de entrada digital 2 para el ingreso de la señal del pulsador.
int ledIzquierda=8; // Asignación de la salida ditital 13 para el encendido del led.

unsigned long MARCA=0;
unsigned long ACTUAL=0;
unsigned long INTERVALO_TIEMPO=500; // Variable que establece el tiempo necesario para el cambio de estado del led. El objetivo es que 
                                    // el mismo encienda y apague (parpadeo)

void setup() {
  // Asignacion de E/S:
pinMode(pulsadorDerecha, INPUT);
pinMode(ledDerecha, OUTPUT);
pinMode(pulsadorIzquierda, INPUT);
pinMode(ledIzquierda, OUTPUT);
}

void loop() {
// Ciclo:
// Lado Derecho

if (digitalRead(pulsadorDerecha)==LOW) // de acuerdo a si el pulsador es NA o NC se pone alto o bajo
 {
  //if (contadorVueltas < Cambio) // Primer Estado hasta tanto se cumpla el tiempo establecido
  ACTUAL=millis();
  if (ACTUAL < (INTERVALO_TIEMPO + MARCA)) // Primer Estado hasta tanto se cumpla el tiempo establecido
   { 
     digitalWrite(ledDerecha, HIGH);
     MARCA=millis();
   }
  else
   {
    digitalWrite(ledDerecha, LOW);
    if (ACTUAL < ((INTERVALO_TIEMPO * 2)+ MARCA))
    {
      MARCA= millis();
    }
   }
 }
 else
 {
  digitalWrite(ledDerecha, LOW);
 }

// Lado Izquierdo

if (digitalRead(pulsadorIzquierda)==LOW) // de acuerdo a si el pulsador es NA o NC se pone alto o bajo
 {
  ACTUAL=millis();
  if (ACTUAL < (INTERVALO_TIEMPO + MARCA)) // Primer Estado hasta tanto se cumpla el tiempo establecido
   { 
     digitalWrite(ledIzquierda, HIGH);
     MARCA=millis();
   }
   else
   {
    digitalWrite(ledIzquierda, LOW);
    if (ACTUAL < ((INTERVALO_TIEMPO * 2)+ MARCA))
    {
      MARCA= millis();
    }
   }
 }
 else
 {
  digitalWrite(ledIzquierda, LOW);
 }

}
