// Implementación preliminar de luz de giro.
// El cambio de estado en el led es establecido mediante conteo de vueltas para evitar uso de delay.

int pulsadorDerecha=2; // Asignación de entrada digital 2 para el ingreso de la señal del pulsador.
int ledDerecha=13; // Asignación de la salida ditital 13 para el encendido del led.
int pulsadorIzquierda=4; // Asignación de entrada digital 2 para el ingreso de la señal del pulsador.
int ledIzquierda=8; // Asignación de la salida ditital 13 para el encendido del led.
int contadorVueltas=0;  // Variable utilizada para contar las vueltas.
int Cambio=15000;  // Variable que establece el tiempo necesario para establecer el cambio de estado del led. El objetivo es que el mismo encienda y apague 
                   // simulando lo realizado por motos y autos, para indicar giro.

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
  if (contadorVueltas < Cambio) // Primer Estado hasta tanto se cumpla el tiempo establecido
   { 
     digitalWrite(ledDerecha, HIGH);
   }
   else
   {
    digitalWrite(ledDerecha, LOW);
    if (contadorVueltas > (Cambio*2))
    {
      contadorVueltas=0;
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
  if (contadorVueltas < Cambio) // Primer Estado hasta tanto se cumpla el tiempo establecido
   { 
     digitalWrite(ledIzquierda, HIGH);
   }
   else
   {
    digitalWrite(ledIzquierda, LOW);
    if (contadorVueltas > (Cambio*2))
    {
      contadorVueltas=0;
    }
   }
 }
 else
 {
  digitalWrite(ledIzquierda, LOW);
 }


contadorVueltas++;

}
