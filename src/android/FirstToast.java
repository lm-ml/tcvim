
package com.first.toast;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.video.AVChatCameraCapturer;
import com.netease.nimlib.sdk.avchat.video.AVChatVideoCapturerFactory;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FirstToast extends CordovaPlugin {
    private CallbackContext mCallbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.mCallbackContext = callbackContext;
        if ("toast".equals(action)) {
            String msg = args.getString(0);
            Toast.makeText(cordova.getActivity(), msg, Toast.LENGTH_SHORT).show();
            callbackContext.success("success");
            return true;
        }
        if ("initNIMClient".equals(action)) {
            JSONObject loginIMU = args.getJSONObject(0);
            String account = loginIMU.getString("account");
            String token = loginIMU.getString("token");
            try {
                this.login(account, token);
            } catch (Exception ex) {
                String errorpMsg = ex.toString();
                Toast.makeText(cordova.getActivity(), errorpMsg, Toast.LENGTH_SHORT).show();
                callbackContext.error("初始化失败.....");
            }
            callbackContext.success("初始化成功.....");
            return true;
        }
        if ("registerObserver".equals(action)) {
            try {
                this.registerAVChatIncomingCallObserver(true);
            } catch (Exception ex) {
                String errorMsg = ex.toString();
                Toast.makeText(cordova.getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                callbackContext.error("初始化通话监听失败.....");
            }
            callbackContext.success("初始化通话监听成功.....");
            return true;
        }
        if ("logoutNIMClient".equals(action)) {
            NIMClient.getService(AuthService.class).logout();
            return true;
        }
        mCallbackContext.error("error");
        return false;
    }

    private void login(String account, String token) {
        LoginInfo loginInfo = new LoginInfo(account, token);
        RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                Toast.makeText(cordova.getActivity(), "恭喜您登陆成功", Toast.LENGTH_SHORT).show();
                initNIMClient(loginInfo);
            }

            @Override
            public void onFailed(int code) {
                Toast.makeText(cordova.getActivity(), "Failed:" + code, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable exception) {
            }
        };
        NIMClient.getService(AuthService.class).login(loginInfo).setCallback(callback);
    }


    public void initNIMClient(LoginInfo loginInfo) {
        Context context = cordova.getActivity().getApplicationContext();
        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        NIMClient.init(context, loginInfo, options());
    }

    // 如果返回值为 null，则全部使用默认参数。
    private SDKOptions options() {
        SDKOptions options = new SDKOptions();
        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        // config.notificationEntrance = WelcomeActivity.class; // 点击通知栏跳转到该Activity
        // config.notificationSmallIconId = R.drawable.ic_stat_notify_msg;
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        return options;
    }

    private void registerAVChatIncomingCallObserver(boolean register) {
        AVChatManager.getInstance().observeIncomingCall(new Observer<AVChatData>() {

            @Override
            public void onEvent(AVChatData data) {
                receiveInComingCall(data);
            }
        }, register);
    }

    /**
     * 接听来电
     */
    private void receiveInComingCall(AVChatData avChatData) {
        Context context = cordova.getActivity().getApplicationContext();
        //接听，告知服务器，以便通知其他端
        AVChatManager.getInstance().enableRtc();
        AVChatCameraCapturer mVideoCapturer = AVChatVideoCapturerFactory.createCameraPolicyCapturer(true);
        AVChatManager.getInstance().setupVideoCapturer(mVideoCapturer);
        AVChatManager.getInstance().enableVideo();
        AVChatManager.getInstance().startVideoPreview();

        AVChatManager.getInstance().accept2(avChatData.getChatId(), new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }

            @Override
            public void onFailed(int code) {
                if (code == -1) {
                    Toast.makeText(context, "本地音视频启动失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "建立连接失败", Toast.LENGTH_SHORT).show();
                }
                handleAcceptFailed();
            }

            @Override
            public void onException(Throwable exception) {
                handleAcceptFailed();
            }
        });
    }

    private void handleAcceptFailed() {
        AVChatManager.getInstance().stopVideoPreview();
        AVChatManager.getInstance().disableVideo();
        AVChatManager.getInstance().disableRtc();
    }


}
