/*
 * Cuenta cantidad de vueltas por minuto.
 * Muestra por serial: Vueltas: v - Tiempo medición: t - RPM: r
 * 
 * Rueda de 26" de ISO 50-559 (559mm diametro)
 * Max speed: 50 km/h (474,522 RPM) - Read time: 100 ms
 * Min speed: 3 km/h  (28,471 RPM)  - Read time: 2000 ms
 * 
 * Input: digital read A0
 * 
 * Sensor: 3144 402 digital.
 *        G: GND
 *        R: 5v
 *        Y: A0
 */

#define MIN_TIME 100

unsigned long startTime = 0;
unsigned long currentTime = 0;
unsigned long lastResetTime = 0;
unsigned long timeSinceLastRead = 0;

int currentValue = 0;
int lastValue = 0;
int revolutions = 0;
float currentRPM = 0.0;

void setup() {
  Serial.begin(9600);
  startTime = millis();
}

void loop() {
  // Time since last read
  timeSinceLastRead = millis() - currentTime;

  // Read
  currentTime = millis();
  currentValue = digitalRead(A0);

  // Check if should count revolution
  if(lastValue > currentValue && lastResetTime >= MIN_TIME) {
    revolutions++;
  }

  // Reset RPM counter (after 2 second)
  if(currentTime - lastResetTime > 2000) {
    Serial.println("==== RESET ====");
    Serial.print("Vueltas: ");
    Serial.println(revolutions);
    
    Serial.print("RPM: ");
    currentRPM = (float)(revolutions*60)/2;
    Serial.println(currentRPM);
    
    Serial.print("Velocidad: ");
    Serial.println(0.559*currentRPM*0.10472);

    currentTime = 0;
    revolutions = 0;
    lastResetTime = millis();
  }
  
  lastValue = currentValue;
}
