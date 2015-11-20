#include <Sparki.h> // include the sparki library

String inputString; //make an empty String called inputString
boolean returnFlag; //flag to check for carriage return
boolean oKSent; //flag to check for OK communication
char commArray [3]; //array to store communication
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



void setup()
{
  Serial1.begin(9600);
  sparki.clearLCD();
}

void loop()
{
    readComm();
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
    else if (commArray[0] != 0) //in case it's a character sparki doesn't understand
    {
      Serial1.print("~");
      Serial1.println(commArray); //send the character back
    }
  memset(commArray, 0, sizeof(commArray)); //clear out commArray
}

void readComm()
{
  
  while (Serial1.available())
  {
    int inByte = Serial1.read();
    
    if (counter == 2) {
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
    delay(60); // wait 0.1 seconds
  
    deltaTime = float((millis() - time)) / 1000.0;
    xi = xi + cos(theta) * (r * wheelRight / 2 + r * wheelLeft / 2) * deltaTime;
    yi = yi + sin(theta) * (r * wheelRight / 2 + r * wheelLeft / 2) * deltaTime;
    theta = theta + (wheelRight * r / d - wheelLeft * r / d) * deltaTime;
    time = millis();
    
    sparki.clearLCD();
    sparki.print("\nxi: "); sparki.println(xint);
    sparki.print("\nyi: "); sparki.println(yint);
  }
}
