int val, minVal, maxVal;

void setup() {
  Serial.begin(9600);
  minVal = 400;
  maxVal = 600;
  pinMode(2, OUTPUT);
  digitalWrite(1, LOW);
}

void loop() {
  // Leo el LDR
  val = analogRead(A1); 

  // Si la cantidad de luz es baja, cierro el relé (enciendo la tira de LED's)
  if(val < minVal) {
    digitalWrite(2, HIGH);
  }

  // Si la cantidad de luz es alta, abro el relé (apago la tira de LED's)
  if(val > maxVal) {
    digitalWrite(2, LOW);
  }
  
  Serial.println(val);
}
