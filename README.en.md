# NFC Tools
It is an Android-based NFC tool.

## 1. Development environment
* Win10
* Android Studio Hedgehog
* Kotlin
* Compose

## 2. Test environment
RedMi K30 Pro， Android 14  
Due to the limited number of cards available, only `ACTION_TECH_DISCOVERED` was tested

## 3. Feature description
*The following tags are used in many places, and all tags are interested in the App by default*

* System scheduling  
  When the system detects an NFC tag, it can selectively start the app for further processing of the tag of interest.

* Front desk scheduling  
  When the card reader activity is in the foreground, it has the permission to prioritize the NFC tag of interest by default, and the app selection box that can be processed will no longer pop up.

* Foreground read  
  When the reader mode is enabled, the tag information is directly returned through the callback function

* Card reading  
  Currently, only tag IDs and tech lists can be read.

* other  
  Developing