// 433 MHz mock vysílač

// bezdratova komunikace
#include <VirtualWire.h>

void setup() {
  // put your setup code here, to run once:
  vw_set_tx_pin(8);
  // nastavení rychlosti přenosu v bitech za sekundu
  vw_setup(1000);
  
   Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  String message = "2;21.2;38.3;550;553;21;";
  byte messageLength = message.length() + 1; 

  // convert string to char array
  char charBuffer[messageLength]; 
  message.toCharArray(charBuffer, messageLength);

  digitalWrite(13, true);
  vw_send((uint8_t *)charBuffer, messageLength); 

  vw_wait_tx(); 
  digitalWrite(13, false);
  // Serial.print("sent: " + message); 
  Serial.print(message); 
  delay(1000);
}
