import serial
import string
import time
#opening serial port
ser=serial.Serial('/dev/ttyUSB0', 9600)
#Here /dev/ttyUSB2 is used
#It can be different in your case like /dev/ttyUSB0, /dev/ttyUSB1 etc.
while True:
    serialdata=ser.readline()
    print(serialdata)
#read serial data and print it on screen
