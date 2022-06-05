// SimpleRxAckPayload- the slave or the receiver

#include <RF24.h>
#include <RF24Network.h>
#include <SPI.h>
#include "printf.h"

#define CE_PIN  7
#define CSN_PIN 8
#define SERIAL_DEBUG

RF24 radio(CE_PIN, CSN_PIN); // nRF24L01 (CE,CSN)
RF24Network network(radio); // Include the radio in the network

const uint16_t master_node = 00;  // Address of master node in Octal format ( 04,031, etc)
const uint16_t this_node= 01;     // Address of this node in Octal format 

long veryValuableValue = 0;
bool dataPresent = false;
void setup() {
  printf_begin();
  Serial.begin(9600);
  SPI.begin();
  radio.begin();
  network.begin(90, this_node);  //(channel, node address)
  // radio.setDataRate(RF24_2MBPS);
}

void loop() {
  network.update();
  
  //===== Receiving =====//
  while ( network.available() ) {     // Is there any incoming data?
    RF24NetworkHeader header;
    char incomingData;

    network.read(header, &incomingData, sizeof(incomingData)); // Read the incoming data
    Serial.print(header.to_node);
    Serial.print(" - ");
    Serial.print(incomingData);
    Serial.print(" - ");
    network.update();
    delay(100);
    Serial.println();
    dataPresent = true;
  }

  if(dataPresent) {
    RF24NetworkHeader master_header(master_node);
    bool ok = network.write(master_header, &veryValuableValue, sizeof(veryValuableValue));
    if (ok) {
      Serial.println("Suc");
    } else {
      Serial.println("Err");
    }
    veryValuableValue++;

    dataPresent = false;
  }
  
  delay(400);
}
