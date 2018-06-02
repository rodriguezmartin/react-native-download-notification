
import { NativeModules, DeviceEventEmitter } from 'react-native';

const { DownloadNotification } = NativeModules;

export default {
  setup: (options)=>{
    DownloadNotification.setup(options);
  },
  create: async (title, intentHandler)=>{
    const id = await DownloadNotification.create(title);
    return new Notification(id, intentHandler);
  }
}

class Notification{
  constructor(id, intentHandler){
    this.id = id;
    this.register(intentHandler);
  }
  updateProgress = (progress)=>{
    DownloadNotification.updateProgress(this.id, progress);
  }
  setStateCompleted = ()=>{
    DownloadNotification.finish(this.id, 'completed');
  }
  setStateCancelled = ()=>{
    DownloadNotification.finish(this.id, 'cancelled');
  }
  setStateFailed = ()=>{
    DownloadNotification.finish(this.id, 'failed');
  }
  setExtraInfo = (data)=>{
    this.extraInfo = data;
  }
  getExtraInfo = ()=>{
    return this.extraInfo;
  }
  getId = ()=>{
    return this.id;
  }
  register = (intentHandler)=>{
    const ref = this;
    DeviceEventEmitter.addListener('DownloadNotification'+ this.id, (intent)=>{
      intentHandler.call(ref, intent);
      if(intent == 'open' || intent == 'dismiss'){
        ref.unregister();
      }
    });
  }
  unregister = ()=>{
    DeviceEventEmitter.removeListener('DownloadNotification'+ this.id);
  }
}