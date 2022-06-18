#include <RF24.h>
#include <nRF24L01.h>
#include <SPI.h>

#define CE_PIN  7
#define CSN_PIN 8
#define MAX_CHILD_COUNT 5

RF24 radio(CE_PIN, CSN_PIN);      // nRF24L01 (CE,CSN)

const byte firstNode [5] = {'0','0','0','0','1'};
const byte secondNode [5] = {'0','0','0','0','2'};

int slaveId = 0;
bool newData = false;

struct {
  float teplota;
  float vlhkost;
  int co2ppm;
  int co2ppm2;
  int teplota2;
} dataStruct;

unsigned long currentMillis;
unsigned long prevMillis;
unsigned long txIntervalMillis = 5000; // send once per second

//===============

void setup() {
    Serial.begin(9600);
    // Serial.println("SimpleTxAckPayload Starting");

    radio.begin();
    radio.setDataRate( RF24_2MBPS );
    radio.setChannel(125);

    radio.enableAckPayload();

    radio.setRetries(150,5); // delay, count
}

//=============

void loop() {
radio.openWritingPipe(secondNode);
    currentMillis = millis();
    if (currentMillis - prevMillis >= txIntervalMillis) {
        updateMessage(1);
        send(firstNode);
        delay(100);
        updateMessage(2);
        send(secondNode);
    }
}

//================

void send(byte address []) {
    radio.openWritingPipe(address);
    bool rslt;
    rslt = radio.write( &slaveId, sizeof(slaveId) );
        // Always use sizeof() as it gives the size as the number of bytes.
        // For example if dataToSend was an int sizeof() would correctly return 2

    // Serial.print("Data Sent ");
    // Serial.print(slaveId);
    // Serial.println();
    if (rslt) {
        if ( radio.isAckPayloadAvailable() ) {
            radio.read(&dataStruct, sizeof(dataStruct));
            newData = true;
            showData();
        }
        else {
            // Serial.println("  Acknowledge but no data ");
        }
        
    }
    else {
        // Serial.println("  Tx failed");
    }

    prevMillis = millis();
 }


String handleThisShit() {
  String result = "";

  if (dataStruct.teplota == 0 && dataStruct.vlhkost == 0) {
    result.concat(";;");
  } else {
  result.concat(dataStruct.teplota);
  result.concat(";");

  result.concat(dataStruct.vlhkost);
  result.concat(";");
  }
  return result;
}

String handleThatShit() {
  String result = "";
  
  if (dataStruct.co2ppm == 0 && dataStruct.co2ppm2 == 0 && dataStruct.teplota2 == 0) {
    result.concat(";;;");
  } else {
  result.concat(dataStruct.co2ppm);
  result.concat(";");

  result.concat(dataStruct.co2ppm2);
  result.concat(";");

  result.concat(dataStruct.teplota2);
  result.concat(";");
  }
  return result;
}


//=================

void showData() {
    if (newData == true) {
       // (float)(AM2120 - teplota); (float)(AM2120 - vlhost); (int)(MH-Z19 - UART - CO2); (int)(MH-Z19 - PWM - CO2); (int)(MH-Z19 - teplota);
      String message = "1;";
      message.concat(handleThisShit());
      message.concat(handleThatShit());
      Serial.println(message);
      newData = false;
    }
}

//================

void updateMessage(int txNum) {
    slaveId = txNum;
}
