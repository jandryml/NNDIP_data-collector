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
unsigned long txIntervalMillis = 5000; // send once per 5 second

//===============

void setup() {
    Serial.begin(9600);
    Serial.println("SimpleTxAckPayload Starting");

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

    Serial.print("Data Sent ");
    Serial.print(slaveId);
    Serial.println();
    if (rslt) {
        if ( radio.isAckPayloadAvailable() ) {
            radio.read(&dataStruct, sizeof(dataStruct));
            newData = true;
            showData();
        }
        else {
            Serial.println("  Acknowledge but no data ");
        }
        
    }
    else {
        Serial.println("  Tx failed");
    }

    prevMillis = millis();
 }


//=================

void showData() {
    if (newData == true) {
        Serial.print("  Acknowledge data teplota:");
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
        newData = false;
    }
}

//================

void updateMessage(int txNum) {
    slaveId = txNum;
}
