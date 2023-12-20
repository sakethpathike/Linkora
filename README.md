# ![Linkora Logo](https://github.com/sakethpathike/Linkora/blob/master/app/src/main/res/mipmap-hdpi/logo_launcher.png?raw=true) Linkora

Linkora is an Android project that focuses on making link organization simpler. From saving a single link individually to saving links in a folder for respective use cases, Linkora has almost all of them with a simpler UI and will be updated in further releases with a few other helpful and useful features that you would expect from a link utility app.

## Features

##### To name a few:

- Save links individually with ease.
- Organize links into folders (sub-folders can be added from the [latest alpha release](https://github.com/sakethpathike/Linkora/releases/tag/release-v0.4.0-alpha02)) for better management.
- Mark important links for quick access.
- Archive links or folders to keep things tidy.
- Customize link names as you like.
- Share links from other apps effortlessly.
- Search and sort links effortlessly.
- Importing and exporting links (in alpha version).
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
The latest release of Linkora (v0.4.0-alpha02) is available on [GitHub Releases](https://github.com/sakethpathike/Linkora/releases/tag/release-v0.4.0-alpha02). You can download the APK file from there; or [click here](https://github.com/sakethpathike/Linkora/releases/download/release-v0.4.0-alpha02/Linkora-v0.4.0-alpha02.apk)

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
| ![Saved Links Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/e3a8057f-7fef-4ece-8277-bdba97be38c4) |![Saved Links Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/93a208d4-ea3b-44ad-9ed8-53a37e261a05)| ![Sorting Links in Saved Links Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/66774ea0-48df-4e82-ac6a-64ee710a093d) |

### Important Links Screen

|                                                       Important Links Screen                                                        |                                                       Sorting Links in Important Links Screen                                                        |
|:-----------------------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------------------------------------:|
| ![Important Links Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/a123efa5-7cac-45d4-bc0d-337a0dd794d9) | ![Sorting Links in Important Links Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/6deef9b9-2a8e-4441-a4ae-01b1f03342b9) |

### Archive Screen

|                                                       Archived Link Screen                                                        |                                                       Archived Folder Screen                                                        |
|:---------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------:|
| ![Archived Link Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/0ad080e2-9d85-4dc1-8f8c-a51bd60a39f6) | ![Archived Folder Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/4edd4c67-0525-4665-ac89-8a0518cf94c7) |

### Custom Folder

|                                                       Links in a Custom Folder                                                        |                                                       Sorting Links in a Custom Folder                                                        |
|:-------------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------------------:|
| ![Links in a Custom Folder in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/ab05a26c-4629-4dcf-9173-bcd662d0aa9e) | ![Sorting Links in a Custom Folder in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/472d01d2-e15e-4771-b2c2-453ccd7f15ba) |

### Settings Screen

|                                                       Settings Screen                                                        | Settings Screen |
|:----------------------------------------------------------------------------------------------------------------------------:|:-------------:|
| ![Settings Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/9c4b60ea-a507-4f6f-bfbf-e9d5f7925cf9) |![Settings Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/bbcfd363-f527-4bb3-b493-c18a1644c0d2)|

### Sharing from other apps

|                                                        Sharing links from other apps                                                         |
|:--------------------------------------------------------------------------------------------------------------------------------------------:|
| https://github-production-user-asset-6210df.s3.amazonaws.com/83284398/291981034-085c7d3f-e4f2-4466-9b13-639451846233.webm  |

## Upcoming Updates
- [x] Sorting
- [x] Searching
- [x] Importing and exporting links (in alpha)
- Better title and image parsing of links
- [x] Sub-folders ([currently in alpha version](https://github.com/sakethpathike/Linkora/releases/tag/release-v0.4.0-alpha02))
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