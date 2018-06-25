# The Days
Days is a simple Android app that helps to control event dates.
This project showcases MVP pattern with Mosby, Repository pattern, RXJava 2, Dagger 2, Room and Material Design.

## Features

### For the user
- Countdown and progressive: Keep track of how many days are left or how many days have passed since each event.
- Tags for organizing events: You can use multiple tags to organize your events.
- Favorite events: Assign favorite events to keep them close at hand.
- Event sorting: You can sort events in the most convenient way.
- Simple and easy to use Material Design: Days has a simple design, so that it is agile and comfortable to use. Also use and follow the Material Design style guide.
- Cloud storage - Integrates with the Airtable Service API to provide cloud storage.
- Multi-platform data access: Thanks to the storage in Airtable it is possible to consult and modify the data from any compatible device.
- Local storage - If cloud storage is not used, data is stored on the phone. If cloud storage is used, storage on the phone is used as a data cache.
- Free and open source: It is free and the source code of the application is available.

### For other developers:
- MVP: Implementation of the MVP architecture pattern.
- Repository pattern: Implementation of the Repository pattern for the persistence layer.
- Room: Use of the Room library for local data storage.
- RxJava 2: Use of the RxJava2 library for reactive programming.
- Material Design: Implementation of Google's Material Design style.
- Unit testing: Init tests have been implemented to check the correct functioning of the application. Using the Robolectric, Mockito and Mockwebserver libraries to avoid dependencies.
- Dagger 2: Implementation of dependency injection using Dagger 2.
  
## Screenshots

## Libraries
The following libraries are used in the project:
- [Airtable Android](https://github.com/clloret/airtable.android)
- [Android Support Libraries](https://developer.android.com/topic/libraries/support-library)
- [ButterKnife](https://github.com/JakeWharton/butterknife)
- [Dagger](https://github.com/google/dagger)
- [EventBus](https://github.com/greenrobot/EventBus)
- [Gson](https://github.com/google/gson)
- [Joda-Time](https://github.com/JodaOrg/joda-time)
- [Material-About-Library](https://github.com/daniel-stoneuk/material-about-library)
- [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver)
- [Mockito](https://github.com/mockito/mockito)
- [Mosby](https://github.com/sockeqwe/mosby)
- [OkHttp](https://github.com/square/okhttp)
- [ParcelablePlease](https://github.com/sockeqwe/ParcelablePlease)
- [Robolectric](https://github.com/robolectric/robolectric)
- [Room](https://developer.android.com/topic/libraries/architecture/room)
- [RxAndroid](https://github.com/ReactiveX/RxAndroid)
- [Stetho](https://github.com/facebook/stetho)
- [Timber](https://github.com/JakeWharton/timber)

## Requirements

- JDK 1.8
- [Android SDK](http://developer.android.com/sdk/index.html).
- Android Oreo [(API 27) ](http://developer.android.com/tools/revisions/platforms.html).
- Latest Android SDK Tools and build tools.

## To-dos

This project is still in progress. Here are the some features that I will finish in the future.

### For users:
- [ ] Notifications
- [ ] Widgets
- [ ] Tasker integration

### For developers:
- [ ] Kotlin
- [ ] Architecture components

## License

This project is licensed under the GPLv3 License - see the [LICENSE](LICENSE) file for details
