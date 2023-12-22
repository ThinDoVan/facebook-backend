package com.example.facebookbackend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Email {
    REGISTER_MAIL("Thông báo Đăng ký ứng dụng",
            "Xin chào ${username}," +
            "\nChúng tôi đã nhận được thông tin đăng ký tài khoản Tào Lao Social Network của bạn. " +
                    "Hiện tại tài khoản nãy đã được active và bạn có thể bắt đầu sử dụng. " +
                    "\nTrân trọng!"),
    DISABLE_ACCOUNT("Thông báo Khóa tài khoản",
                          "Xin chào ${username}," +
                          "\nTài khoản của bạn đã vi phạm tiêu chuẩn cộng đồng của Tào Lao Social Network 3 lần. " +
                            "Chúng tôi quyết định khóa tài khoản của bạn" +
                          "\nTrân trọng!"),
    REMOVE_POST("Thông báo Gỡ bài viết",
            "Xin chào ${username}," +
                    "\nBài viết của bạn đã vi phạm tiêu chuẩn cộng đồng của Tào Lao Social Network. " +
                    "Chúng tôi quyết định gỡ bài viết này của bạn" +
                    "\nTrân trọng!");
    private final String subject;
    private final String content;

}
