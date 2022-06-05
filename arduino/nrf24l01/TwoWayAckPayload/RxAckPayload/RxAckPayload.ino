// SimpleRxAckPayload- the slave or the receiver

#include <RF24.h>
#include <nRF24L01.h>
#include <SPI.h>

#define CE_PIN  7
#define CSN_PIN 8

RF24 radio(CE_PIN, CSN_PIN); // nRF24L01 (CE,CSN)

const byte address[6] = {'0','0','0','0','1'};   // Address of this node
int ackData[2] = {1, -1}; // the two values to be sent to the master

char dataReceived[10]; // this must match dataToSend in the TX
bool newData = false;

//==============

void setup() {

    Serial.begin(9600);

    Serial.println("SimpleRxAckPayload Starting");
    radio.begin();
    radio.setDataRate( RF24_250KBPS );
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
    radio.writeAckPayload(1, &ackData, sizeof(ackData)); // load the payload for the next time
}
