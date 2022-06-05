#include <RF24.h>
#include <nRF24L01.h>
#include <SPI.h>

#define CE_PIN  7
#define CSN_PIN 8
#define MAX_CHILD_COUNT 5

RF24 radio(CE_PIN, CSN_PIN);      // nRF24L01 (CE,CSN)

const byte firstNode [5] = {'0','0','0','0','1'};
const byte secondNode [5] = {'0','0','0','0','2'};

char dataToSend[10] = "M0";
char txNum = '0';
int ackData[2] = {0, 0}; // to hold the two values coming from the slave
int ackData2[2] = {0, 0};
bool newData = false;

unsigned long currentMillis;
unsigned long prevMillis;
unsigned long txIntervalMillis = 2000; // send once per second

//===============

void setup() {
    Serial.begin(9600);
    Serial.println("SimpleTxAckPayload Starting");

    radio.begin();
    radio.setDataRate( RF24_2MBPS );
    radio.setChannel(125);

    radio.enableAckPayload();

    radio.setRetries(5,5); // delay, count
}

//=============

void loop() {
radio.openWritingPipe(secondNode);
    currentMillis = millis();
    if (currentMillis - prevMillis >= txIntervalMillis) {
        updateMessage('1');
        send(firstNode);
        updateMessage('2');
        send(secondNode);
    }
}

//================

void send(byte address []) {
    radio.openWritingPipe(address);
    bool rslt;
    rslt = radio.write( &dataToSend, sizeof(dataToSend) );
        // Always use sizeof() as it gives the size as the number of bytes.
        // For example if dataToSend was an int sizeof() would correctly return 2

    Serial.print("Data Sent ");
    Serial.print(dataToSend);
    Serial.println();
    if (rslt) {
        if ( radio.isAckPayloadAvailable() ) {
            radio.read(&ackData, sizeof(ackData));
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
        Serial.print("  Acknowledge data ");
        Serial.print(ackData[0]);
        Serial.print(", ");
        Serial.println(ackData[1]);
        Serial.println();
        newData = false;
    }
}

//================

void updateMessage(char txNum) {
    dataToSend[2] = txNum;
}
