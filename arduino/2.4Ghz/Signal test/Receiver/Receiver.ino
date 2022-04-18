#include <SPI.h>
#include <nRF24L01.h>
#include <RF24.h>

//
// Hardware configuration
//

RF24 radio(7, 8); // CE, CSN

const uint64_t rAddress = 0x5F4E4F444531;

void setup(void) {
  Serial.begin(9600);
  Serial.println("SimpleReceiver");

  setupRadio();
}

void setupRadio() {
  radio.begin();
  
  radio.setChannel(108);
  radio.setPALevel(RF24_PA_MIN);
  radio.setDataRate(RF24_250KBPS);
//  radio.setRetries(15, 15); // set the number of retries and interval between retries to the maximum
  radio.openReadingPipe(1, rAddress);
  
  radio.startListening();
}

void loop(void) {
  // if there is data ready
  uint8_t pipe;
  unsigned int message;
  
  if (radio.available(&pipe) ) {
    Serial.print("Data ready on pipe: " );
    Serial.println(pipe);
    
    radio.read(&message, sizeof(message));
    Serial.print("RECEIVED ");
    Serial.println(message);
  } else {
    Serial.println("Not present!");
  }
  

  delay(990);
}
