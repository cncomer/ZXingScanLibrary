/*
 * Copyright 2007 ZXing authors
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

package com.bestjoy.app.common.qrcode.result;

/**
 * Represents the type of data encoded by a barcode -- from plain text, to a
 * URI, to an e-mail address, etc.
 *
 * @author Sean Owen
 */
public enum ParsedResultType {
  ADDRESSBOOK("ADDRESSBOOK"),
  EMAIL_ADDRESS("EMAIL_ADDRESS"),
  PRODUCT("PRODUCT"),
  URI("URI"),
  TEXT("TEXT"),
  GEO("GEO"),
  TEL("TEL"),
  BXK("BXK"),  //保修卡条码
  NX("NX"),  //能效条码
  SMS("SMS"),
  MYLIFE("MYLIFE"), //生活圈
  WIFI("WIFI"),
  CALENDAR("CALENDAR"),
  StarService("StarService"), //星服务
  USER_BXK_QR("USER_BXK_QR"),//保修卡二维码
  COUPON("COUPON");   //优惠劵

  private String mType;
  private ParsedResultType(String type) {
    mType = type;
  }

  public String getType() {
    return mType;
  }
}
