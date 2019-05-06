
// Include firebase library
#include <Firebase.h>

#include <FirebaseArduino.h>

#include <FirebaseCloudMessaging.h>

#include <FirebaseError.h>

#include <FirebaseHttpClient.h>

#include <FirebaseObject.h>

//Include time library
#include <time.h>

//note library
#include "pitches.h"

// Include microcontroller NodeMcu ESP8266
#include <ESP8266WiFi.h>

// initial wifi
//#define WIFI_SSID "Linksys Ganteng"
//#define WIFI_PASSWORD "wajibdiisi"

//#define WIFI_SSID "G-House"
//#define WIFI_PASSWORD "Ghouse246810"

//#define WIFI_SSID "WIN-NBRNAFILN2H 0033"
//#define WIFI_PASSWORD "123456784"

#define FIREBASE_HOST  "smartdoor-7d0e6.firebaseio.com"
#define FIREBASE_AUTH  "MvOSBwK2Rn7cwk8ktZT6qzA6an1Bw4hX41t6INi7"

//note
int melody[] = {NOTE_C4, NOTE_G3, NOTE_G3, NOTE_A3, NOTE_G3, 0, NOTE_B3, NOTE_C4};

//note durations. 4=quarter note / 8=eighth note
int noteDurations[] = {4, 8, 8, 4, 4, 4, 4, 4};

//set pin number
int WIFI_LED = D0;

int door01 = D1;

int doorLedRed = D2;

int doorStatus01 = D3;

int doorLedGreen = D4;

int pushButton = D5;

int pushBell = D6;

int buzzer = D7;


//variables
int wifiStatus;

int buttonState = 0;

int pushState = 0;

int redVal;

int greenVal;

int blueVal;

int count;

int state01;

int state02;

int threshold = 500;

int timezone = 7 * 3600;

int dst = 0;

unsigned long previousMillis = 0;

const long interval = 1000;

unsigned long lastMillis1;

unsigned long lastMillis2;

unsigned long times = 1554907986666;

long currentDelay = 0;

bool isWait = false;

int analogValue = 0;

float voltage = 0;

void setup() {
  // put your setup code here, to run once:

  pinMode(door01, OUTPUT);

  pinMode(doorStatus01, INPUT);

  pinMode(pushButton, INPUT);

  pinMode(pushBell, INPUT);

  pinMode(buzzer, OUTPUT);

  pinMode(WIFI_LED, OUTPUT);

  pinMode(doorLedRed, OUTPUT);

  pinMode(doorLedGreen, OUTPUT);

  redVal = 255;

  greenVal = 255;

  blueVal = 255;

  Serial.begin(9600);
  /* Set ESP8266 to WiFi Station mode */
  WiFi.mode(WIFI_STA);
  /* start SmartConfig */
  WiFi.beginSmartConfig();

  /* Wait for SmartConfig packet from mobile */
  Serial.println("Waiting for SmartConfig.");

  while (!WiFi.smartConfigDone()) {

    delay(500);

    Serial.print(".");

  }

  Serial.println("");

  Serial.println("SmartConfig done.");

  /* Wait for WiFi to connect to AP */
  Serial.println("Waiting for WiFi");

  while (WiFi.status() != WL_CONNECTED) {

    delay(500);

    Serial.print(".");

  }

  Serial.println("WiFi Connected.");

  Serial.print("IP Address: ");

  Serial.println(WiFi.localIP());

  configTime(timezone, dst, "id.pool.ntp.org",
             "https://www.worldtimeserver.com/current_time_in_ID.aspx?city=Jakarta");

  Serial.println("\nWaiting for Internet time");

  while (!time(nullptr)) {

    Serial.print("*");

    delay(1000);

  }

  Serial.println("\nTime response....OK");

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  //Send data to firebase
  //Device
  Firebase.setBool("Devices/mGFjsGhS/house_lock", false);
  //Door 1
  Firebase.setInt("Devices/mGFjsGhS/Doors/D1/doorLock", 1);
  Firebase.setString("Devices/mGFjsGhS/Doors/D1/doorPin", "D1");
  //Guest
  Firebase.setBool("Devices/mGFjsGhS/guest", false);
  //thief
  Firebase.setBool("Devices/mGFjsGhS/thief", false);
  //Door status
  Firebase.setString("Devices/mGFjsGhS/Doors/D1/status", "Open");

}

