Chinese version of the README -> please [click here](./README.md)

## About version matching instructions
This Demo is applicable to V3.5.2 and above controller version of CR series.

## DEMO documentation

This demo is a DEMO written in Java for Android phones or tablets to use TCP to control Dobot collaborative robot CR (hereinafter referred to as CR)

## DEMO OS requirements

Android version 4.3 or higher

## Project file description

The project file needs to be opened by Android Studio, please use Android Studio version 4.0 or higher.
The project file directory is divided into four modules: app, socket-client, socket-common-interface, and socket-core. The app is the main module of the demo, which is used to encapsulate the TCP communication protocol of CR and realize some simple functional interactions, such as enable, clear alarm, emergency stop, jog, MovJ and modify IO functions. The remaining modules are the underlying implementation of the Socket client that implements the TCP communication function.
The app package is mainly divided into Client, Message and RobotState modules, which correspond to the realization of the TCP client, the encapsulation of the TCP communication protocol and the real-time encapsulation of the robot state.
* Three Clients are implemented in Client, corresponding to three ports of CR and three types of message protocols. APIClient corresponds to port 29999, which implements a response client with one sending and one receiving. MoveClient corresponds to port 30003, and only sends the movement instructions corresponding to CR. StateClient corresponds to port 30004 and only accepts the robot's state protocol.
* Message is the package of various messages sent by CR, which is divided into basic BaseMessage and CRMessage.
* RobotState encapsulates the data returned from port 30004 and is used to generate various CR parameters.