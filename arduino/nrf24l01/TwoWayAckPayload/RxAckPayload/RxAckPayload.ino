// SimpleRxAckPayload- the slave or the receiver

// knihovny pro nrf24l01
//==============
#include <RF24.h>
#include <nRF24L01.h>
#include <SPI.h>
//==============
#include "DHT.h" // knihovna pro AM2120 - teplomer a vlhkomer
#include "MHZ19.h" // knihovna pro detektor CO2 MH-Z19

// nastaveni pinu pro nrf24l01
#define CE_PIN  7
#define CSN_PIN 8

// nastavení datových pinů pro MH-Z19 - CO2
#define rx_pin  10
#define tx_pin  9
#define pwmpin  6
// nastavení datoveho pinu pro AM2120 - Teplomer
#define pinDHT  5

#define deviceID 1 // mente dle potreby, je pouzivano jako id zarizeni

// nastaveni komunikace
//==============
RF24 radio(CE_PIN, CSN_PIN); // nRF24L01 (CE,CSN)

const byte address[6] = {'0','0','0','0','1'};   // Address of this node
char dataReceived[10]; // this must match dataToSend in the TX
bool newData = false;
int ackData[2] = {1, -1}; // the two values to be sent to the master
struct {
  float teplota;
  float vlhkost;
  int co2ppm;
  int co2ppm2;
  int teplota2;
} dataStruct;

// nastaveni sensoru
//==============
// vytvoření instance senzoru z knihovny,
// s nastavením typu DHT22, který má stejný
// typ komunikace jako AM2120
DHT dht(pinDHT, DHT22);
// vytvoření objektů z knihovny
MHZ19 *mhz19_uart = new MHZ19(rx_pin, tx_pin);
MHZ19 *mhz19_pwm = new MHZ19(pwmpin);
//==============

void setup() {
    dataStruct.teplota = 20.1f;
    dataStruct.vlhkost = 25.5f;
    dataStruct.co2ppm = 400;
    dataStruct.co2ppm2 = 420;
    dataStruct.teplota2 = 15;

    Serial.begin(9600);
    Serial.println("SimpleRxAckPayload Starting");

    // sensor setup
    //==============
    // zahájení komunikace se senzorem AM2120
    dht.begin();
    // zahájení komunikace se senzorem MH-Z19 přes UART
    mhz19_uart->begin(rx_pin, tx_pin);
    // vypnutí autokalibrace
    mhz19_uart->setAutoCalibration(false);

    // communication setup
    //==============
    radio.begin();
    radio.setDataRate( RF24_2MBPS );
    radio.setChannel(125);
    radio.openReadingPipe(1, address);
    radio.enableAckPayload();
    radio.startListening();
    
    radio.writeAckPayload(1, &ackData, sizeof(ackData)); // pre-load data
}

//==========
void loop() {
    getData();
    showData();
}

//============
void getData() {
    if ( radio.available() ) {
        radio.read( &dataReceived, sizeof(dataReceived) );
        updateReplyData();
        newData = true;
    }
}

//================
void showData() {
    if (newData == true) {
        Serial.print("Data received ");
        Serial.println(dataReceived);
        Serial.print(" ackPayload sent ");
        Serial.print(ackData[0]);
        Serial.print(", ");
        Serial.println(ackData[1]);
        newData = false;
    }
}

//================
void updateReplyData() {
  //radio.writeAckPayload(1, &ackData, sizeof(ackData)); // load the payload for the next time
//     String message = "";
//     message.concat(deviceID);
//     message.concat(";");
//     message.concat(handleDHT());
//     message.concat(handleMHZ19());
     // sends data in this format:
//     // (float)(AM2120 - teplota); (float)(AM2120 - vlhost); (int)(MH-Z19 - UART - CO2); (int)(MH-Z19 - PWM - CO2); (int)(MH-Z19 - teplota);
//     sendString(message);
  Serial.println(sizeof(dataStruct));
  radio.writeAckPayload(1, &dataStruct, sizeof(dataStruct));

}

// cte hodnoty z AM2120
//==========
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
//==========
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
  result.concat(";");
//  Serial.print(result);
  return result;
}

//==========
void sendString(String message)
{
  byte messageLength = message.length() + 1;

  // convert string to char array
  char charBuffer[messageLength];
  message.toCharArray(charBuffer, messageLength);

  digitalWrite(13, true);
  // Serial.print("sent: " + message);
  Serial.println(message);
  Serial.println(sizeof(ackData));
  Serial.println(sizeof(message));

  radio.writeAckPayload(1, &ackData, sizeof(ackData)); // load the payload for the next time
  digitalWrite(13, false);
}
