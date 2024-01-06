# ![Linkora Logo](https://github.com/sakethpathike/Linkora/blob/master/app/src/main/res/mipmap-hdpi/logo_launcher.png?raw=true) Linkora

Linkora is an Android project that focuses on making link organization simpler. From saving a single link individually to saving links in a folder for respective use cases, Linkora has almost all of them with a simpler UI and will be updated in further releases with a few other helpful and useful features that you would expect from a link utility app.

## Features

##### To name a few:

- Save links individually with ease.
- Organize links into folders (sub-folders can be added from the [latest beta release](https://github.com/sakethpathike/Linkora/releases/tag/release-v0.4.0-beta01)) for better management.
- Mark important links for quick access.
- Archive links or folders to keep things tidy.
- Customize link names as you like.
- Share links from other apps effortlessly.
- Search and sort links/folders effortlessly.
- Importing and exporting links (in beta version).
- Linkora recognizes images and titles whenever possible.

## Tech Stack

- Kotlin: This project is entirely written in Kotlin.
- Jetpack Compose: Jetpack Compose is used to write the UI, making it a completely Jetpack Compose-based project.
- Material 3: Linkora uses the latest M3 components for the UI.
- Room: For saving all of the data locally.
- Kotlin Coroutines: Used for managing background tasks
- Kotlin Flows: Used for handling asynchronous data streams
- Kotlinx Serialisation: Used for deserializing API responses.
- Coil is Used for loading images, and Architecture Components such as DataStore, Navigation, and ViewModel are also used to make this project alive!

## GitHub Releases
The latest release of Linkora (v0.4.0-beta01) is available on [GitHub Releases](https://github.com/sakethpathike/Linkora/releases/tag/release-v0.4.0-beta01). You can download the APK file from there; or [click here](https://github.com/sakethpathike/Linkora/releases/download/release-v0.4.0-beta01/Linkora-v0.4.0-beta01.apk)

## Screenshots

### Home Screen

|                                                       Home Screen                                                      | Sorting History in Home Screen |
|:-----------------------------------------------------------------------------------------------------------------------:|:-------------:|
| ![HomeScreen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/2c2c09a4-78e0-409c-b2e5-45a3688c34ea) |![Sorting in Home Screen of Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/fe33dcd3-c142-4f68-ac1d-757c5134833c)|

### Search Screen

|                                                           Search                                                           |                                                       History in Search Screen                                                        |                                                       Sorting History in Search Screen                                                        |
|:--------------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------------------:|
| ![Search Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/b158a105-be5e-4d4d-8ae8-b2d87b7063b3) | ![History in Search Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/7162d7f4-8a24-41cb-a477-127b65603606) | ![Sorting History in Search Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/9fce4fd6-45b0-48f6-9d34-497d3d49a62e) |

### Collections Screen

|                                                       Collections Screen                                                        | Collections Screen |                                                       Sorting Folders in Collections Screen                                                        |
|:-------------------------------------------------------------------------------------------------------------------------------:|:-------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------:|
| ![Collections Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/57f7b9c4-bd8c-47e7-9488-c1143156e964) |![Collections Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/c2c70bb2-4e9a-49de-a943-7ba9b03316b1)| ![Sorting Folders in Collections Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/90136dfd-ae1e-45fe-a3e1-8568facc3f87) |

### Saved Links Screen

|                                                       Saved Links Screen                                                        | Saved Links Screen (Light Theme) |                                                       Sorting Links in Saved Links Screen                                                        |
|:-------------------------------------------------------------------------------------------------------------------------------:|:-------------:|:------------------------------------------------------------------------------------------------------------------------------------------------:|
| ![Saved Links Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/8045296c-0187-4586-9c6c-818c36e54034) |![Saved Links Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/f3d8dd80-ae8d-4cc4-8b0d-eed454f57b78)| ![Sorting Links in Saved Links Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/22639fa7-ff50-4c0e-8eed-b964a06c7b21) |

### Important Links Screen

|                                                       Important Links Screen                                                        |                                                       Sorting Links in Important Links Screen                                                        |
|:-----------------------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------------------------------------:|
| ![Important Links Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/a2cc009e-25f2-4e7f-850c-94b2fe3f923e) | ![Sorting Links in Important Links Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/f873fe3f-435b-4bb9-ad03-98a1a6e7aaca) |

### Archive Screen

|                                                       Archived Link Screen                                                        |                                                       Archived Folder Screen                                                        |
|:---------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------:|
| ![Archived Link Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/0ad080e2-9d85-4dc1-8f8c-a51bd60a39f6) | ![Archived Folder Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/4edd4c67-0525-4665-ac89-8a0518cf94c7) |

### Custom Folder

|                                                       Links in a Custom Folder                                                        |                                                       Sorting Links in a Custom Folder                                                        |
|:-------------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------------------:|
| ![Links in a Custom Folder in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/c11c54b1-eb0d-4308-8bd1-3f52751a5b02) | ![Sorting Links in a Custom Folder in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/be0d5501-1b90-4288-89eb-e691630f1c2f) |

### Settings Screen

|                                                       Settings Screen                                                        | Settings Screen |
|:----------------------------------------------------------------------------------------------------------------------------:|:-------------:|
| ![Settings Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/e7e7a604-0adc-4e7f-85a5-a206131af7a3) |![Settings Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/5b6dfc28-bef2-4060-8b2b-2282299ee400)|

### Sharing from other apps

|                                                        Sharing links from other apps                                                         |
|:--------------------------------------------------------------------------------------------------------------------------------------------:|
| https://github-production-user-asset-6210df.s3.amazonaws.com/83284398/291981034-085c7d3f-e4f2-4466-9b13-639451846233.webm  |

## Upcoming Updates
- [x] Sorting
- [x] Searching
- [x] Importing and exporting links (in beta)
- Better title and image parsing of links
- [x] Sub-folders ([currently in beta version](https://github.com/sakethpathike/Linkora/releases/tag/release-v0.4.0-beta01))
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