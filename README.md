Github Organizations Search
===========================

## About ##
### Quick Note for Reviewer ###
I have a little bit of experience with Android development, but its been a few
years and a whole lot has changed. So using Dagger, Android Architecture Components,
etc. was all new to me. I hope you will keep this in mind while looking over my 
project.

### The Project ###
An MVVM architecture was used to implement my solution. I used the following
libraries: 

Android Architecture Components:  
* LiveData
* ViewModel

Dagger 2  
Retrofit+Moshi  
Picasso  
[CustomTabs-Kotlin](https://github.com/saurabharora90/CustomTabs-Kotlin)

JUnit  
OkHttp3 MockWebServer  
Espresso  

## Instructions ##

### Build & Run ###
The project uses Gradle. It can be imported into Android Studio.
In Android Studio, you can run the app, the unit tests, or the UI tests.
I'm sure you're familiar with how to do this, so I won't go into detail 
(you can find instructions on the Android Studio site or StackOverflow).

#### Troubleshooting ####
If you run the instrumented tests multiple times, it is possible that 
you might hit the GitHub API rate limit. Wait an hour and retry (or use
VPN on the device).

If the UI tests fail with `PerformException: Error performing 'single click' on view...`, 
disable animations on the device by going to `Settings` -> `Developer Options`
and turn off the following:
* Window animation scale
* Transition animation scale
* Animator duration scale