int valueDoor01 = 0;

void loop() {
  // put your main code here, to run repeatedly:

  times = millis();

  wifiStatus = WiFi.status();

  time_t now = time(nullptr);

  struct tm* p_tm = localtime(&now);

  String str = String(p_tm->tm_mday) + '/' +
               String(p_tm->tm_mon + 1) + '/' +
               String(p_tm->tm_year + 1900) + " " +
               String(p_tm->tm_hour) + ':' +
               String(p_tm->tm_min);
  //  + ':' + String(p_tm->tm_sec);

  if (wifiStatus == WL_CONNECTED) {

    Serial.println("");

    Serial.println("Your ESP is connected!");

    Serial.println("Your IP address is: ");

    Serial.println(WiFi.localIP());

    digitalWrite(WIFI_LED, HIGH);

    Firebase.setBool("Devices/mGFjsGhS/connect", true);

    Firebase.setString("Devices/mGFjsGhS/Doors/D1/connect", str);

  }

  else {

    Serial.println("");

    Serial.println("WiFi not connected");

    digitalWrite(WIFI_LED, LOW);

    Firebase.setBool("Devices/mGFjsGhS/connect", false);

  }

  delay(100); // check for connection every once a second

  if (valueDoor01 == 1) {

    digitalWrite(doorLedGreen, HIGH);

    digitalWrite(doorLedRed, LOW);

    Serial.println(valueDoor01);

  }

  else {

    digitalWrite(doorLedGreen, LOW);

    digitalWrite(doorLedRed, HIGH);


  }

  eventDoorA();

  dateToday();

  analogValue = analogRead(A0);

  voltage = 0.0048 * analogValue;

  if ( voltage >= 1.6 ){
    
  }
  
  else if (voltage > 1.2 && voltage < 1.6){
    
  }
  
  else if ( voltage <= 1.2){
    
  }

}

void eventDoorA() {

  valueDoor01 = Firebase.getInt("Devices/mGFjsGhS/Doors/D1/doorLock");

  buttonState = digitalRead(pushButton);

  state01 = digitalRead(doorStatus01);

  pushState = digitalRead(pushBell);

  if (valueDoor01 == 1 && state01  == HIGH && !isWait) {

    isWait = true;

    tone(buzzer, 50);

    delay(10);

    noTone(buzzer);

    digitalWrite(door01, LOW);

    //    digitalWrite(doorLedGreen, HIGH);
    //
    //    digitalWrite(doorLedRed, LOW);

    Firebase.setString("Devices/mGFjsGhS/Doors/D1/status", "Closed");

    Firebase.setBool("Devices/mGFjsGhS/thief", false);

  }

  else if (valueDoor01 == 1 &&  state01 == LOW ) {

    isWait = false;

    digitalWrite(door01, LOW);

    //    digitalWrite(doorLedGreen, HIGH);
    //
    //    digitalWrite(doorLedRed, LOW);

    Firebase.setString("Devices/mGFjsGhS/Doors/D1/status", "Open");

    Firebase.setBool("Devices/mGFjsGhS/thief", false);

    currentDelay = 0;

  }

  if (isWait) {

    currentDelay += 1;

    Serial.println( "currentDelay : " + String(currentDelay));

  }

  //ini kondisi saat tidak ada pemencetan button saat kurang dari 10 detik
  if (currentDelay >= 15) {

    isWait = false;

    currentDelay = 0;

    digitalWrite(door01, HIGH);

    Serial.println("Lock 01 is locket");

    Firebase.setInt("Devices/mGFjsGhS/Doors/D1/doorLock", 0);

  }

  //ini kondisi saat ada pemencetan button saat kurang dari 10 detik

  if (valueDoor01 == 0 && isWait) {

    isWait = false;

    currentDelay = 0;

    digitalWrite(door01, HIGH);

    Serial.println("Lock 01 is locket");

    Firebase.setInt("Devices/mGFjsGhS/Doors/D1/doorLock", 0);

  }

  if (valueDoor01 == 0 && state01 == LOW) {

    isWait = false;

    Firebase.setString("Devices/mGFjsGhS/Doors/D1/status", "Open");

    Firebase.setBool("Devices/mGFjsGhS/thief", true);

    Serial.println("active thief alarm");

    tone(buzzer, 400);

    delay(500);

    noTone(buzzer);

  }

  //  if ( buttonState == HIGH) {
  //
  //    //      isWait = false;
  //    Firebase.setInt("Devices/mGFjsGhS/Doors/D1/doorLock", 1);
  //
  //    tone(buzzer, 50);
  //
  //    delay(10);
  //
  //    noTone(buzzer);
  //
  //  }

  if (pushState == HIGH) {

    Firebase.setBool("Devices/mGFjsGhS/guest", true);

    Serial.println("bell actif");

    tone(buzzer, 3000);

    delay(800);

    noTone(buzzer);

    delay(3000);

  }

  else if (pushState == LOW) {

    Firebase.setBool("Devices/mGFjsGhS/guest", false);

    Firebase.setBool("Devices/mGFjsGhS/thief", false);

    noTone(buzzer);

  }

}

