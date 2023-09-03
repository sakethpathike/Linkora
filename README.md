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
The latest stable release of Linkora is available on [GitHub Releases](https://github.com/sakethpathike/Linkora/releases/tag/release-v0.0.3). You can download the APK file from there; or [click here](https://github.com/sakethpathike/Linkora/releases/download/release-v0.0.3/Linkora-v0.0.3.apk)

## Screenshots

### Home Screen

| Home Screen | Home Screen |
|:-------------:|:-------------:|
|![Screenshot_2023_09_03_05_12_45_35_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/865b8bf8-db4a-490c-b9e3-e52d4bf40820)|![Screenshot_2023_09_03_05_18_16_52_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/b1658c49-762f-41cd-9b40-8cc2eddb70b3)|

### Collections Screen

| Collections Screen | Collections Screen |
|:-------------:|:-------------:|
|![Screenshot_2023_09_03_05_19_27_10_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/a6d051b9-2749-44fd-920e-92e4174bfade)|![Screenshot_2023_09_03_05_20_23_49_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/4f9a67db-7ac4-48a6-872b-ce11272dfab1)|

### Saved Links Screen

| Saved Links Screen | Saved Links Screen (Light Theme) |
|:-------------:|:-------------:|
|![Screenshot_2023_09_03_05_25_37_52_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/b8736cb1-6b6b-4567-9a9a-6c9e295fd1b9)|![Screenshot_2023_09_03_05_26_06_31_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/0bed95e5-046b-47b4-82ac-a3ab1660e050)|

### Important Links Screen

| Important Links Screen |
|:-------------:|
|![Screenshot_2023_09_03_05_36_26_55_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/42695e62-4299-44ae-8494-f8f99cbdade0)|

### Archive Screen

| Archived Link Screen | Archived Folder Screen |
|:-------------:|:-------------:|
|![Screenshot_2023_09_03_05_33_09_23_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/634a5eb8-34e1-4ed8-8c4e-aa094e322520)|![Screenshot_2023_07_18_18_34_46_84_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/906de99b-c959-4274-af22-6f189108a2ce)|

### Specific Folder Links

| Specific Folder Links  |
|:-------------:|
|![Screenshot_2023_09_03_05_35_11_78_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/912c5d35-12fd-44ca-899f-05191b31068a)|

### Settings Screen

| Settings Screen | Settings Screen |
|:-------------:|:-------------:|
| ![Screenshot_2023_07_18_20_45_36_54_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/7f4b2bd1-703c-4ad7-93a3-7520f138841c)| ![Screenshot_2023_07_18_18_37_35_93_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/17c35aac-6ad3-4bfc-9fe8-6d580a21b4e4)|

### Sharing from other apps

| Sharing Screen | Sharing Screen |
|:-------------:|:-------------:|
|![Screenshot_2023_09_03_05_28_54_74_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/d2d2b7a6-a438-4249-8532-bc226e11f34e)|![Screenshot_2023_09_03_05_30_03_73_9d74ce38016571544acf7a6bc28cc5f6](https://github.com/sakethpathike/Linkora/assets/83284398/a2cc8933-f3ec-4faf-889f-1d689ee966ce)|

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