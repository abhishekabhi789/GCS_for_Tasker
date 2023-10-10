<div align="center">
<picture>
  <source media="(prefers-color-scheme: dark)" srcset="assets/ic_launcher_dark.png">
  <img alt="" src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png">
</picture><br>
<h1 align="center">GCS for Tasker</h1>
<a href="https://developer.android.com/tools/releases/platforms#5.0">
<img alt="API 21+" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=for-the-badge&color=FF0800" title="Android 5.0 Lollipop"></a>
<a href="https://github.com/abhishekabhi789/GCS_for_Tasker/releases">
<img alt="GitHub all releases" src="https://img.shields.io/github/downloads/abhishekabhi789/GCS_for_Tasker/total?style=for-the-badge&color=00C853" title="Total download count"></a>
<a href="https://github.com/abhishekabhi789/GCS_for_Tasker/releases/latest">
<img alt="GitHub release (latest by date including pre-releases)" src="https://img.shields.io/github/v/release/abhishekabhi789/GCS_for_Tasker?include_prereleases&style=for-the-badge&color=0091EA" title="latest release version"></a>
<img alt="demo project download" src="https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Ftaskernet.com%2F_ah%2Fapi%2Fdatashare%2Fv1%2Fshares%2FAS35m8mVC%252FNlWH31JCTnGHpKVeZk1osEp8V1pFxCq1Ls28Un1RXCw9ZNWWvmpxOebt4WIYFeiZhZKHc%253D%2FProject%253AGCS4T%2520Example%2520Project%3Fa%3D0&query=%24.info.stats.downloads&prefix=Downloads%3A%20&style=for-the-badge&label=Taskernet&labelColor=yellow&color=grey&link=https%3A%2F%2Ftaskernet.com%2Fshares%2F%3Fuser%3DAS35m8mVC%252FNlWH31JCTnGHpKVeZk1osEp8V1pFxCq1Ls28Un1RXCw9ZNWWvmpxOebt4WIYFeiZhZKHc%253D%26id%3DProject%253AGCS4T%2BExample%2BProject" title="import the demo project from taskernet" >

</div>GCS for Tasker is an Android application that can act as both a Tasker event plugin and action
plugin. This app uses the Google Code Scanner API to scan and extract information from QR codes.<br>


## :gear: Requirements

This is a [Tasker Plugin](https://tasker.joaoapps.com/plugins-intro.html), and it requires
the [Tasker](https://joaoapps.com/tasker/) Android app to function. To use this app, you must have
an Android device with an API level of [21](https://developer.android.com/tools/releases/platforms#5.0 "Android 5.0 Lollipop") or higher, as the Code Scanner API works on devices with
this API level or above. Furthermore, it is necessary to have the Google Play Services installed on the device.

## :bulb: Features

With the GCS for Tasker app, you can easily integrate QR code scanning into your Tasker projects.
This app supports both scanning events and actions. This app doesn't ask for camera permissions, nor
does it store or share any data except with Tasker.

## :dna: Variants

There are two variants available for this project based on the theme used in event configuration activity. Classic themed app is smaller in size and material themed app is around 4 times bigger. Both are having same package name and are signed with same key, you can switch them anytime. It's recommended to clear the app data after such switching to release storage space consumed by previous variant.

<details><summary>

## :hammer_and_wrench: How to Setup
</summary>
Install this plugin app and Tasker.

#### In Tasker

- For events, select the "Event" option, then choose "Plugin" and select "GCS for Tasker" from the
  list. From there, you can configure the event based on your preferences.
	
	* Value filter: The event will trigger only when the raw value of the scanned code matches the value filter. This field supports both simple and regex matching. Adjust the switch next to this field to choose simple matching or regex matching.
	* Type filter: The event will trigger only when the qr code type matches any of the type filter. Refer [Barcode.BarcodeValueType](https://developers.google.com/android/reference/com/google/mlkit/vision/barcode/common/Barcode.BarcodeValueType) for more about code types.
  * Format filter: The event will trigger only when the code format matches any of the format filter. Refer [Barcode.BarcodeFormat](https://developers.google.com/android/reference/com/google/mlkit/vision/barcode/common/Barcode.BarcodeFormat) for more about code formats.
	
  The event will trigger only if all specified filters are satisfied.
	
- For actions, select "Plugin" from the "Select action category" list, and then choose "GCS for Tasker". Then on the configuration activity you can configure below settings.
  * Format filter: The code scanner will only detect codes having the chosen format.
  * Enable auto zoom: The scanner will try to automatically zoom the camera towards the code.
  * Allow manual input: The scanner allows manual input of code value.

All the code types and code formats can be copied from event and action configuration.

#### In GCS for Tasker

After completing the Tasker setup, try to perform a scan.

- If scanner modules are present, you will see the camera opened with the Google Code Scanner UI.
- If scanner modules are absent, you will see a toast message saying "Waiting for the Barcode UI
  module to be downloaded."
    * This download is a background task and is handled by the Google Play Service. You won't be
      notified when the task is completed.

Here's an example project. Import it from [TaskerNet](https://taskernet.com/shares/?user=AS35m8mVC%2FNlWH31JCTnGHpKVeZk1osEp8V1pFxCq1Ls28Un1RXCw9ZNWWvmpxOebt4WIYFeiZhZKHc%3D&id=Project%3AGCS4T+Example+Project)
</details>
<details><summary>

## :question: FAQ
</summary>

 #### Can this be used on a device that does not have Google Play Services?
  > No, the app uses the unbundled Google code scanner API provided by Google Play Services on the
  device.

 #### Does the app require an internet connection to function?
  > This app does not require an internet connection since the scanner library is capable of working
  offline. However, Google Play Services requires an internet connection to download QR scanner
  libraries if they are not already present on your device.
  
 #### Does clearing app data delete any setup or data?
  > All configuration data is stored in Tasker and the scanner library is in google play services,
  so by clearing the app data of this app will not make any problem. Also, if you want to save
  the scan results, you have to set up a Tasker task to do so.
  
</details>
<details><summary>

## :wrench: Troubleshoot
</summary>

 #### Scanner module not downloading.
  >- Ensure internet connection.
  >- Ensure battery saver is turned off.
  >- Update play service if available.
  >- Reboot device.
  >
  > If problem persists try
  >- clearing the data of Google Play Services (Attention!: Use caution when deleting)
  >- Upgrade or downgrade Google Play Services.

 #### Code Scanned but no response from Tasker.
  >- Try a different code to make sure the tasker setup is correct.
  >- Check Tasker run log.
  >- Try both event and action.
  >- Create a new event/action without any filter rules and flash the output.

 #### Scanner UI doesn't dismiss after action timeout.

> The library does not provide a method to programmatically close or set a timeout for the scanner.
> You can consider using Tasker/AutoInput to click the close button or the back button after the action fails due to timeout.

</details>