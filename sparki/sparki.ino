#include <Sparki.h> // include the sparki library

// Directions
#define RIGHT 0
#define LEFT 1
#define FRONT 2
#define NONE -1

// Current Heading
#define N 0
#define W 1
#define S 2
#define E 3

// Ultra Sound Angles
#define ANGLE_LEFT -85
#define ANGLE_RIGHT 80
#define ANGLE_FRONT 0

// Blocks
#define BLOCK_CLOSE 1
#define BLOCK_FAR 2

// Block Distances
#define BLOCK_CLOSE_DISTANCE 10
#define BLOCK_FAR_DISTANCE 20

// Number of UltraSound Readings Per Correction
#define ULTRASOUND_READS 50

// Acceptable UltraSound Error
#define ACCEPTABLE_ULTRA_ERROR 2

// Variables For Reading From Bluetooth
char bt_input_array [4]; //array to store communication
int bt_array_counter = 0;

// Variables for Odometry
/**
float xi = 0;
float yi = 0;
int xint;
int yint;
float theta = 0;
float r = 2.55; // cm
float d = 8.45; // cm
float motorSpeed = 2.7724; // cm/s
float wheelSpeed = motorSpeed / r; // 1/s
float wheelRight = 0;
float wheelLeft = 0;
unsigned long time;
float deltaTime;
**/

// Global UltraSound Reading
int cm = 0;

// Direction of Last Block For Correction
int block_dir = NONE;

// Current Heading
int current_heading = N;
int current_x = 0;
int current_y = 0;



void setup()
{
  Serial1.begin(9600);
  Serial.begin(9600);
  sparki.clearLCD();
  sparki.servo(0); 
}


void loop()
{
    sparki.clearLCD();
    readBlueTooth();
    //delay(60); // wait 0.1 seconds
    //odometryCalc();
    sparki.print("x: "); sparki.println(current_x);
    sparki.print("y: "); sparki.println(current_y);
    sparki.print("heading: "); sparki.println(current_heading);
    sparki.updateLCD();
}


void makeMove(){
    if(bt_input_array[0] == 'f' || bt_input_array[1] == 'F')
    {
      bt_input_array[0] = '0';
      char * pEnd;
      long int distance = strtol(bt_input_array,&pEnd,10);
      sparki.moveForward(distance);
      updatePosition(distance);
      Serial1.print("F");
      //wheelLeft = wheelSpeed;
      //wheelRight = wheelSpeed;
    }
    else if (bt_input_array[0] == 'r' || bt_input_array[0] == 'R')
    {
      sparki.moveRight(90);
      updateHeading(1);
      //wheelLeft = wheelSpeed;
      //wheelRight = -wheelSpeed;
    }
    else if (bt_input_array[0] == 'l' || bt_input_array[0] == 'L')
    {
      sparki.moveLeft(90);
      updateHeading(-1);
      //wheelLeft = -wheelSpeed;
      //wheelRight = wheelSpeed;
      
    }
    else if (bt_input_array[0] == 'b' || bt_input_array[0] == 'b') // block
    {
      bt_input_array[0] = '0';
      block_dir = NONE;
      if (bt_input_array[1] == 'n' || bt_input_array[1] == 'n') // front
      {
         block_dir = FRONT;
      }
      else if (bt_input_array[1] == 'e' || bt_input_array[1] == 'e') // right
      {
         block_dir = RIGHT;
      }
      else if (bt_input_array[1] == 'w' || bt_input_array[1] == 'w') // left
      {
         block_dir = LEFT;
      }
      char * pEnd;
      bt_input_array[1] = '0';
      Serial.print("string: "); Serial.println(bt_input_array);
      ultraCorrection(strtol(bt_input_array,&pEnd,10));
      Serial1.print("B");
    }
    else if (bt_input_array[0] != 0) //in case it's a character sparki doesn't understand
    {
      Serial1.print("~");
      Serial1.println(bt_input_array); //send the character back
    }
  memset(bt_input_array, 0, sizeof(bt_input_array)); //clear out bt_input_array
}


void readBlueTooth()
{
  if (Serial1.available())
  {
    int input_byte = Serial1.read();
    
    if (bt_array_counter == 3) {
         makeMove(); 
         bt_array_counter = 0;
         memset(bt_input_array, 0, sizeof(bt_input_array));
    }
    else if ((char)input_byte == '\n')
    {
      bt_array_counter = 0;
      memset(bt_input_array, 0, sizeof(bt_input_array));
    }
    else
    {
      bt_input_array[bt_array_counter] = (char)input_byte;
      bt_array_counter++;
    }
  }
}


