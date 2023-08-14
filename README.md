# TodoApp

TodoApp is a simple Android application that allows users to manage their tasks and to-dos. The app provides CRUD operations to add, edit, and delete tasks, helping users stay organized and focused.
The structure follows the MVVM architecture.

## Features

- View a list of tasks in a clean and user-friendly interface.
- Add new tasks with a title and optional description.
- Edit existing tasks, updating their title and description.
- Delete tasks to remove completed or unnecessary items.
- Data persistence using a local SQLite database with Room.


## Getting Started

To get started with the TodoApp project, follow these steps:

1. Clone this repository: `https://github.com/gerigol/todo-list.git`
2. Open the project in Android Studio.

### Running on an Emulator

3. Build and run the app on an Android emulator:
   - Launch Android Studio.
   - Click "Open an existing Android Studio project" and select the cloned repository.
   - Wait for the project to sync and build.
   - Choose an emulator from the AVD Manager (Android Virtual Device Manager).
   - Click the "Run" button (green play icon) to launch the app on the selected emulator.

### Running on a Physical Android Device

4. Build and run the app on a physical Android device using a USB cable:
   - Connect your Android device to your computer using a USB cable.
   - Enable Developer Mode on your device by going to Settings > About phone > Software information > Tap "Build number" seven times.
   - In the Developer Options, enable USB Debugging.
   - Select "File transfer" or "MTP" mode.
   - In Android Studio, click "Run" to deploy the app to your connected device.

## Technologies Used

- Android Studio: The official IDE for Android app development.
- Kotlin: The modern programming language used for Android development.
- Room Database: A part of Android Architecture Components, used for local data storage.
- RecyclerView: Displaying and managing a scrollable list of tasks.
- ViewModel: To manage UI-related data and handle configuration changes.
