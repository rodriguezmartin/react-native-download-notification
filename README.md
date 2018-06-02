# react-native-download-notification
Create, update and handle intents from Android notifications with progress bar in React Native.

## Instalation
```
npm install rodriguezmartin/react-native-download-notification --save
``` 

## Usage
```js
// First import it
import  DownloadNotification  from  'react-native-download-notification';

// Create the function which will be called when the notification is tapped
intentHandler(intent){
  // intent is a String, depending on the notification state could be:
  //   'cancel' if download is on going
  //   'open' if it is set to completed
  //   'dismiss' if the user dismisses it
  if(intent === 'cancel'){
    // a reference of the notification tapped is binded
    this.setStateCancelled();
  }
}

// Create a notification
const notification = await DownloadNotification.create(
  'Document.pdf', // The notification title
  this.intentHandler // The intentHandler function created above
);

// Update the progress
notification.updateProgress(progress); // int from 0 to 100

// Set the finished state
notification.setStateCompleted();
notification.setStateCancelled();
notification.setStateFailed();
```

## Customization
```js
// You can globally customize the icon and labels of the notifications.
componentDidMount(){
  DownloadNotification.setup({
    icon: 'ic_launcher', // Default is the 'get_app' material icon.
    downloadingLabel: 'Downloading...', // Default is 'Downloading' 
    completedLabel: 'The download is complete', // Default is 'Download Completed'
    failedLabel: 'The download has failed', // Default is 'Download Failed'
    cancelledLabel: 'The download has been cancelled' // Default is 'Download Cancelled'
  });
}
```