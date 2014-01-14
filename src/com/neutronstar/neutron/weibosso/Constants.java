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

/**
 * 璇ョ被瀹氫箟浜嗗井鍗氭巿鏉冩椂鎵�闇�瑕佺殑鍙傛暟銆�
 * 
 * @author SINA
 * @since 2013-09-29
 */
public interface Constants {

    /** 褰撳墠 DEMO 搴旂敤鐨� APP_KEY锛岀涓夋柟搴旂敤搴旇浣跨敤鑷繁鐨� APP_KEY 鏇挎崲璇� APP_KEY */
//    public static final String APP_KEY      = "2045436852";
    public static final String APP_KEY      = "1221807720";
    
    /** 
     * 褰撳墠 DEMO 搴旂敤鐨勫洖璋冮〉锛岀涓夋柟搴旂敤鍙互浣跨敤鑷繁鐨勫洖璋冮〉銆�
     * 
     * <p>
     * 娉細鍏充簬鎺堟潈鍥炶皟椤靛绉诲姩瀹㈡埛绔簲鐢ㄦ潵璇村鐢ㄦ埛鏄笉鍙鐨勶紝鎵�浠ュ畾涔変负浣曠褰㈠紡閮藉皢涓嶅奖鍝嶏紝
     * 浣嗘槸娌℃湁瀹氫箟灏嗘棤娉曚娇鐢� SDK 璁よ瘉鐧诲綍銆�
     * 寤鸿浣跨敤榛樿鍥炶皟椤碉細https://api.weibo.com/oauth2/default.html
     * </p>
     */
//    public static final String REDIRECT_URL = "http://www.sina.com";
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    /**
     * Scope 鏄� OAuth2.0 鎺堟潈鏈哄埗涓� authorize 鎺ュ彛鐨勪竴涓弬鏁般�傞�氳繃 Scope锛屽钩鍙板皢寮�鏀炬洿澶氱殑寰崥
     * 鏍稿績鍔熻兘缁欏紑鍙戣�咃紝鍚屾椂涔熷姞寮虹敤鎴烽殣绉佷繚鎶わ紝鎻愬崌浜嗙敤鎴蜂綋楠岋紝鐢ㄦ埛鍦ㄦ柊 OAuth2.0 鎺堟潈椤典腑鏈夋潈鍒�
     * 閫夋嫨璧嬩簣搴旂敤鐨勫姛鑳姐��
     * 
     * 鎴戜滑閫氳繃鏂版氮寰崥寮�鏀惧钩鍙�-->绠＄悊涓績-->鎴戠殑搴旂敤-->鎺ュ彛绠＄悊澶勶紝鑳界湅鍒版垜浠洰鍓嶅凡鏈夊摢浜涙帴鍙ｇ殑
     * 浣跨敤鏉冮檺锛岄珮绾ф潈闄愰渶瑕佽繘琛岀敵璇枫��
     * 
     * 鐩墠 Scope 鏀寔浼犲叆澶氫釜 Scope 鏉冮檺锛岀敤閫楀彿鍒嗛殧銆�
     * 
     * 鏈夊叧鍝簺 OpenAPI 闇�瑕佹潈闄愮敵璇凤紝璇锋煡鐪嬶細http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 鍏充簬 Scope 姒傚康鍙婃敞鎰忎簨椤癸紝璇锋煡鐪嬶細http://open.weibo.com/wiki/Scope
     */
    public static final String SCOPE = 
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
}
