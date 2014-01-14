/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.neutronstar.neutron.weibosso;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.neutronstar.neutron.R;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

/**
 * 璇ョ被涓昏婕旂ず濡備綍杩涜鎺堟潈銆丼SO鐧婚檰銆�
 * 
 * @author SINA
 * @since 2013-09-29
 */
public class WBAuthActivity extends Activity {

    /** 鏄剧ず璁よ瘉鍚庣殑淇℃伅锛屽 AccessToken */
    private TextView mTokenText;
    
    /** 寰崥 Web 鎺堟潈绫伙紝鎻愪緵鐧婚檰绛夊姛鑳�  */
    private WeiboAuth mWeiboAuth;
    
    /** 灏佽浜� "access_token"锛�"expires_in"锛�"refresh_token"锛屽苟鎻愪緵浜嗕粬浠殑绠＄悊鍔熻兘  */
    private Oauth2AccessToken mAccessToken;

    /** 娉ㄦ剰锛歋soHandler 浠呭綋 SDK 鏀寔 SSO 鏃舵湁鏁� */
    private SsoHandler mSsoHandler;

    /**
     * @see {@link Activity#onCreate}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        
        // 鑾峰彇 Token View锛屽苟璁╂彁绀� View 鐨勫唴瀹瑰彲婊氬姩锛堝皬灞忓箷鍙兘鏄剧ず涓嶅叏锛�
        mTokenText = (TextView) findViewById(R.id.token_text_view);
        TextView hintView = (TextView) findViewById(R.id.obtain_token_hint);
        hintView.setMovementMethod(new ScrollingMovementMethod());

        // 鍒涘缓寰崥瀹炰緥
        mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        
        // 閫氳繃搴旂敤绛惧悕淇℃伅鑾峰彇 Token
        findViewById(R.id.obtain_token_via_signature).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeiboAuth.anthorize(new AuthListener());
                // 鎴栬�呬娇鐢細mWeiboAuth.authorize(new AuthListener(), Weibo.OBTAIN_AUTH_TOKEN);
            }
        });
        
        // 閫氳繃鍗曠偣鐧诲綍 (SSO) 鑾峰彇 Token
        findViewById(R.id.obtain_token_via_sso).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSsoHandler = new SsoHandler(WBAuthActivity.this, mWeiboAuth);
                mSsoHandler.authorize(new AuthListener());
            }
        });
        
        //del by yy
        // 閫氳繃 Code 鑾峰彇 Token
//        findViewById(R.id.obtain_token_via_code).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(WBAuthActivity.this, WBAuthCodeActivity.class));
//            }
//        });

        // 浠� SharedPreferences 涓鍙栦笂娆″凡淇濆瓨濂� AccessToken 绛変俊鎭紝
        // 绗竴娆″惎鍔ㄦ湰搴旂敤锛孉ccessToken 涓嶅彲鐢�
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (mAccessToken.isSessionValid()) {
            updateTokenView(true);
        }
    }

    /**
     * 褰� SSO 鎺堟潈 Activity 閫�鍑烘椂锛岃鍑芥暟琚皟鐢ㄣ��
     * 
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // SSO 鎺堟潈鍥炶皟
        // 閲嶈锛氬彂璧� SSO 鐧婚檰鐨� Activity 蹇呴』閲嶅啓 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 寰崥璁よ瘉鎺堟潈鍥炶皟绫汇��
     * 1. SSO 鎺堟潈鏃讹紝闇�瑕佸湪 {@link #onActivityResult} 涓皟鐢� {@link SsoHandler#authorizeCallBack} 鍚庯紝
     *    璇ュ洖璋冩墠浼氳鎵ц銆�
     * 2. 闈� SSO 鎺堟潈鏃讹紝褰撴巿鏉冪粨鏉熷悗锛岃鍥炶皟灏变細琚墽琛屻��
     * 褰撴巿鏉冩垚鍔熷悗锛岃淇濆瓨璇� access_token銆乪xpires_in銆乽id 绛変俊鎭埌 SharedPreferences 涓��
     */
    class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 浠� Bundle 涓В鏋� Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 鏄剧ず Token
                updateTokenView(false);
                
                // 淇濆瓨 Token 鍒� SharedPreferences
                AccessTokenKeeper.writeAccessToken(WBAuthActivity.this, mAccessToken);
                Toast.makeText(WBAuthActivity.this, 
                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
            } else {
                // 褰撴偍娉ㄥ唽鐨勫簲鐢ㄧ▼搴忕鍚嶄笉姝ｇ‘鏃讹紝灏变細鏀跺埌 Code锛岃纭繚绛惧悕姝ｇ‘
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(WBAuthActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(WBAuthActivity.this, 
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(WBAuthActivity.this, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * 鏄剧ず褰撳墠 Token 淇℃伅銆�
     * 
     * @param hasExisted 閰嶇疆鏂囦欢涓槸鍚﹀凡瀛樺湪 token 淇℃伅骞朵笖鍚堟硶
     */
    private void updateTokenView(boolean hasExisted) {
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(mAccessToken.getExpiresTime()));
        String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
        mTokenText.setText(String.format(format, mAccessToken.getToken(), date));
        
        String message = String.format(format, mAccessToken.getToken(), date);
        if (hasExisted) {
            message = getString(R.string.weibosdk_demo_token_has_existed) + "\n" + message;
        }
        mTokenText.setText(message);
    }
}