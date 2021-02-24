# TemperatureMachine
An educational app that gets the environmental temperature from a **Pmod-TMP3 Sensor** and sends it to a **JavaApp** through an **Universal Asynchronous Transimission Protocol**, using a FPGA device(**Basys3**).

How does it work?
This app was implemented on 2 sides. 

**Hardware side:** 
- setting-up a Control Unit in VHDL
- using an I2C protocol for retrieving data from our Pmod Sensor
- setting an UART connection

![Face](https://github.com/crisapal/TemperatureMachine/blob/main/Software%20project/assets/face.png )


**Software side:** 
- linking the necessary libraries to our project for displaying a pretty UI - temperature chart 
- setting the virtual ports and opening the UART connection
- displaying a precise temperature that is read once in 10 seconds by listening to the "Get Temperature" Button

![Temperature presentation](https://github.com/crisapal/TemperatureMachine/blob/main/Software%20project/assets/temp.png | width=100)
