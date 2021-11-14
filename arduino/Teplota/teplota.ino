// Teploměr a vlhkoměr AM2120

// připojení potřebné knihovny
#include "DHT.h"
// nastavení čísla propojovacího pinu
#define pinDHT 5
// vytvoření instance senzoru z knihovny,
// s nastavením typu DHT22, který má stejný
// typ komunikace jako AM2120
DHT dht(pinDHT, DHT22);

void setup() {
  // inicializace komunikace po sériové lince
  Serial.begin(9600);
  // zahájení komunikace se senzorem DHT
  dht.begin();
}

void loop() {
  // načtení teploty do proměnné
  float teplota = dht.readTemperature();
  // načtení vlhkosti do proměnné
  float vlhkost = dht.readHumidity();
  // kontrola, jestli jsou načtené hodnoty čísla, pomocí funkce isnan
  if (isnan(teplota) || isnan(vlhkost)) {
    // při chybném čtení vypiš hlášku
    Serial.println("Chyba při čtení z DHT senzoru!");
  } else {
    // pokud jsou hodnoty v pořádku,
    // vypiš je po sériové lince
    Serial.print("Teplota: ");
    Serial.print(teplota);
    Serial.print(" stupnu Celsia, ");
    Serial.print("vlhkost: ");
    Serial.print(vlhkost);
    Serial.println("% RH.");
  }
  // pauza před novým během smyčky
  delay(1000);
}
