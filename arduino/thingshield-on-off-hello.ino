//*****************************************************************************
/// @file
/// @brief
///   Arduino SmartThings Shield LED Example 
//*****************************************************************************
#include <SoftwareSerial.h>   //TODO need to set due to some weird wire language linker, should we absorb this whole library into smartthings
#include <SmartThings.h>


#define PIN_THING_RX    3
#define PIN_THING_TX    2


SmartThingsCallout_t messageCallout;    // call out function forward decalaration
SmartThings smartthing(PIN_THING_RX, PIN_THING_TX, messageCallout);  // constructor


int ledPin = 13;
bool isDebugEnabled;    // enable or disable debug in this example
int stateLED;           // state to track last set value of LED

void setup()
{
  // setup default state of global variables
  isDebugEnabled = true;
  stateLED = 0;                 // matches state of hardware pin set below
  
  // setup hardware pins 
  pinMode(ledPin, OUTPUT);     // define PIN_LED as an output
  digitalWrite(ledPin, LOW);   // set value to LOW (off) to match stateLED=0

  if (isDebugEnabled)
  { // setup debug serial port
    Serial.begin(9600);         // setup serial with a baud rate of 9600
    Serial.println("setup..");  // print out 'setup..' on start
  }
}


void loop()
{
  // run smartthing logic
  smartthing.run();
}

void on()
{
  stateLED = 1;                 // save state as 1 (on)
  digitalWrite(ledPin, HIGH);  // turn LED on
  smartthing.shieldSetLED(0, 0, 1);
  smartthing.send("on");        // send message to cloud
  Serial.println("on");
}

void off()
{
  stateLED = 0;                 // set state to 0 (off)
  digitalWrite(ledPin, LOW);   // turn LED off
  smartthing.shieldSetLED(0, 0, 0);
  smartthing.send("off");       // send message to cloud
  Serial.println("off");
}

void hello()
{
  Serial.println("brobasaur");
  smartthing.send("colors!");
  smartthing.shieldSetLED(1, 0, 0);
  delay(200);
  smartthing.shieldSetLED(0, 1, 0);
  delay(200);
  smartthing.shieldSetLED(0, 0, 1);
  delay(200);
  smartthing.shieldSetLED(1, 1, 0);
  delay(200);
  smartthing.shieldSetLED(1, 1, 1);
  delay(200);
  smartthing.shieldSetLED(1, 0, 1);
  delay(200);
  smartthing.shieldSetLED(0, 1, 1);
  delay(200);
  smartthing.shieldSetLED(3, 2, 1);
  delay(200);
  smartthing.shieldSetLED(1, 2, 3);
  delay(200);
  smartthing.shieldSetLED(2, 2, 4);
  delay(200);
  smartthing.shieldSetLED(4, 3, 1);
     delay(200);
  smartthing.shieldSetLED(0, 0, 0);
  smartthing.send("fancy");
  
}


void messageCallout(String message)
{
  // if debug is enabled print out the received message
  if (isDebugEnabled)
  {
    Serial.print("Received message: '");
    Serial.print(message);
    Serial.println("' ");
  }

  // if message contents equals to 'on' then call on() function
  // else if message contents equals to 'off' then call off() function
  if (message.equals("on"))
  {
    on();
  }
  else if (message.equals("off"))
  {
    off();
  }
  else if (message.equals("hello"))
  {
    hello();
  }
}
