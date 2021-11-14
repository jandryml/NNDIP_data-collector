// 433 MHz vysílač

// bezdratova komunikace
#include <VirtualWire.h>
// teplomer
#include "DHT.h"
// nastavení čísla propojovacího pinu
#define pinDHT 5
// vytvoření instance senzoru z knihovny,
// s nastavením typu DHT22, který má stejný
// typ komunikace jako AM2120
DHT dht(pinDHT, DHT22);

void setup()
{
  // nastavení typu bezdrátové komunikace
  vw_set_ptt_inverted(true);
  // nastavení čísla datového pinu pro vysílač
  vw_set_tx_pin(8);
  // nastavení rychlosti přenosu v bitech za sekundu
  vw_setup(1000);

  Serial.begin(9600);

  // zahájení komunikace se senzorem DHT
  dht.begin();
}

String DHTHandle() {
    // načtení teploty do proměnné
  float teplota = dht.readTemperature();
  // načtení vlhkosti do proměnné
  float vlhkost = dht.readHumidity();
  // kontrola, jestli jsou načtené hodnoty čísla, pomocí funkce isnan
  if (isnan(teplota) || isnan(vlhkost)) {
    // při chybném čtení vypiš hlášku
    return "Error: reading DHT!";
  } else {
    String result = "";
    result.concat(teplota);
    result.concat(";");
    result.concat(vlhkost);
    result.concat(";\n");
//    Serial.print(result);
    return result;
  }
}

void sendString(String message, bool wait)
{
  byte messageLength = message.length() + 1; 

  // convert string to char array
  char charBuffer[messageLength]; 
  message.toCharArray(charBuffer, messageLength);

  digitalWrite(13, true);
  vw_send((uint8_t *)charBuffer, messageLength); 

  if (wait) vw_wait_tx(); 
  digitalWrite(13, false);
  Serial.println("sent: " + message); 
}

void loop()
{
  sendString(DHTHandle(), true); 
  delay(1000);
}
