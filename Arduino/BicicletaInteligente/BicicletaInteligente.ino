
// Bibliotecas
#include <SoftwareSerial.h>
#include <LibUltrasonic.h>
#include <LibAlarma.h>
#include <LibVelocidad.h>
#include <LibLDR.h>
#include <LibRele.h>
#include <LibTilt.h>
#include <LibLuzDeGiro.h>
#include <LibLlaveTresEstados.h>
#include <stdlib.h>

// Pines utilizados
#define LDR_PIN A0
#define RIGHT_LED 2
#define LEFT_LED 3
#define THREE_STATES_RIGHT_PIN 4
#define THREE_STATES_LEFT_PIN 5
#define BUZZER_PIN 6
#define TILT_PIN 7
#define HALL_PIN 8
#define RELE_PIN 9
#define ULTRASONIC_SENSOR_TRIG_PIN 10
#define ULTRASONIC_SENSOR_ECHO_PIN 11
#define BLUETOOTH_RX 12
#define BLUETOOTH_TX 13

// Velocidad mínima para poder entrar, de forma automática,
// al estado traveling.
#define MIN_VELOCITY_FOR_CHANGE_TO_TRAVELING_STATE 3 // En km/h

// Velocidad mínima para poder entrar, de forma automática,
// al estado alarmIsRinging.
#define MIN_VELOCITY_FOR_CHANGE_TO_ALARM_IS_RINGING_STATE 4 // En km/h

// Distancia mínima a la que debe estar el objeto más cercano a la 
// bicicleta (detrás de la misma).
#define MIN_DISTANCE_TO_OBJECT 30 // En cm

// Constante necesaria para la transición automática de estado: traveling -> standBy.
#define MIN_TIME_FOR_CHANGE_TO_STAND_BY 60000 //1 minuto.

// Tiempo minimo que debe transcurrir para contabilizar un cambio de estado de las luces del chasis
#define MIN_TIME_FOR_SWITCH_CHASSIS_LIGHTS 100 //Aprox ~5 seg

// Variables necesarias para la transición automática de estado: traveling -> standBy.
boolean shouldSetTimestamp = true;
unsigned long velocityZeroTimestamp;

int detectedLightTimeCounter = 0;
int noLightTimeCounter = 0;

#define END_CMD_CHAR '#' // Bluetooth: caracter de fin de mensaje.
SoftwareSerial BT1(BLUETOOTH_RX, BLUETOOTH_TX);

// Definicion de sensores
LibUltrasonic ultrasonicSensor(ULTRASONIC_SENSOR_TRIG_PIN, ULTRASONIC_SENSOR_ECHO_PIN); 
LibVelocidad Velocidad(HALL_PIN);
LibLDR ldr(LDR_PIN);
LibTilt tilt(TILT_PIN);
LibLlaveTresEstados llaveTresEstados(THREE_STATES_LEFT_PIN, THREE_STATES_RIGHT_PIN);

// Definición de actuadores
LibBuzzer Buzzer(BUZZER_PIN);
LibAlarma *alarm = new LibAlarma(BUZZER_PIN);
LibRele rele(RELE_PIN);
LibLuzDeGiro luzDeGiro(LEFT_LED, RIGHT_LED);

// Estados posibles del sistema (máquina de estados)
enum state {
  standBy,
  traveling,
  activatedAlarm,
  alarmIsRinging
};

// Mensajes posibles entre Arduino y Android.
enum message {
  unknown = 0,
  startedTrip,
  endedTrip,
  activateAlarm,
  deactivateAlarm,
  turnAlarmOn,
  turnAlarmOff,
  velocity,
  nearObject,
  nearObjectEnabled, // Android habilitó la funcionalidad.
  nearObjectDisabled, // Android deshabilitó la funcionalidad.
  automaticLightEnabled, // Android habilitó la funcionalidad.
  automaticLightDisabled // Android deshabilitó la funcionalidad.
};

state systemState;
message receivedMessage;

// Variables de configuración para poder encender/apagar
// funcionalidades desde la aplicación de Android.
bool nearObjectON = true;
bool automaticLightON = true;

void setup() {

    // Por defecto el sistema se encuentra en estado stand by, 
    // esperando un evento que modifique dicho estado.
    systemState = standBy;

    Serial.begin(9600);
    while (!Serial) ;             // wait for Arduino Serial Monitor to open
    Serial.println("------ MODULE HC-05 AVAILABLE ------");
    BT1.begin(9600);  
}

