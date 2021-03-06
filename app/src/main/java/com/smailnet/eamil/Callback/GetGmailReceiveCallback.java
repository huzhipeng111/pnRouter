/*
 * Copyright 2018 Lake Zhang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smailnet.eamil.Callback;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.smailnet.eamil.EmailMessage;

import java.util.List;

/**
 * 获取邮件结果回调
 */
public interface GetGmailReceiveCallback {
    void gainSuccess(List<EmailMessage> messageList, long totalCount, long totalUnreadCount, Boolean noMoreData, String error, String menu,String pageToken);
    void gainFailure(String errorMsg);
    void authFailure(UserRecoverableAuthIOException errorMsg);
    void googlePlayFailure(GooglePlayServicesAvailabilityIOException errorMsg);
}
