#include <SoftwareSerial.h>

// ARDUINO        RXD   TXD
// HC-05          TXD   RXD
// -----          ---   ---
// Arduino Uno     8    9

#define BT_VCC 7
#define LED_PIN_13 13 // usamos al LED por defecto del pin 13, para marcar cambio de estado.
#define BT_RXD 10
#define BT_TXD 11
#define START_CMD_CHAR '*'
#define END_CMD_CHAR '#'



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
  nearObject
};

//bluetooth hc-05
SoftwareSerial BT1(BT_RXD, BT_TXD);
String message = "";
int message2; 

void setup() {
  // CONFIGURAMOS UN PIN COMO SALIDA, CUANDO MANDAMOS HIGH ENCIENDE EL MÓDULO HC-05
  pinMode(BT_VCC, OUTPUT);
    
  pinMode(LED_PIN_13, OUTPUT);   //Declara pin de Salida
  digitalWrite(LED_PIN_13, LOW); //Normalmente Apagado

  
  Serial.begin(9600);
  while (!Serial) ;             // wait for Arduino Serial Monitor to open
  
  Serial.println("------ MODULE HC-05 AVAILABLE ------");
  digitalWrite(BT_VCC, HIGH);    // Enciende el modulo     
  BT1.begin(9600);  
  
}
 
void loop() {
  // Si tengo datos en el bluetooth, available me devuelve un size.
  if (BT1.available() > 0) {
   
    message = BT1.readString();
    Serial.print("size: ");
    Serial.println(message.length());
    Serial.println(message);
    digitalWrite(LED_PIN_13, HIGH);

   /* message2 = get_cod_Message();
    Serial.print(message2);
     Serial.print('\n');*/
  }

  // Si tengo datos en el Serial Monitor, available me devuelve un size.
  if (Serial.available() > 0) {
    message = Serial.readStringUntil('\n');
    Serial.println("SENT ---> " + message);
    BT1.print(message);
    BT1.print('\n');
    digitalWrite(LED_PIN_13, LOW);
  }
 
}

int get_cod_Message( )
   {   int cod_msj = 0 ;
   
       if (BT1.available())
          {    char c = BT1.read(); 
              
                while ( c != END_CMD_CHAR)            //Hasta que el caracter sea END_CMD_CHAR
                  {       
                        cod_msj = (int)(c - 48);   
                        return( cod_msj ) ;
                        c = BT1.read();  
                        
                  }
                  
          }

          return( cod_msj ) ;
   }

