# Github Organization Search

## About

This was a take-home project I did as part of a job interview that I did in 2019.
I also used it as an opportunity to learn more about what was new in Android development (which I had a little bit of experience in but hadn't done in a few years).  

What it does, basically: view most-starred GitHub repos for an organization

### Built With

[Kotlin](https://kotlinlang.org)  
[Android KTX](https://developer.android.com/kotlin/ktx.html)

Android Architecture Components:

- [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)

[Koin](https://start.insert-koin.io/) - Dependency Injection  
[Retrofit](https://square.github.io/retrofit/) - REST client  
[Moshi](https://github.com/square/moshi) - JSON processing  
[Picasso](https://square.github.io/picasso/) - Image downloading & caching  
[CustomTabs-Kotlin](https://github.com/saurabharora90/CustomTabs-Kotlin) - a clean & simple lifecycle-aware API for using [Chrome Custom Tabs](https://developer.chrome.com/multidevice/android/customtabs)  
[AndroidX](https://developer.android.com/jetpack/androidx) - AndroidX support libraries

[JUnit 5](https://junit.org/junit5/docs/current/user-guide/) - Unit & Instrumentation Tests

- Reference: [JUnit 5 on Android](https://www.lordcodes.com/articles/testing-on-android-using-junit-5)

[Kotest](https://github.com/kotest/kotest)  
[mockk](https://mockk.io/)  
[OkHttp MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver) - Networking Tests  
[Espresso](https://developer.android.com/training/testing/espresso) - UI Tests

### Misc

If you run the instrumented tests multiple times, it is possible that
you might hit the GitHub API rate limit. Wait an hour and retry (or use
VPN on the device).
