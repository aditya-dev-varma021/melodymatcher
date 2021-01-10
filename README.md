# melodymatcher
## Functionality
Ever remember a tune but aren't able to find the song on the Internet? This project implements Java's Sound API in order to allow users to hum or sing a song (up to 7.5 seconds long), which will then be matched against an existing database of songs
in order to recognize that elusive melody. 

## Implementation 
Users can record audio clips of themselves humming or singing a song (up to 7.5 seconds in current implementation) using a TargetDataLine. From there, the audio is saved into a .WAVE file inside the src folder. 
From there, audio is matched against an existing base of songs by performing linear cross-correlation between the songs and searching for a magnitude peak in the resulting correlation graph, which signifies an audio sample with a high match. This can be 
performed using the Fast Fourier Transform [NOT YET IMPLEMENTED]. Based on the highest peak recorded for each database song, the songs sort themselves into a list and the topmost match is presented [NOT YET IMPLEMENTED]. 

## Credit
I used baralaborn's Java API tutorials as supplementary learning material about the Sound API and made modifications to some of the information presented in order to develop 
modular recording and playback features. This project is also powered by the JTransforms library on GitHub for the FFT features used to implement cross-correlation. 