void dateToday() {

  time_t now = time(nullptr);

  struct tm* p_tm = localtime(&now);

  Serial.print(p_tm->tm_mday);
  Serial.print("/");
  Serial.print(p_tm->tm_mon + 1);
  Serial.print("/");
  Serial.print(p_tm->tm_year + 1900);

  Serial.print(" ");

  Serial.print(p_tm->tm_hour);
  Serial.print(":");
  Serial.print(p_tm->tm_min);
  Serial.print(":");
  Serial.println(p_tm->tm_sec);

  delay(500);
}
//
//void eventDoorB() {
//  valueDoor01 = Firebase.getInt("Devices/mGFjsGhS/Doors/D1/doorLock");
//  buttonState = digitalRead(pushButton);
//  state01 = digitalRead(doorStatus01);
//  pushState = digitalRead(pushBell);
//
//  if (valueDoor01 == 1 && state01  == LOW && !isWait) {
//    isWait = true;
//    tone(buzzer, 50);
//    delay(10);
//    noTone(buzzer);
//    digitalWrite(door01, LOW);
//    Firebase.setString("Devices/mGFjsGhS/Doors/D1/status", "Closed");
//
//
//  } else if (valueDoor01 == 1 &&  state01 == HIGH ) {
//    isWait = false;
//    digitalWrite(door01, LOW);
//    Firebase.setString("Devices/mGFjsGhS/Doors/D1/status", "Open");
//    currentDelay = 0;
//  }
//
//
//  if (isWait) {
//    currentDelay += 1;
//    Serial.println( "currentDelay : " + String(currentDelay));
//  }
//
//  //ini kondisi saat tidak ada pemencetan button saat kurang dari 10 detik
//  if (currentDelay >= 15) {
//    isWait = false;
//    currentDelay = 0;
//
//    digitalWrite(door01, HIGH);
//    Serial.println("Lock 01 is locket");
//    Firebase.setInt("Devices/mGFjsGhS/Doors/D1/doorLock", 0);
//  }
//
//
//  //ini kondisi saat ada pemencetan button saat kurang dari 10 detik
//
//  if (valueDoor01 == 0 && isWait) {
//    isWait = false;
//    currentDelay = 0;
//    digitalWrite(door01, HIGH);
//    Serial.println("Lock 01 is locket");
//    Firebase.setInt("Devices/mGFjsGhS/Doors/D1/doorLock", 0);
//
//  }
//
//  if (valueDoor01 == 0 && state01 == HIGH) {
//    isWait = false;
//    Firebase.setString("Devices/mGFjsGhS/Doors/D1/status", "Open");
//    Firebase.setBool("Devices/mGFjsGhS/thief", true);
//    Serial.println("active burglar alarm");
//    tone(buzzer, 400);
//    delay(500);
//    noTone(buzzer);
//
//  }
//
//  if ( buttonState == HIGH) {
//    //      isWait = false;
//    Firebase.setInt("Devices/mGFjsGhS/Doors/D1/doorLock", 1);
//    tone(buzzer, 50);
//    delay(10);
//    noTone(buzzer);
//
//
//  }
//
//  if (pushState == HIGH) {
//    Firebase.setBool("Devices/mGFjsGhS/guest", true);
//    Serial.println("bell actif");
//    tone(buzzer, 3000);
//    delay(800);
//    noTone(buzzer);
//    delay(3000);
//
//  }
//
//  else if (pushState == LOW) {
//    Firebase.setBool("Devices/mGFjsGhS/guest", false);
//    noTone(buzzer);
//
//  }
//
//}
