// 433 MHz Přijímač

// připojení knihovny
#include <VirtualWire.h>

  // vytvoření proměnných pro uložení
  // přijaté zprávy a její délky,
  // délka je maximálně 78 znaků
  
  

void setup()
{
  // inicializace komunikace po sériové lince
  Serial.begin(9600);
  // nastavení typu bezdrátové komunikace
  //vw_set_ptt_inverted(true);
  // nastavení rychlosti přenosu v bitech za sekundu
  vw_setup(1000);
  // nastavení čísla datového pinu pro přijímač
  vw_set_rx_pin(7);
  // nastartování komunikace po nastaveném pinu
  vw_rx_start();
}

void readMessage()
{
  byte messageLength = VW_MAX_MESSAGE_LEN;
  byte message[messageLength];
  //
  if (vw_get_message(message, &messageLength)) // non-blocking
  {
    digitalWrite(13, true);
    for (int i = 0; i < messageLength; i++)
    {
      Serial.write(message[i]);
    }
    digitalWrite(13, true);
  }
}

void loop()
{
  readMessage();
}
