# ![Linkora Logo](https://github.com/sakethpathike/Linkora/blob/master/app/src/main/res/mipmap-hdpi/logo_launcher.png?raw=true) Linkora

Linkora is an Android project that focuses on making link organization simpler. From saving a single link individually to saving links in a folder for respective use cases, Linkora has almost all of them with a simpler UI and will be updated in further releases with a few other helpful and useful features that you would expect from a link utility app.

## Features

##### To name a few:

- Save links individually with ease.
- Organize links into folders for better management.
- Mark important links for quick access.
- Archive links or folders to keep things tidy.
- Customize link names as you like.
- Share links from other apps effortlessly.
- Linkora recognizes images and titles whenever possible.

###### You can check for the latest release from the app and further download the newest version(s).

## Tech Stack

- Kotlin: This project is entirely written in Kotlin.
- Jetpack Compose: Jetpack Compose is used to write the UI, making it a completely Jetpack Compose-based project.
- Material 3: Linkora uses the latest M3 components for the UI.
- Room: For saving all of the data locally.
- Kotlin Coroutines: Used for managing background tasks
- Kotlin Flows: Used for handling asynchronous data streams
- Kotlinx Serialisation: Used for deserializing API responses.
- Coil is Used for loading images, and Architecture Components such as DataStore, Navigation, and ViewModel are also used to make this project alive!

##### Linkora doesn't strictly follow MVVM architecture but uses it where required, at least for now. I'm planning to rewrite a few things; it may then be a proper MVVM-based project, but for now, it has most of the touch of MVVM.

## GitHub Releases
The latest stable release of JetSpacer is available on [GitHub Releases](https://github.com/sakethpathike/JetSpacer/releases/tag/v1.0.0). You can download the APK file from there; or [click here](https://github.com/sakethpathike/JetSpacer/releases/download/v1.0.0/JetSpacer_v1.0.0.apk)

## Screenshots

### Home Screen

| Home Screen | Home Screen |
|:-------------:|:-------------:|
| ![Screenshot_2023_07_18_18_07_14_24_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/0613e418-1054-43da-8d70-cc4bbc4a0db9)| ![Screenshot_2023_07_18_18_28_09_07_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/c4cb15aa-019b-4572-b406-c496a3cff994) |

### Collections Screen

| Collections Screen | Collections Screen |
|:-------------:|:-------------:|
|![Screenshot_2023_07_18_18_09_40_17_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/f67fd6f5-f60f-442a-ab38-1a61d01e8981)|![Screenshot_2023_07_18_18_09_54_74_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/b4f55270-1ec4-434f-8a44-442538a4c4b4)|
  
### Saved Links Screen

| Saved Links Screen | Saved Links Screen (Light Theme) |
|:-------------:|:-------------:|
| ![Screenshot_2023_07_18_18_19_17_24_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/184b43e6-19af-43ae-934d-e07385be8a28)| ![Screenshot_2023_07_18_18_36_44_77_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/af82120d-4113-44bf-a7cf-1dc7566acb6f)|
 
### Important Links Screen

| Important Links Screen |
|:-------------:|
|![Screenshot_2023_07_18_18_26_35_79_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/76c336c0-5964-4115-b743-c01f12248452)|

### Archive Screen

| Archived Link Screen | Archived Folder Screen |
|:-------------:|:-------------:|
| ![Screenshot_2023_07_18_18_30_22_84_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/c35c2aee-c1f4-4639-b1e1-f039e8944c78)|![Screenshot_2023_07_18_18_34_46_84_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/906de99b-c959-4274-af22-6f189108a2ce)|

### Specific Folder Links

| Specific Folder Links  |
|:-------------:|
| ![Screenshot_2023_07_18_18_34_16_82_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/1161adcf-11c4-4c0f-b7d8-3d92deb98db9)|

### Settings Screen 

| Settings Screen | Settings Screen |
|:-------------:|:-------------:|
| ![Screenshot_2023_07_18_20_45_36_54_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/7f4b2bd1-703c-4ad7-93a3-7520f138841c)| ![Screenshot_2023_07_18_18_37_35_93_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/17c35aac-6ad3-4bfc-9fe8-6d580a21b4e4)|

### Sharing from other apps

| Sharing Screen | Sharing Screen |
|:-------------:|:-------------:|
| ![Screenshot_2023_07_18_18_39_04_55_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/fbd4749d-4725-40ab-9a92-d5d93c0246c2)|![Screenshot_2023_07_18_18_40_49_86_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/62f116c3-27f6-4150-baa8-e055ff8d5254)|

## Upcoming Updates 
- Searching and sorting
- Better title and image parsing of links
- Subfolders
- Pinned folders
- Linkboard in the home screen
- Linksheet and a few other things that a link utility app should have.

#### Note: This project is in active development and new features and improvements will be added over time.

## Contribute

Contributions are welcome! Feel free to raise issues or submit pull requests to improve Linkora.

- If you're looking to contribute, you can refer to the "Upcoming Updates" section above for areas to work on or any related stuff.
