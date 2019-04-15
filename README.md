Github Organization Search
==========================

## About ##
A small Android app using the GitHub API with an MVVM architecture, Kotlin, some Android Architecture Components (LiveData & ViewModel), Dagger 2, Retrofit, Moshi, & Picasso. Initially done as a take-home project as part of the interview process for a company and to learn about modern Android development (I had done a bit of Android years ago, but quite a lot has changed). I intend to use this app to add some new things I want to learn (e.g., RxJava, Room).

### What the App Does ###
I'm going to leave this ambiguous since the company likely wants to continue using the prompt in their interview process. Basically you can search for an organization on GitHub (e.g., 'Apple', or 'Google').

### Used in the Project ###
[Kotlin](https://kotlinlang.org)  
[Android KTX](https://developer.android.com/kotlin/ktx.html)  

Android Architecture Components:
* [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)

[Dagger 2](https://google.github.io/dagger/) - Dependency Injection  
[Retrofit](https://square.github.io/retrofit/) - REST client  
[Moshi](https://github.com/square/moshi) - JSON processing  
[Picasso](https://square.github.io/picasso/) - Image downloading & caching  
[CustomTabs-Kotlin](https://github.com/saurabharora90/CustomTabs-Kotlin) - a clean & simple lifecycle-aware API for using [Chrome Custom Tabs](https://developer.chrome.com/multidevice/android/customtabs)  
[AndroidX](https://developer.android.com/jetpack/androidx) - AndroidX support libraries  

[JUnit 4](https://junit.org/junit4/) - Unit Testing  
[OkHttp3 MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver) - Networking Tests  
[Espresso](https://developer.android.com/training/testing/espresso) - UI Tests

### TODO List ###

Improvements to be made:

- [x] Use Repository Pattern
- [ ] Improve networking code
    - [ ] Encapsulate API response (result, error message, isLoading)
    - [ ] Retries
- [ ] Full Test coverage - ViewModels, RecyclerView, Mockito tests
- [ ] RxJava
- [ ] Room
- [ ] App icon - only works on some devices?
- [ ] Toolbar Up button
- [ ] Logging/Build variant

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
