<div align="center">
<picture>
  <source media="(prefers-color-scheme: dark)" srcset="assets/ic_launcher_dark.png">
  <img alt="" src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png">
</picture><br>
<h1 align="center">GCS for Tasker</h1>
<a href="https://developer.android.com/tools/releases/platforms#5.0">
<img alt="API 21+" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=for-the-badge&color=FF0000" title="Android 5.0 Lollipop"></a>
<a href="https://github.com/abhishekabhi789/GCS_for_Tasker/releases/latest">
<img alt="GitHub release (latest by date including pre-releases)" src="https://img.shields.io/github/v/release/abhishekabhi789/GCS_for_Tasker?include_prereleases&style=for-the-badge&color=0091EA" title="latest release version"></a>
<a href="https://github.com/abhishekabhi789/GCS_for_Tasker/releases">
<img alt="GitHub all releases" src="https://img.shields.io/github/downloads/abhishekabhi789/GCS_for_Tasker/total?style=for-the-badge&color=00C853" title="Total download count"></a>
<br>
<br>
A code scanning plugin for <a href="https://joaoapps.com/tasker/">Tasker</a> that uses the <a href="https://developers.google.com/ml-kit/vision/barcode-scanning/code-scanner">Google Code Scanner</a> API.
</div>

## :gear: Requirements

- This is a [Tasker Plugin](https://tasker.joaoapps.com/plugins-intro.html), and it requires the [Tasker](https://joaoapps.com/tasker/) Android app to function.

- This app works on Android 5.0(Lollipop) and higher devices as the Code Scanner API works on devices with this API level or above.

- The app works only on devices having updated Google Play Services installed.

## :shield: Permissions

- #### Manage overlay permission (Draw-over-other-apps permission)
  From Android 10 - Q (API 29) android system restricted launching activities from background([more info here](https://developer.android.com/guide/components/activities/background-starts)).
  App will be exempted from this restriction if it has the manage overlay permission.

- #### Post Notification
  On some low memory devices, the android system kills the app when it goes to background while scanner UI is visible.
  Therefore, once scanning is done, the app may not be running to receive the result.
  Running a service may keep the app in memory, and it requires a notification to be posted.
  This permission is not requested at run time because not every device needs it. If you encounter this issue, try granting it.

- ### Starting from background
  App has to be white listed from any restriction that can prevent starting the scanner from background, like `Open new windows while
  running in the background` in MiUi.

## :dna: Variants

There are two variants available for this project based on the theme used in plugin configuration activity.
The classic variant is smaller in size, and material variant is twice as bigger.
Both variants share the same package name and are signed with the same key, so you can switch between them anytime.
It is recommended to clear the app data after switching to free up storage space consumed by the previous variant.

<details><summary>

## :hammer_and_wrench: How to Setup

</summary>
Install this plugin app and Tasker.

#### In Tasker

- For events, select the "Event" option, then choose "Plugin" and select "GCS for Tasker" from the
  list. From there, you can configure the event based on your preferences.

    * **Value filter**: The event will trigger only when the raw value of the scanned code matches the
      value filter. This field supports both simple and regex matching. Use the switch next to
      this field to choose matching method.
    * **Type filter**: The event will trigger only when the qr code type matches any of the type filter.
      Refer [Barcode.BarcodeValueType](https://developers.google.com/android/reference/com/google/mlkit/vision/barcode/common/Barcode.BarcodeValueType)
      for more about code types.
    * **Format filter**: The event will trigger only when the code format matches any of the format
      filter.
      Refer [Barcode.BarcodeFormat](https://developers.google.com/android/reference/com/google/mlkit/vision/barcode/common/Barcode.BarcodeFormat)
      for more about code formats.

  The event will trigger only if all specified filters are satisfied.

- For actions, select "Plugin" from the action list, and then choose "GCS for
  Tasker". Then on the configuration activity you can configure below settings.
 
    * **Format filter**: The code scanner will only detect codes having the chosen format.
    * **Enable auto zoom**: The scanner will try to automatically zoom the camera towards the code.
    * **Allow manual input**: The scanner allows manual input of code value.


#### In GCS for Tasker

After completing the Tasker setup, try to perform a scan.

- If scanner modules are present, you will see the camera opened with the Google Code Scanner UI.
- If scanner modules are absent, you will see a toast message saying "Waiting for the Barcode UI
  module to be downloaded."
    * This download is a background task and is handled by the Google Play Service. You won't be
      notified when the task is completed.

Here's a demo project. Import it from [TaskerNet](https://taskernet.com/shares/?user=AS35m8mVC%2FNlWH31JCTnGHpKVeZk1osEp8V1pFxCq1Ls28Un1RXCw9ZNWWvmpxOebt4WIYFeiZhZKHc%3D&id=Project%3AGCS4T+Example+Project)

The app also initializes a shortcut during the first scan. From this app shortcut, you'll be able to choose a Tasker task related to the scanner for quick access, such as `GCS4T: View History` in the demo project.
</details>

<details><summary>

## :wrench: Troubleshoot

</summary>

#### Scanner module not downloading.

>- Ensure internet connection.
>- Make sure the battery saver is turned off.
>- Update play service if available.
>- Reboot the device.
>
> If problem persists try
>- clearing the data of Google Play Services (:warning: Use caution when deleting)
>- Try upgrading or downgrading Google Play Services.

#### Scanner Action not launching

>- Remove any restriction such as battery saver, background start, draw-over-other-apps, notification and try again.
>- If the issue persists, report it here.

#### Error: Failed to scan code.

> If the scanner returns 'Failed to scan code' error, try clearing the app data of Google Play Service.
> It's a Google Code Scanner module related issue.
</details>
