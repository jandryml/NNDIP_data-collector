#include <SPI.h>
#include <nRF24L01.h>
#include <RF24.h>

//
// Hardware configuration
//

RF24 radio(7, 8); // CE, CSN

const uint64_t rAddress = 0x5F4E4F444531;

unsigned int message = 0;

void setup(void) {
  Serial.begin(9600);
  Serial.println("SimpleSender");

  setupRadio();
}

void setupRadio() {
  radio.begin();
  
  radio.setChannel(108);
  radio.setPALevel(RF24_PA_MIN);
  radio.setDataRate(RF24_250KBPS);
  //radio.setRetries(15, 15); // set the number of retries and interval between retries to the maximum
  radio.openWritingPipe(rAddress);
  
  radio.stopListening();
}

void loop(void) {
    if(radio.write(&message, sizeof(message))) {
      Serial.print("Sent: ");
      Serial.println(message);
    } else {
      Serial.print("Send failed: ");
      Serial.println(message);
      // radio.printDetails();
    }
    
    message++;
    delay(1000);
}
