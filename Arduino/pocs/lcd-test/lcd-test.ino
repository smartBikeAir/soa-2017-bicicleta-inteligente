#include <Wire.h> 
#include <LiquidCrystal_I2C.h>

#define I2C_ADDR    0x3F  // Define I2C Address for the PCF8574T 
#define BACKLIGHT_pin  3
#define En_pin  2
#define Rw_pin  1
#define Rs_pin  0
#define D4_pin  4
#define D5_pin  5
#define D6_pin  6
#define D7_pin  7

LiquidCrystal_I2C  lcd(I2C_ADDR, En_pin, Rw_pin, Rs_pin, D4_pin, D5_pin, D6_pin, D7_pin, BACKLIGHT_pin, POSITIVE);

void setup()
{ 
  lcd.begin(16,2);               // initialize the lcd 
  lcd.backlight();
}

void loop()
{
  lcd.home ();                   // go home
  lcd.clear();
  lcd.print("Hola! ");  
  lcd.setCursor ( 0, 1 );        // go to the next line
  lcd.print (" SOA - 2017 ");
  delay ( 1500 );
  lcd.home ();
  lcd.clear();
  lcd.print("Esto es un ");  
  lcd.setCursor ( 0, 1 );        // go to the next line
  lcd.print ("test run.");
  delay ( 1500 );  

}
