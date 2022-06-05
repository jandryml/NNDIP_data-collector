// SimpleRxAckPayload- the slave or the receiver

#include <RF24.h>
#include <RF24Network.h>
#include <SPI.h>

#define CE_PIN  7
#define CSN_PIN 8

RF24 radio(CE_PIN, CSN_PIN); // nRF24L01 (CE,CSN)
RF24Network network(radio);      // Include the radio in the network
const uint16_t this_node = 01;   // Address of our node in Octal format ( 04,031, etc)

void setup() {
  Serial.begin(9600);
  SPI.begin();
  radio.begin();
  network.begin(90, this_node); //(channel, node address)
}

void loop() {
  network.update();
  while ( network.available() ) {     // Is there any incoming data?
    RF24NetworkHeader header;
    long incomingData;
    network.read(header, &incomingData, sizeof(incomingData)); // Read the incoming data
    Serial.println(incomingData);
    RF24NetworkHeader header2(00);
    network.write(header2, &incomingData, sizeof(incomingData));
  }
}
