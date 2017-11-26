
/*
 * Defined constants in arduino don't take up any program memory space on the chip. 
 * The compiler will replace references to these constants with the defined value at compile time.
 */
#define MODO_VIAJE  0
#define MODO_ALARMA 1

/** Pin mapping **/
// Boton de cambio de modo
#define PIN_BUTTON  13

// RGB Led
#define PIN_RGB_RED 10
#define PIN_RGB_GREEN 9
#define PIN_RGB_BLUE  8

// Buzzer
#define PIN_BUZZER 6

// Tilt sensor
#define PIN_TILT 7

// Debug mode
#define DEBUG true


// Modo actual del sistema
int modoSistema;

// Lectura del boton de cambio de modo
int cambiarModo;
int tiltValue;
int alarmDutyCycle;

bool alarmaSonando;

void setup() {
  // Pin config
  pinMode(PIN_BUTTON, INPUT);

  pinMode(PIN_RGB_RED, OUTPUT);
  pinMode(PIN_RGB_GREEN, OUTPUT);
  pinMode(PIN_RGB_BLUE, OUTPUT);
  
  pinMode(PIN_BUZZER, OUTPUT);

  pinMode(PIN_TILT, INPUT);

  // Sistema se inicia en Modo Viaje
  setModoViaje();
  
  if(DEBUG) {
    Serial.begin(9600);
  }
}

void loop() {
  // Cambio de modo?
  leerBotonCambioModo();
  if(cambiarModo > 50) {
    cambiarModo = 0;
      if(modoSistema == MODO_VIAJE) {
        // Pasa de modo viaje a modo alarma
        setModoAlarma();
      } else {
        // Pasa de modo alarma a modo viaje
        setModoViaje();
      }
  }

  // Flujo de cada modo
  if(modoSistema == MODO_VIAJE) {
    
  } else {
    leerTilt();
    if(tiltValue > 20 && !alarmaSonando) {
      rgbTurnOff();
      tiltValue = 0;
      alarmaSonando = true;
      digitalWrite(PIN_RGB_BLUE, HIGH);
    }
    if(alarmaSonando) {
      sonarAlarma();
    }
  }

  // Debug report
  if(DEBUG) {
    printDebugReport();
  }
}

void rgbTurnOff() {
  digitalWrite(PIN_RGB_RED, LOW);
  digitalWrite(PIN_RGB_GREEN, LOW);
  digitalWrite(PIN_RGB_BLUE, LOW);
}

void setModoAlarma() {
  rgbTurnOff();
  digitalWrite(PIN_RGB_RED, HIGH);
  tiltValue = 0;
  alarmDutyCycle = 100;
  alarmaSonando = false;
  modoSistema = MODO_ALARMA;
}

void setModoViaje() {
  rgbTurnOff();
  digitalWrite(PIN_BUZZER, HIGH);
  digitalWrite(PIN_RGB_GREEN, HIGH);
  alarmaSonando = false;
  modoSistema = MODO_VIAJE;
}

void leerBotonCambioModo() {
  if(digitalRead(PIN_BUTTON) == LOW) {
    cambiarModo +=1;
  } else {
    if(cambiarModo > 0) {
      cambiarModo--;
    }
  }
}


void leerTilt() {
  if(digitalRead(PIN_TILT) == HIGH) {
    Serial.println(tiltValue);
    tiltValue +=1;
  } else {
    if(tiltValue > 0) {
      tiltValue--;
    }
  }
}

void sonarAlarma() {
  if(alarmDutyCycle >= 100) {
    alarmDutyCycle = 50;
  } else {
    alarmDutyCycle += 5;
  }
  Serial.print("AlarmDutyCycle: ");
  Serial.print(alarmDutyCycle);
  Serial.print("\n");
  analogWrite(PIN_BUZZER, alarmDutyCycle);
}

void printDebugReport() {
     // Envia reportes al serial monitor para debugging indirecto
    Serial.print("Modo: ");
    Serial.print(modoSistema == MODO_VIAJE?"viaje":"alarma");
    Serial.print(" - cambiarModo: ");
    Serial.println(cambiarModo);
}


