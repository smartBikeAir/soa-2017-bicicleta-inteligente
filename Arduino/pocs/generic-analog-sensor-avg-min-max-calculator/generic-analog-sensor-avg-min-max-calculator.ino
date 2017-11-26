/*
 * Este poc fue creado para obtener los valores min, max y promedio del sensor LDR pero también puede ser útil para obtener los valores de cualquier otro sensor analógico.
 *  
 */

int val, minVal, maxVal, avgVal;
unsigned long sum;
unsigned long iterations;

unsigned long startTime;
unsigned long elapsedTime;

void setup() {
  Serial.begin(9600);
  minVal = 0;
  maxVal = 0;
  avgVal = 0;
  sum = 0;
  iterations = 0;
  startTime = millis();
}

void loop() {
  // Leo el LDR
  val = analogRead(A1); 

  // Detecto si es menor que el menor hasta el momento
  if(val < minVal) {
    minVal = val;
  }

  // Detecto si es mayor que el mayor hasta el momento
  if(val > maxVal) {
    maxVal = val;
  }

  // Calculo promedio actual
  sum += val;
  iterations++;
  avgVal = sum/iterations;

  elapsedTime = millis() - startTime;
  Serial.println(elapsedTime);
  if(elapsedTime > 60000) {
    Serial.println("Average: ");
    Serial.print(avgVal);
    Serial.print(" - Min: ");
    Serial.print(minVal);
    Serial.print(" - Max: ");
    Serial.print(maxVal);
    delay(6000000);
  }
}
