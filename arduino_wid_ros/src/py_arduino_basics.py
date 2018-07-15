#!/usr/bin/env python

''' * Author: Srikanth Peetha
    * Created on: July 10, 2018 '''

import pyfirmata
import time

#arduino is connected to the usb port named "ttyUSB0".
#Run the command "dmesg | grep tty" to find your port name

board = pyfirmata.Arduino('/dev/ttyUSB0')
print "Started.."

led_1 = board.get_pin('d:13:o')
led_2_in = board.get_pin('d:5:o')
led_2_out = board.get_pin('d:7:o')

while( str( raw_input("Do you want to operate LED(y/n): ")) == "y"):

	i = int(raw_input("Select your option\n(1)On (2)OFF  :"))

	if(i == 1):
		print i
		led_1.write(1)
		led_2_in.write(1)
		led_2_out.write(0)

	if (i == 2):
		print i
		led_1.write(0)
		led_2_in.write(0)
		led_2_out.write(0)
	else:
		print "invalid input"

print " Done "