void loop() {

      // Chequeamos si tenemos un nuevo mensaje BT.
      receivedMessage = receiveMessage();

      // En este momento chequeamos únicamente si recibimos
      // un mensaje que modifica el encendido/apagado de
      // las funcionalidades objeto cercano y luz del chasis 
      // automática.
      switch (receivedMessage) {
         case nearObjectEnabled:
             nearObjectON = true;
             Serial.println("----- Funcionalidad objeto cercano ON -----");
             break;
         case nearObjectDisabled:
             nearObjectON = false;
             Serial.println("----- Funcionalidad objeto cercano OFF -----");
             break;
         case automaticLightEnabled:
             automaticLightON = true;
             Serial.println("----- Funcionalidad luz automática ON -----");
             break;
         case automaticLightDisabled:
             automaticLightON = false;
             Serial.println("----- Funcionalidad luz automática OFF -----");
             break;
         default:
             break;
      }

      // Determinamos que estado debe ejecutarse.
      switch(systemState) {
      case standBy:
          execStandBy();
          break;
      case traveling:
          execTraveling();
          break;
      case activatedAlarm:
          execActivatedAlarm();
          break;
      case alarmIsRinging:
          execAlarmIsRinging();
          break;
    }
}

void execStandBy() {

    combinacion resultado = llaveTresEstados.leerCombinacion();
    unsigned long vel = Velocidad.medirVelocidad();
    if (receivedMessage == activateAlarm) {
        systemState = activatedAlarm;
        Serial.println("PASAMOS A MODO ALARMA ACTIVADA!!!!!!!!!!!!!!!!!!!!!!");
    } else if (receivedMessage == startedTrip) {
        systemState = traveling;
        Serial.println("PASAMOS A MODO VIAJANDO!!!!!!!!!!!!!!!!!!!!!!");
    } else if (resultado == ddd) { // Si se hizo la combinación que activa la alarma.
        sendMessage(activateAlarm);
        systemState = activatedAlarm;
        Serial.println("PASAMOS A MODO ALARMA ACTIVADA!!!!!!!!!!!!!!!!!!!!!!");
    } else if (vel > MIN_VELOCITY_FOR_CHANGE_TO_TRAVELING_STATE) {
        sendMessage(startedTrip);
        systemState = traveling;
        Serial.println("PASAMOS A MODO VIAJANDO!!!!!!!!!!!!!!!!!!!!!!");
    }
}

void execTraveling() {

    // Funcionalidad objeto cercano.
    if (nearObjectON == true) {
        long distanceToObject = ultrasonicSensor.checkDistance();        
        boolean nearObjectAlert = distanceToObject != UNDEFINED_DISTANCE && distanceToObject < MIN_DISTANCE_TO_OBJECT;
        if (nearObjectAlert == true) {
            alarm->activarAlarmaSonando(); // Utilizamos la alarma para avisar al usuario que tiene un objeto cercano.
            sendMessage(nearObject); // Le avisamos a Android que tenemos un objeto cercano.
        } else {
            alarm->desactivarAlarmaSonando();
        }
    } else {
            alarm->desactivarAlarmaSonando();
    }

    // Funcionalidad encendido automático de luces del chasis (LDR y Rele + leds).
    if (automaticLightON == true) {
        if (ldr.hayLuz() && rele.isClosed()) { // Si hay luz ambiente/natural.
           detectedLightTimeCounter++;
        }

        if(!ldr.hayLuz() && rele.isOpen()){
          noLightTimeCounter++;
        }
        
        if(rele.isClosed() && detectedLightTimeCounter > MIN_TIME_FOR_SWITCH_CHASSIS_LIGHTS) {
          detectedLightTimeCounter = 0;
          rele.openRele(); // Apago luz.
        }
        if(rele.isOpen() && noLightTimeCounter > MIN_TIME_FOR_SWITCH_CHASSIS_LIGHTS) {
          noLightTimeCounter = 0;
          rele.closeRele(); // Enciendo luz.
        }
    } else {
         rele.openRele(); // Apago luz.
    }

    // Funcionalidad luces de giro.
    estadoActual threeStates = llaveTresEstados.leerEstado();
    if (threeStates == izquierda) {
      luzDeGiro.encenderLuzIzquierda();
    } else if (threeStates == derecha) {
      luzDeGiro.encenderLuzDerecha();
    } else {
      luzDeGiro.apagarLuces();
    }

    // Chequeamos si la aplicación de Android nos envió
    // un mensaje de finalización de viaje y en ese caso
    // cambiamos el estado del sistema.
    if (receivedMessage == endedTrip) {

        // Al irme del estado traveling, tengo que apagar los actuadores asociados.
        rele.openRele(); // Apago luz del chasis.
        Buzzer.desactivarBuzzer(); // Por si está sonando por objeto cercano.
        luzDeGiro.apagarLuces();// Apagar luces de giro (por si alguna justo quedó encendida)
        systemState = standBy;
        Serial.println("----- PASAMOS A MODO REPOSO -----");
        return;
    }

    // Sensado de velocidad.
    long velocityValue = Velocidad.medirVelocidad();
    sendVelocity(velocityValue); // Enviamos a Android la velocidad actual.
    if (velocityValue == 0) { // Si el usuario está quieto.

        if (shouldSetTimestamp == true) {
            shouldSetTimestamp = false;
            velocityZeroTimestamp = millis();
        } else if ((millis() - velocityZeroTimestamp) > MIN_TIME_FOR_CHANGE_TO_STAND_BY) {
            sendMessage(endedTrip);
            
            // Al irme del estado traveling, tengo que apagar los actuadores asociados.
            rele.openRele(); // Apago luz del chasis.
            Buzzer.desactivarBuzzer(); // Por si está sonando por objeto cercano.
            luzDeGiro.apagarLuces();// Apagar luces de giro (por si alguna justo quedó encendida)
            shouldSetTimestamp = true;
            systemState = standBy;
            Serial.println("----- PASAMOS A MODO REPOSO -----");
        }
    }
}

