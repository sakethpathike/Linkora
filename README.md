![Linkora Header](https://github.com/sakethpathike/Linkora/assets/83284398/eced359e-f327-4316-ba65-ae14722e1384)
Linkora is an Android project that focuses on making link organization simpler. From saving a single link individually to saving links in a folder for respective use cases, Linkora has almost all of them with a simpler UI and will be updated in further releases with a few other helpful and useful features that you would expect from a link utility app.

## Features

- Store links individually effortlessly.
- Organize links into folders for better management.
- Create nested folders to further organize your links (sub-folders can be added from
  the [latest beta release](https://github.com/sakethpathike/Linkora/releases/tag/release-v0.4.0-beta02)).
- Mark important links for quick access.
- Archive links or folders to keep things tidy.
- Customize link names as you like.
- Share links from other apps effortlessly.
- Easily search and sort through your links and folders.
- Import and export data (beta feature).
- Linkora recognizes images and titles whenever possible.
- Personalize your Home screen UI with your favorite folders, similar to Twitter Lists.
- Perform various operations by selecting multiple links or folders.

## Tech Stack

- Kotlin: This project is entirely written in Kotlin.
- Jetpack Compose: Jetpack Compose is used to write the UI, making it a completely Jetpack
  Compose-based project.
- Material 3: Linkora uses the latest M3 components for the UI.
- Room: For saving all of the data locally.
- Kotlin Coroutines: Used for managing background tasks.
- Kotlin Flows: Used for handling asynchronous data streams.
- Kotlinx Serialisation: Used for deserializing API responses.
- Coil is Used for loading images, and Architecture Components such as DataStore, Navigation, and
  ViewModel are also used to make this project alive!

## Releases

The latest release of Linkora (v0.4.0-beta02) is available
on [GitHub Releases](https://github.com/sakethpathike/Linkora/releases/tag/release-v0.4.0-beta01).
You can download the APK file from there;
or [click here](https://github.com/sakethpathike/Linkora/releases/download/release-v0.4.0-beta01/Linkora-v0.4.0-beta01.apk).

[<img  src="https://img.shields.io/github/downloads/sakethpathike/linkora/latest/total?style=for-the-badge&logo=github&label=Download%20v0.4.0-beta02%20APK&link=https%3A%2F%2Fgithub.com%2Fsakethpathike%2FLinkora%2Freleases%2Fdownload%2Frelease-v0.4.0-beta02%2FLinkora-v0.4.0-beta02.apk"/>](https://github.com/sakethpathike/Linkora/releases/download/release-v0.4.0-beta02/Linkora-v0.4.0-beta02.apk)

[<img src="https://img.shields.io/github/downloads/sakethpathike/linkora/latest/total?style=for-the-badge&logo=github&label=v0.4.0-beta02%20release%20notes&link=https%3A%2F%2Fgithub.com%2Fsakethpathike%2FLinkora%2Freleases%2Ftag%2Frelease-v0.4.0-beta02">](https://github.com/sakethpathike/Linkora/releases/tag/release-v0.4.0-beta02)

## Screenshots

### Home Screen

|                                                          Home                                                           |                                                           Home                                                           |                                                           Home                                                           |                                                           Home                                                           |
|:-----------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------:|
| ![HomeScreen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/45a36ea1-63cb-4c07-9eb5-d8a08e0b7926) | ![Home Screen of Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/68c25f56-9d8f-4025-bc2a-81883b42aa07) | ![Home Screen of Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/ae8d6477-9882-4923-927f-1f0fb351bd55) | ![Home Screen of Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/1acf8de2-f57c-439c-9286-40568e6e562b) |

### Search Screen

|                                                           Search                                                           |                                                                Search                                                                 |                                                  Search Screen  (History links are selected)                                                  |                                                                    Search                                                                     |
|:--------------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------------------:|
| ![Search Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/f3902502-acfe-4f58-9e76-eb4aaa002b3c) | ![History in Search Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/469e9e11-c935-48f3-b797-44e8203973ca) | ![Sorting History in Search Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/0dfac08a-e544-4f5b-b87b-900e2c383823) | ![Sorting History in Search Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/d455303b-8e9b-421f-808e-e98672840d9c) |

### Collections Screen

|                                                           Collections                                                           |                                                           Collections                                                           |                                                                    Collections                                                                     |                                                                    Collections                                                                     |
|:-------------------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------:|
| ![Collections Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/668e9f4e-60cc-4d28-b04f-a4845c8a49f5) | ![Collections Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/f255c301-9a2c-4d44-a94e-f5c500a1510b) | ![Sorting Folders in Collections Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/c569aecc-70e4-41e8-9202-cadf1b4ca8c2) | ![Sorting Folders in Collections Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/818e4d32-feb5-4c09-9946-9a71b4372a50) |

|                                                           Saved Links                                                           |                                                           Important Links                                                           |                                                    Links Selection                                                     |                                                    Links Selection                                                     |
|:-------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------:|
| ![Saved Links Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/b1bab3ac-57d3-4ed9-86f4-df6b623b480e) | ![Important Links Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/73838d98-6fda-42d1-a954-73c4f16b5e98) | ![Selection in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/4a16bf68-410a-4c5a-86d0-11db86764429) | ![Selection in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/8d081299-993f-4eeb-ab9d-63a64d4deeef) |

### Archive Screen

|                                                           Archive                                                            |                                                           Archive                                                           |                                                           Archive                                                           |                                                           Archive                                                           |
|:----------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------:|
| ![Archived Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/248d813c-be2b-454c-b90a-43b293478ff9) | ![Archive Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/8046556f-8e8a-4ec5-b712-259323f346e8) | ![Archive Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/5093cf00-ce59-4e24-92e6-c189cb8f65d4) | ![Archive Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/fc9acf73-4cbe-45e1-bc3e-e7c15855d9f7) |

### Custom Folder

|                                                             Custom Folder                                                             |                                                       Custom Folder                                                        |                                                       Custom Folder                                                        |                                                       Custom Folder                                                        |
|:-------------------------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------:|
| ![Links in a Custom Folder in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/ce577c90-62fa-476d-ac46-05d38ec1557c) | ![Custom Folder in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/207c06b3-13b7-4bd4-8b61-9ad03d723741) | ![Custom Folder in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/851e9c95-0300-4d99-9df0-c6f1165f6e69) | ![Custom Folder in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/56708dca-78c6-4d12-be65-b9dacb51ce60) |

### Settings Screen

|                                                       Settings Screen                                                        |                                                       Settings Screen                                                        |
|:----------------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------------:|
| ![Settings Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/e8902ba7-03bc-4ffb-b666-aa8aa5d09011) | ![Settings Screen in Linkora](https://github.com/sakethpathike/Linkora/assets/83284398/36dee10a-8058-43d0-9d85-4f7b438ddc28) |

### Sharing from other apps

|                                                          Sharing links from other apps                                                          |
|:-----------------------------------------------------------------------------------------------------------------------------------------------:|
| <video src="https://github-production-user-asset-6210df.s3.amazonaws.com/83284398/291981034-085c7d3f-e4f2-4466-9b13-639451846233.webm"></video> |

## Upcoming Updates

- [x] Sorting
- [x] Searching
- [x] Importing and exporting links (in beta)
- [ ] Better title and image parsing of links
- [x] 
  Sub-folders (in beta)
- [ ] Backup data to cloud (Dropbox, Google Drive)
- [ ] Folder lock
- [ ] Reading links from Barcode and text
- [x] Lists similar to Twitter Lists (in beta)
- [x] Selectable items for different operations (in beta)
- [ ] Saving multiple links in a single click

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