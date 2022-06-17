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
const uint16_t child_nodes[MAX_NETWORK_DEPTH][MAX_CHILD_COUNT] = {{this_node, 01, 02}};   // {{this_node, 01, 02}, {010, 011}, {020}, {030, 031}}

void setup() {
  Serial.begin(9600);
  SPI.begin();
  radio.begin();
  network.begin(90, this_node);  //(channel, node address)
  // radio.setDataRate(RF24_2MBPS);
}

void loop() {
  network.update();
  delay(2000);
  network.update();
  while ( network.available() ) {     // Is there any incoming data?
    Serial.print("data");
    RF24NetworkHeader header;
    long incomingData;
    network.read(header, &incomingData, sizeof(incomingData)); // Read the incoming data
    Serial.print(header.to_node);
    Serial.print(" - ");
    Serial.println(incomingData);
  }

  for (int i = 0; i < MAX_NETWORK_DEPTH; i++) {
    // Serial.print(i);
    // Serial.print(" - ");
    for (int j = 0; j < MAX_CHILD_COUNT; j++) {
      uint16_t node_address = child_nodes[i][j];
      if (node_address != 0) {
        RF24NetworkHeader header(node_address);
        // Serial.print(header.to_node);
        // Serial.print(" - ");
        char get_message = 'g';
        bool ok = network.write(header, &get_message, sizeof(get_message));
        
        if (ok) {
         //  Serial.print("Suc; ");
        } else {
         //  Serial.print("Err; ");
        }
        delay(100);
      }
    }
   //  Serial.println();
  }
 //  Serial.println(); 
}
