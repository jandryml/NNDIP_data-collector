// Detektor CO2 MH-Z19

// připojení potřebné knihovny
#include "MHZ19.h"
// nastavení propojovacích pinů
#define rx_pin 11
#define tx_pin 10
#define pwmpin 9
// vytvoření objektů z knihovny
MHZ19 *mhz19_uart = new MHZ19(rx_pin, tx_pin);
MHZ19 *mhz19_pwm = new MHZ19(pwmpin);

void setup() {
  // zahájení komunikace po sériové lince
  Serial.begin(9600);
  // zahájení komunikace se senzorem přes UART
  mhz19_uart->begin(rx_pin, tx_pin);
  // vypnutí autokalibrace
  mhz19_uart->setAutoCalibration(false);
}

void loop() {
  // zahájení měření senzoru a načtení výsledků do proměnné
  measurement_t m = mhz19_uart->getMeasurement();
  int co2ppm = mhz19_pwm->getPpmPwm();
  // vytištění naměřených údajů
  Serial.print("Koncentrace CO2: ");
  Serial.print(map(m.co2_ppm, 0, 5000, 0, 2000));
  Serial.print("ppm, mereni pres PWM: ");
  Serial.print(co2ppm);
  Serial.print("ppm, teplota: ");
  Serial.print(m.temperature);
  Serial.println("stC");
  // pauza před novým měřením
  delay(5000);
}
