import React from 'react';
import { ZegoUIKitPrebuilt } from '@zegocloud/zego-uikit-prebuilt';
import { useParams, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';

export default function VideoCall() {
  const { roomID } = useParams();
  const navigate = useNavigate();
  const { currentUser } = useSelector(state => state.user);

  let myMeeting = async (element) => {
    // Check for environment variables
    const appID = process.env.REACT_APP_ZEGO_APP_ID ? parseInt(process.env.REACT_APP_ZEGO_APP_ID) : null;
    const serverSecret = process.env.REACT_APP_ZEGO_SERVER_SECRET || "";

    if (!appID || !serverSecret) {
      element.innerHTML = `
        <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; background: #1e1e2e; color: white; font-family: sans-serif; text-align: center; padding: 2rem;">
          <h1 style="color: #ff6b6b; margin-bottom: 1rem;">Video Call Configuration Missing</h1>
          <p style="font-size: 1.2rem; color: #a0a0b0; max-width: 600px; line-height: 1.5;">
            To enable the video call feature, you must configure your ZegoCloud credentials.
          </p>
          <div style="background: rgba(255,255,255,0.05); padding: 1.5rem; border-radius: 12px; margin-top: 2rem; text-align: left; border: 1px solid rgba(255,255,255,0.1);">
            <p style="margin-bottom: 10px;">1. Go to <a href="https://console.zegocloud.com/" target="_blank" style="color: #4facfe;">ZegoCloud Console</a></p>
            <p style="margin-bottom: 10px;">2. Create a new project (UIKits)</p>
            <p style="margin-bottom: 10px;">3. Open <b>frontend/.env</b> and add:</p>
            <code style="background: #000; padding: 10px; display: block; border-radius: 6px; margin-top: 10px; color: #00f2fe;">
              REACT_APP_ZEGO_APP_ID=your_app_id<br/>
              REACT_APP_ZEGO_SERVER_SECRET=your_server_secret
            </code>
          </div>
          <button onclick="window.history.back()" style="margin-top: 2rem; padding: 12px 24px; background: #8E54E9; color: white; border: none; border-radius: 8px; cursor: pointer; font-size: 1rem;">Go Back</button>
        </div>
      `;
      return;
    }

    try {
      const kitToken = ZegoUIKitPrebuilt.generateKitTokenForTest(
          appID, 
          serverSecret, 
          roomID, 
          currentUser?._id || Date.now().toString(), 
          currentUser?.name || "User"
      );

      // Create instance object from Kit Token.
      const zp = ZegoUIKitPrebuilt.create(kitToken);
      
      // start the call
      zp.joinRoom({
          container: element,
          sharedLinks: [
            {
              name: 'Copy link',
              url:
               window.location.protocol + '//' + 
               window.location.host + window.location.pathname +
                '?roomID=' +
                roomID,
            },
          ],
          scenario: {
            mode: ZegoUIKitPrebuilt.GroupCall,
          },
          onLeaveRoom: () => {
             navigate(-1);
          }
      });
    } catch (error) {
       console.error("ZegoCloud Error:", error);
       element.innerHTML = `
        <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; background: #1e1e2e; color: white; text-align: center; padding: 2rem;">
          <h1 style="color: #ff6b6b;">Failed to initialize Video Call</h1>
          <p style="color: #a0a0b0; margin-top: 1rem;">Please verify your App ID and Server Secret are correct.</p>
          <button onclick="window.history.back()" style="margin-top: 2rem; padding: 12px 24px; background: #8E54E9; color: white; border: none; border-radius: 8px; cursor: pointer;">Go Back</button>
        </div>
       `;
    }
  };

  return (
    <div
      className="myCallContainer"
      ref={myMeeting}
      style={{ width: '100vw', height: '100vh', background: '#1e1e2e' }}
    ></div>
  );
}