void updateHeading(int direction)
{
   if(abs(direction) != 1) return;
   current_heading += direction;
   if(current_heading == 4) current_heading = 0;
   if(current_heading == -1) current_heading = 3;
}


void updatePosition(int distance)
{
  if(current_heading == N) current_y += distance;
  else if(current_heading == W) current_x += distance;
  else if(current_heading == S) current_y -= distance;
  else if(current_heading == E) current_x -= distance;
}

/**
void odometryCalc()
{
  deltaTime = float((millis() - time)) / 1000.0;
  
  sparki.print(" deltaTime: "); sparki.println(deltaTime);
  sparki.print(" wheelRight: "); sparki.println(wheelLeft);
  sparki.print(" wheelLeft: "); sparki.println(wheelRight);
  
  xi = xi + cos(theta) * (r * wheelRight / 2 + r * wheelLeft / 2) * deltaTime;
  yi = yi + sin(theta) * (r * wheelRight / 2 + r * wheelLeft / 2) * deltaTime;
  theta = theta + (wheelRight * r / d - wheelLeft * r / d) * deltaTime;
  time = millis();
    
  sparki.print(" xi: "); sparki.println(xint);
  sparki.print(" yi: "); sparki.println(yint);
  sparki.print(" theta: "); sparki.println(theta);
}
**/

int readUltra()
{
  cm = sparki.ping(); // measures the distance with Sparki's eyes 
  sparki.print(" ultra "); sparki.println(cm);
  return cm;
}


void positionUltra()
{
  int angle = 0;
  if(block_dir == FRONT)
  {
    angle = ANGLE_FRONT;
  }
  else if(block_dir == RIGHT)
  {
    angle = ANGLE_RIGHT;
  }
  else if(block_dir == LEFT)
  {
    angle = ANGLE_LEFT;
  }
  sparki.servo(angle);   
}


void correctRight(int distance)
{
  sparki.moveRight(90);
  sparki.moveForward(distance);
  sparki.moveLeft(90);
}


void correctLeft(int distance)
{
  sparki.moveLeft(90);
  sparki.moveForward(distance);
  sparki.moveRight(90);
}


void correctFront(int distance)
{
  sparki.moveForward(distance);
}


void correctBack(int distance)
{
  sparki.moveForward(-1*distance);
}


void ultraCorrection(int long block_location)
{
  
  //Find the expected distance away from block
  int expected_distance = 0;
  Serial.println("ultra correction ");
  if(block_location == BLOCK_CLOSE)
  {
    expected_distance = BLOCK_CLOSE_DISTANCE;
  } 
  else if(block_location == BLOCK_FAR)
  {
    expected_distance = BLOCK_FAR_DISTANCE;
  }
  else
  {
    Serial.print("bad block distance: ");
    Serial.println(block_location);
    return; 
  }
  Serial.print(" expected distance "); Serial.println(expected_distance);
  positionUltra();
  delay(100);
  
  //Read distance from ultrasound
  int i = 0;
  int sum = 0;
  for(i = 0; i < ULTRASOUND_READS; i++)
  {
     sum += readUltra();
  }
  delay(100);
  sparki.servo(FRONT);
  int ultra_reading = sum/ULTRASOUND_READS;
  Serial.print("ultra reading: "); Serial.println(ultra_reading);
  
  int error =  expected_distance - ultra_reading;
  Serial.print("error "); Serial.println(error);
  
  if(abs(error) > ACCEPTABLE_ULTRA_ERROR)
  {
    if(error < 0 && block_dir == RIGHT)
    {
      correctRight(abs(error));
    }
    else if(error > 0 && block_dir == LEFT)
    {
      correctRight(abs(error));
    }
    else if(error > 0 && block_dir == RIGHT)
    {
      correctLeft(abs(error));
    }
    else if(error < 0 && block_dir == LEFT)
    {
      correctLeft(abs(error));
    }
    else if(error > 0 && block_dir == FRONT)
    {
      correctBack(abs(error));
    }
    else if(error < 0 && block_dir == FRONT)
    {
      correctFront(abs(error));
    }
  }
}


