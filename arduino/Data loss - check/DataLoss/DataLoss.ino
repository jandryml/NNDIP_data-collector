// 433 MHz Přijímač

// připojení knihovny
#include <VirtualWire.h>

int x = 0;

void setup()
{
  // inicializace komunikace po sériové lince
  Serial.begin(9600);
}

void loop()
{
  char znaky [64];
  snprintf(znaky, sizeof(znaky) - 1, "1;%d;\n", x++);
  Serial.write(znaky);
  delay(1000);
}
