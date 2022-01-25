// 433 MHz vysílač

// bezdratova komunikace
#include <VirtualWire.h>
// knihovna pro AM2120 - teplomer a vlhkomer 
#include "DHT.h"
// nastavení datoveho pinu pro AM2120
#define pinDHT 5
// vytvoření instance senzoru z knihovny,
// s nastavením typu DHT22, který má stejný
// typ komunikace jako AM2120
DHT dht(pinDHT, DHT22);

// mente dle potreby, je pouzivano jako id zarizeni
#define deviceID 1

// knihovna pro detektor CO2 MH-Z19
#include "MHZ19.h"
// nastavení datových pinů pro MH-Z19
#define rx_pin 11
#define tx_pin 10
#define pwmpin 9
// vytvoření objektů z knihovny
MHZ19 *mhz19_uart = new MHZ19(rx_pin, tx_pin);
MHZ19 *mhz19_pwm = new MHZ19(pwmpin);

void setup()
{
  // nastavení typu bezdrátové komunikace
  //vw_set_ptt_inverted(true);
  // nastavení čísla datového pinu pro vysílač
  vw_set_tx_pin(8);
  // nastavení rychlosti přenosu v bitech za sekundu
  vw_setup(1000);

  Serial.begin(9600);

  // zahájení komunikace se senzorem AM2120
  dht.begin();

  // zahájení komunikace se senzorem MH-Z19 přes UART
  mhz19_uart->begin(rx_pin, tx_pin);
  // vypnutí autokalibrace
  mhz19_uart->setAutoCalibration(false);
}

// cte hodnoty z AM2120
String handleDHT() {
    // načtení teploty do proměnné
  float teplota = dht.readTemperature();
  // načtení vlhkosti do proměnné
  float vlhkost = dht.readHumidity();
  // kontrola, jestli jsou načtené hodnoty čísla, pomocí funkce isnan
  if (isnan(teplota) || isnan(vlhkost)) {
    // při chybném čtení prazdne hodnoty
    return ";;";
  } else {
    String result = "";
    result.concat(teplota);
    result.concat(";");
    result.concat(vlhkost);
    result.concat(";");
//    Serial.print(result);
    return result;
  }
}

// cte hodnoty z MH-Z19
String handleMHZ19() {
    // zahájení měření senzoru a načtení výsledků do proměnné
  measurement_t m = mhz19_uart->getMeasurement();
  int co2ppm = mhz19_pwm->getPpmPwm();
  String result = "";
  // CO2 mereni
  result.concat(map(m.co2_ppm, 0, 5000, 0, 2000));
  result.concat(";");
  // CO2 mereni pres PWM
  result.concat(co2ppm);
  result.concat(";");
  // teplota
  result.concat(m.temperature);
  result.concat(";\n");
//  Serial.print(result);
  return result;
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
  // Serial.print("sent: " + message); 
  Serial.print(message); 
}

void loop()
{
  String message = "";
  message.concat(deviceID);
  message.concat(";");
  message.concat(handleDHT());
  message.concat(handleMHZ19());
  // sends data in this format:
  // (float)(AM2120 - teplota); (float)(AM2120 - vlhost); (int)(MH-Z19 - UART - CO2); (int)(MH-Z19 - PWM - CO2); (int)(MH-Z19 - teplota);
  sendString(message, true); 
  delay(1000);
}
