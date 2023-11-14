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
- Search and sort links effortlessly.
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
The latest release of Linkora (v0.3.0) is available on [GitHub Releases](https://github.com/sakethpathike/Linkora/releases/tag/release-v0.3.0). You can download the APK file from there; or [click here](https://github.com/sakethpathike/Linkora/releases/download/release-v0.3.0/Linkora-v0.3.0.apk)

## Screenshots

### Home Screen

| Home Screen | Home Screen | Sorting History in Home Screen |
|:-------------:|:-------------:|:-------------:|
|![Screenshot_2023_09_17_01_04_56_84_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/b688dbc1-42f2-475e-b3c1-9d4b9dced960)|![Screenshot_2023_09_05_04_52_27_77_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/5be5ce8d-61a8-4f83-a2ae-b7d645f13749)|![Screenshot_2023_09_17_00_50_06_76_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/8372c2d4-a8c1-4ca7-9d8d-71c3673357db)|

### Search Screen

| Search | History in Search Screen | Sorting History in Search Screen |
|:-------------:|:-------------:|:-------------:|
|![Screenshot_2023_09_17_00_56_21_86_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/abfd721d-afa4-4324-b3a4-3c197e6f81cb)|![Screenshot_2023_09_17_00_53_20_12_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/43c9c61b-bdec-4283-b691-282705e0590d)|![Screenshot_2023_09_17_00_53_41_43_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/45e829c7-c1a0-484d-a9cd-eb3297f60b07)|

### Collections Screen

| Collections Screen | Collections Screen | Sorting Folders in Collections Screen |
|:-------------:|:-------------:|:-------------:|
|![Screenshot_2023_09_17_01_08_59_44_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/9aed6c17-d837-4ab1-a56f-b0b262142e73)|![Screenshot_2023_09_05_04_55_35_37_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/9b1ff4d2-cc52-450b-9de8-3ae79c838242)|![Screenshot_2023_09_05_04_56_05_85_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/f97d91fd-54b3-4c3c-8d8c-097f2ade1396)|

### Saved Links Screen

| Saved Links Screen | Saved Links Screen (Light Theme) |  Sorting Links in Saved Links Screen |
|:-------------:|:-------------:|:-------------:|
| ![Screenshot_2023_09_05_04_56_27_34_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/b1bd52aa-5871-4214-ac34-5dd7b0efd70b)|![Screenshot_2023_09_05_04_57_34_08_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/8a0df4b6-6530-4af1-bbbb-2bdfb4c9de3e)|![Screenshot_2023_09_05_04_56_48_19_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/6f906fc0-88ed-4999-8b6c-60d79b396546)|

### Important Links Screen

| Important Links Screen | Sorting Links in Important Links Screen |
|:-------------:|:-------------:|
|![Screenshot_2023_09_05_04_59_21_66_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/219aed06-72de-48c0-b96f-6a485cc580cd)|![Screenshot_2023_09_05_04_58_21_42_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/fde56557-3c73-4e63-8e43-5eda796f5dd5)|

### Archive Screen

| Archived Link Screen | Archived Folder Screen |
|:-------------:|:-------------:|
|![Screenshot_2023_09_05_05_00_07_24_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/5d93df76-4284-438a-980b-3d4a706c7788)|![Screenshot_2023_09_05_05_01_38_72_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/145ab639-0853-4c15-953d-2c352515d1c3)|

### Custom Folder

| Links in a Custom Folder  | Sorting Links in a Custom Folder |
|:-------------:|:-------------:|
|![Screenshot_2023_09_06_00_26_26_48_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/4b5057bb-191e-47c7-ba3a-db20a6302dd6)|![Screenshot_2023_09_06_00_26_42_72_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/28feb961-693f-4c9e-aa6c-d495469455d5)|

### Settings Screen

| Settings Screen | Settings Screen |
|:-------------:|:-------------:|
|![Screenshot_2023_07_18_20_45_36_54_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/7f4b2bd1-703c-4ad7-93a3-7520f138841c)|![Screenshot_2023_09_17_00_57_42_11_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/56dcd081-cd93-4177-bedc-c3912b88a8c9)|

### Sharing from other apps

| Sharing Screen | Sharing Screen |
|:-------------:|:-------------:|
|![Screenshot_2023_09_03_05_28_54_74_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/d2d2b7a6-a438-4249-8532-bc226e11f34e)|![Screenshot_2023_09_03_05_30_03_73_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/a2cc8933-f3ec-4faf-889f-1d689ee966ce)|

## Upcoming Updates
- [x] Sorting
- [x] Searching
- [x] Importing and exporting (in alpha)
- Better title and image parsing of links
- Sub-folders
- Pinned folders and a few other things that a link utility app should have.

#### Note: This project is in active development and new features and improvements will be added over time.

## Contribute

Contributions are welcome! Feel free to raise issues or submit pull requests to improve Linkora.

- If you're looking to contribute, you can refer to the "Upcoming Updates" section above for areas to work on or any related stuff.

## License

```
MIT License

Copyright (c) 2023 Saketh Pathike

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```