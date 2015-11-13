#include <Sparki.h> // include the sparki library

String inputString; //make an empty String called inputString
boolean returnFlag; //flag to check for carriage return
boolean oKSent; //flag to check for OK communication
char commArray [10]; //array to store communication
int arrayCounter = 0; //integer to count through commArray
int boredCounter = 0;
int counter = 0;

void setup()
{
  Serial1.begin(9600);
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
      delay(1000); 
      sparki.moveStop();
    }
    else if (commArray[0] == 'r' || commArray[0] == 'R')
    {
      sparki.moveRight(90);
    }
    else if (commArray[0] == 'l' || commArray[0] == 'L')
    {
      sparki.moveLeft(90);
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
    for( int i = 0; i < sizeof(commArray); i++) {
      //Serial1.print(commArray[i]);
    }
    
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
  }
}
