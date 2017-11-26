/*
 * Programa bastante sencillo que armé para analizar rápidamente (plug n' play)
 * distintos sensores y reportar sus variaciones.
 */

void setup() {
  pinMode(13,INPUT);
  Serial.begin(9600);
}

void loop() {
  String analogText = "Analog read: ";
  String digitalText = " - Digital read: ";
  String message;
  message = analogText + analogRead(13);
  message += digitalText + digitalRead(13);
  Serial.println(message);
  delay(250);
}
