int bluePin = 13;
int greenPin = 12;
int redPin = 11;
int leds[] = {bluePin, greenPin, redPin};

void setup() {
  pinMode(bluePin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  pinMode(redPin, OUTPUT);
  randomSeed(199);
}

void loop() {
  resetLights();
  digitalWrite(leds[random(0, sizeof(leds))], HIGH);
  delay(1000);   
}

void resetLights() {
  digitalWrite(bluePin, LOW);
  digitalWrite(greenPin, LOW);
  digitalWrite(redPin, LOW);
}

