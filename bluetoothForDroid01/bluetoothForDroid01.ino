#include <Sparki.h> // include the sparki library

String inputString; //make an empty String called inputString
boolean returnFlag; //flag to check for carriage return
boolean oKSent; //flag to check for OK communication
char commArray [4]; //array to store communication
int arrayCounter = 0; //integer to count through commArray
int boredCounter = 0;
int counter = 0;

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
int threshold = 500;

// Mapping
const int x_dim = 16;
const int y_dim = 8;
char my_map[x_dim][y_dim];

int cm = 0;

void setup()
{
  Serial1.begin(9600);
  sparki.clearLCD();
  sparki.servo(0); 
}

void loop()
{
    sparki.clearLCD();
    readComm();
    readUltra();
    //delay(60); // wait 0.1 seconds
    odometryCalc();
    sparki.updateLCD();
}

void makeMove(){
    if(commArray[0] == 'f' || commArray[1] == 'F')
    {
      commArray[0] = '0';
      char * pEnd;
      long int distance = strtol(commArray,&pEnd,10);
      sparki.moveForward(distance);
      Serial1.print("F");
      wheelLeft = wheelSpeed;
      wheelRight = wheelSpeed;
    }
    else if (commArray[0] == 'r' || commArray[0] == 'R')
    {
      sparki.moveRight(90);
      wheelLeft = wheelSpeed;
      wheelRight = -wheelSpeed;
    }
    else if (commArray[0] == 'l' || commArray[0] == 'L')
    {
      sparki.moveLeft(90);
      wheelLeft = -wheelSpeed;
      wheelRight = wheelSpeed;
    }
    else if (commArray[0] == 'b' || commArray[0] == 'b') // block
    {
      commArray[0] = '0';
      if (commArray[1] == 'n' || commArray[1] == 'n') // front
      {
         commArray[1] = '0';
         
      }
      else if (commArray[1] == 'e' || commArray[1] == 'e') // right
      {
         commArray[1] = '0';
      }
      else if (commArray[1] == 'n' || commArray[1] == 'n') // left
      {
         commArray[1] = '0';
      }
    }
    else if (commArray[0] != 0) //in case it's a character sparki doesn't understand
    {
      Serial1.print("~");
      Serial1.println(commArray); //send the character back
    }
  memset(commArray, 0, sizeof(commArray)); //clear out commArray
}

void readComm()
{
  
  if (Serial1.available())
  {
    int inByte = Serial1.read();
    
    if (counter == 3) {
         makeMove(); 
         counter = 0;
         memset(commArray, 0, sizeof(commArray));
    }
    else if ((char)inByte == '\n')
    {
      counter = 0;
      memset(commArray, 0, sizeof(commArray));
    }
    else
    {
      commArray[counter] = (char)inByte;
      counter++;
    }
  }
}

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

void readUltra()
{
  cm = sparki.ping(); // measures the distance with Sparki's eyes 
  sparki.print(" ultra "); sparki.println(cm);
}
