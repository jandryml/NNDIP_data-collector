/*
  Arduino Wireless Network - Multiple NRF24L01 Tutorial
 == Example 01 - Servo Control / Node 00 - Potentiometer ==
  by Dejan, www.HowToMechatronics.com
  Libraries:
  nRF24/RF24, https://github.com/nRF24/RF24
  nRF24/RF24Network, https://github.com/nRF24/RF24Network
*/
#include <RF24.h>
#include <RF24Network.h>
#include <SPI.h>

#define CE_PIN  7
#define CSN_PIN 8
#define MAX_CHILD_COUNT 5
#define MAX_NETWORK_DEPTH 5

RF24 radio(CE_PIN, CSN_PIN);      // nRF24L01 (CE,CSN)
RF24Network network(radio);      // Include the radio in the network
const uint16_t this_node = 00;   // Address of this node in Octal format ( 04,031, etc)
const uint16_t node01 = 01;      

long preciousData = 0;

void setup() {
  Serial.begin(9600);
  SPI.begin();
  radio.begin();
  network.begin(90, this_node);  //(channel, node address)
}

void loop() {
  network.update();
  while ( network.available() ) {     // Is there any incoming data?
    RF24NetworkHeader header;
    unsigned long incomingData;
    network.read(header, &incomingData, sizeof(incomingData)); // Read the incoming data
    Serial.println("DATA");
    //Serial.println(incomingData);
  }
  
  long angleValue = preciousData++; // Convert the value to 0-180
  RF24NetworkHeader header(node01);     // (Address where the data is going)
  bool ok = network.write(header, &angleValue, sizeof(angleValue)); // Send the data
  // Serial.println(angleValue);
  delay(1000);
}
