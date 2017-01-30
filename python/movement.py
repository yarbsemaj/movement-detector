import RPi.GPIO as GPIO
import requests
import os.path as path
import time

def movement (deviceName):
        r = requests.post("http://bedmovement.yarbsemaj.com/update.php",data={'Name':deviceName},timeout=1)
        # print(r.status_code, r.reason)
        print "Update Sent"


GPIO.setmode(GPIO.BOARD)

GPIO.setwarnings(False)

GPIO.setup(12, GPIO.IN)
GPIO.setup([16,18],GPIO.OUT)

deviceNamePath="deviceName.txt"

if path.isfile(deviceNamePath):
    file=open(deviceNamePath,"r")
    deviceName=file.read()
    file.close
else:
    r = requests.post("http://bedmovement.yarbsemaj.com/newUser.php",data={'Delay':'60'},timeout=1)
    file=open(deviceNamePath,"w")
    deviceName=r.json()["name"]
    file.write(deviceName)
    file.close
    
print "Hello, my name is:",deviceName

movement(deviceName)
sensorValue=GPIO.input(12)

lastTime=time.time()

while 1==1:
    if lastTime+90 < time.time():
	GPIO.output(16,1)
    else:
	GPIO.output(16,0)

    GPIO.output(18,sensorValue)
    if sensorValue!=GPIO.input(12):
        movement(deviceName)
	lastTime=time.time()
    sensorValue=GPIO.input(12)


