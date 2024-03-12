from socket import *

import csv
import time

def current_milli_time():
    return (time.time() * 1000)
serverName = input("serverIP?")
serverPort = input("serverPort?")
serverPort = int(serverPort)
clientSocket = socket(AF_INET, SOCK_STREAM)
clientSocket.connect((serverName, serverPort))
terminated = True
resulting_data = []
while terminated:
    #gets input from user about the measurements
    measureType = input("rtt or tput?")
    pSize = input("How many probes (has to be greater >= 10)?")
    msgSize  = input("Message Size (1 - 32000)")
    delay = input("Any delay (in ms), enter 0 if none.")
    sentence  = "s "+measureType+" "+pSize+" "+msgSize+" "+delay+"\n"
    clientSocket.send(sentence.encode())
    buffer = b""  # Use bytes for buffer instead of a string
        #Beginning Buffer
    test = True
    #sends setup request to server
    while test:
        modifiedSentence = clientSocket.recv(1024)
        buffer += modifiedSentence  # Append the received data to the buffer
        if b"\n" in modifiedSentence:
            test = False
    print('From Server:',buffer.decode())
    #Checks if server accepted setup protocol
    if "200" in buffer.decode(): 
        #we start sending probes
        for x in range(1,int(pSize)+1):      
            payload = "a"*int(msgSize)
            constuctor = "m "+ str(x) +" "+ payload+"\n"
            prev_time = current_milli_time()  #time when request was sent
            clientSocket.send(constuctor.encode())
            buffer = b""
            flag = True
            #read response from server
            while flag:
                new = clientSocket.recv(1024)
                buffer += new
                if b"\n" in new:
                    end_time = current_milli_time() #time when response was recieved
                    flag = False
            if str(x) in buffer.decode():
                    print('From Server:',buffer.decode())
                    if measureType == "rtt":
                        rtt = end_time-prev_time
                        print(rtt)
                        resulting_data.append([x,rtt])
                    elif measureType == "tput":
                        rtt = end_time-prev_time
                        calculation = (int(msgSize)/rtt)*0.008
                        print(calculation," Mbps")
                        resulting_data.append([x,calculation,rtt])
    
    print (resulting_data)
    user = input("terminate? (y/n)") #allows user to terminate the program
    if "y" in user:
        termination = 't\n'
        terminated = False
        clientSocket.send(termination.encode())
        buffer = b""  
        while True:
            modifiedSentence = clientSocket.recv(1024)
            buffer += modifiedSentence  # Append the received data to the buffer
            if b"\n" in modifiedSentence:
                break
if "200 OK: Closing Connection" in buffer.decode(): 
    clientSocket.close()



