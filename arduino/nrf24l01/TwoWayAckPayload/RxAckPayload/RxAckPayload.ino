// SimpleRxAckPayload- the slave or the receiver

// knihovny pro nrf24l01
//================
#include <RF24.h>
#include <nRF24L01.h>
#include <SPI.h>
//================
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
//================
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
//================
// vytvoření instance senzoru z knihovny,
// s nastavením typu DHT22, který má stejný
// typ komunikace jako AM2120
DHT dht(pinDHT, DHT22);
// vytvoření objektů z knihovny
MHZ19 *mhz19_uart = new MHZ19(rx_pin, tx_pin);
MHZ19 *mhz19_pwm = new MHZ19(pwmpin);
//================

void setup() {
    cleanData();

    Serial.begin(9600);
    Serial.println("SimpleRxAckPayload Starting");

    // sensor setup
    //================
    // zahájení komunikace se senzorem AM2120
    dht.begin();
    // zahájení komunikace se senzorem MH-Z19 přes UART
    mhz19_uart->begin(rx_pin, tx_pin);
    // vypnutí autokalibrace
    mhz19_uart->setAutoCalibration(false);

    // communication setup
    //================
    radio.begin();
    radio.setDataRate( RF24_2MBPS );
    radio.setChannel(125);
    radio.openReadingPipe(1, address);
    radio.enableAckPayload();
    radio.startListening();
    
    radio.writeAckPayload(1, &ackData, sizeof(ackData)); // pre-load data
}

//================
void loop() {
  getData();
  if (newData == true) {
    showData();
    cleanData();
  }
}

//================
void getData() {
    if ( radio.available() ) {
        radio.read( &dataReceived, sizeof(dataReceived) );
        updateReplyData();
        newData = true;
    }
}

//================
void updateReplyData() {
  //radio.writeAckPayload(1, &ackData, sizeof(ackData)); // load the payload for the next time
  handleDHT();
  handleMHZ19();

  radio.writeAckPayload(1, &dataStruct, sizeof(dataStruct));
}

// cte hodnoty z AM2120
//================
void handleDHT() {
    // načtení teploty do proměnné
  float teplota = dht.readTemperature();
  // načtení vlhkosti do proměnné
  float vlhkost = dht.readHumidity();
  // kontrola, jestli jsou načtené hodnoty čísla, pomocí funkce isnan
  if (isnan(teplota) || isnan(vlhkost)) {
    // při chybném čtení prazdne hodnoty
    Serial.println("Err: handling DHT");
  } else {
    dataStruct.teplota = teplota;
    dataStruct.vlhkost = vlhkost;
  //  Serial.println("Succ: DHT values refreshed");
  }
}

// cte hodnoty z MH-Z19
//================
void handleMHZ19() {
  // zahájení měření senzoru a načtení výsledků do proměnné
  
  // Serial.println(mhz19_uart->getStatus());
  if (mhz19_uart->getStatus() == 0) {
  measurement_t m = mhz19_uart->getMeasurement();
  int co2ppm = mhz19_pwm->getPpmPwm();
  // CO2 mereni
  dataStruct.co2ppm = map(m.co2_ppm, 0, 5000, 0, 2000);
  // CO2 mereni pres PWM
  dataStruct.co2ppm2 = co2ppm;
  // teplota
  dataStruct.teplota2 = m.temperature;
  // Serial.println("Succ: MHZ19 values refreshed");
  } else {
    Serial.println("Err: handling MHZ19");
  }
}

//================
void showData() {
        Serial.print("Data received ");
        Serial.println(dataReceived);
        printSensorData();
}

//================
void printSensorData() {
  Serial.print(" Send data:");
  Serial.print(dataStruct.teplota);
  Serial.print("; vhlkost: ");
  Serial.print(dataStruct.vlhkost);
  Serial.print("; co2ppm: ");
  Serial.print(dataStruct.co2ppm);
  Serial.print("; co2ppm2: ");
  Serial.print(dataStruct.co2ppm2);
  Serial.print("; teplota2: ");
  Serial.print(dataStruct.teplota2);
  Serial.println();
}

//================
void cleanData() {
  dataStruct.teplota = 0.0f;
  dataStruct.vlhkost = 0.0f;
  dataStruct.co2ppm = 0;
  dataStruct.co2ppm2 = 0;
  dataStruct.teplota2 = 0;
  newData = false;
}