void execActivatedAlarm() {

    combinacion resultado = llaveTresEstados.leerCombinacion();
    unsigned long vel = Velocidad.medirVelocidad();
    if (tilt.isTilted() == true || vel > MIN_VELOCITY_FOR_CHANGE_TO_ALARM_IS_RINGING_STATE) {
        // Hacemos sonar la alarma.
        rele.closeRele(); // Enciendo luz del chasis.
        systemState = alarmIsRinging;
        Serial.println("----- PASAMOS A MODO ALARMA SONANDO -----");
    } else if (resultado == ii) { // Si se hizo la combinación que desactiva la alarma.
        sendMessage(deactivateAlarm);
        systemState = standBy;
        Serial.println("----- PASAMOS A MODO REPOSO -----");
    } else if (receivedMessage == deactivateAlarm) {
        systemState = standBy;
        Serial.println("----- PASAMOS A MODO REPOSO -----");
    }
}

void execAlarmIsRinging() {

    alarm->sonarAlarmaAlternada();
    combinacion resultado = llaveTresEstados.leerCombinacion();
    if (receivedMessage == turnAlarmOff) {
        alarm->desactivarAlarmaSonando();
        rele.openRele(); // Apago luz del chasis.
        systemState = standBy;
        Serial.println("----- PASAMOS A MODO REPOSO -----");
    } else if (resultado == ii) { // Si se hizo la combinación que apaga la alarma.
        alarm->desactivarAlarmaSonando();
        rele.openRele(); // Apago luz del chasis.
        sendMessage(turnAlarmOff);
        systemState = standBy;
        Serial.println("----- PASAMOS A MODO REPOSO -----");
    }
}

// Bluetooth
message receiveMessage() {
   message value = unknown;
   String message = "";
   
   if (BT1.available()) {
      Serial.println("Info BT disponible");
      char c = BT1.read();         
      while ( c != END_CMD_CHAR) { // Hasta que el caracter sea END_CMD_CHAR             
          message += c;           
          Serial.println(message);
          c = BT1.read();  
       }
       int integerValue = atoi(message.c_str());   
       value = getMessageFromInteger(integerValue);
       return(value) ;
    }
    return value;
}

void sendMessage(message identifier) {
     BT1.print(identifier);
     BT1.print('\n');
}

void sendVelocity(int value) {
     value += 200; // Por convención para identificar a la velocidad, tomamos como base a 200.
     BT1.print(value);
     BT1.print('\n');
}

message getMessageFromInteger(int value) {

    Serial.print("Recibimos mensaje BT con ID = ");
    Serial.println(value);
    
    switch(value) {
       case 1:
          return startedTrip;
       case 2:
          return endedTrip;
       case 3:
          return activateAlarm;
       case 4:
          return deactivateAlarm;
       case 5:
          return turnAlarmOn;
       case 6:
          return turnAlarmOff;
       case 7:
          return velocity; //TODO: Chequear si realmente lo necesitamos.
       case 8:
          return nearObject;
       case 9:
          return nearObjectEnabled;
       case 10:
          return nearObjectDisabled;
       case 11:
          return automaticLightEnabled;
       case 12:
          return automaticLightDisabled;
       default:
          return unknown;
    }
}

