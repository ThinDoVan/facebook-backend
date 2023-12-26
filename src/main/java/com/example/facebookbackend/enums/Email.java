package com.example.facebookbackend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Email {
    REGISTER("Thông báo Đăng ký ứng dụng",
            """
                    Xin chào ${username},
                    Chúng tôi đã nhận được thông tin đăng ký tài khoản Tào Lao Social Network của bạn vào lúc ${time}. Hiện tại tài khoản này đã được active và bạn có thể bắt đầu sử dụng.\s
                    Trân trọng!"""),
    DISABLE_ACCOUNT("Thông báo Khóa tài khoản",
            """
                    Xin chào ${username},
                    Tài khoản của bạn đã vi phạm tiêu chuẩn cộng đồng của Tào Lao Social Network 3 lần. Chúng tôi quyết định khóa tài khoản của bạn
                    Trân trọng!"""),
    REMOVE_POST("Thông báo Gỡ bài viết",
            """
                    Xin chào ${username},
                    Bài viết của bạn đã vi phạm tiêu chuẩn cộng đồng của Tào Lao Social Network. Chúng tôi quyết định gỡ bài viết này của bạn
                    Trân trọng!"""),
    CHANGE_PASSWORD("Thông báo Thay đổi mật khẩu",
            """
                    Xin chào ${username},
                    Bạn vừa thực hiện yêu cầu thay đổi mật khẩu trên Tào Lao Social Network vào lúc ${time}.
                    Trân trọng!
                    """),

    FORGOT_PASSWORD("Mã khôi phục tài khoản",
            """
                    Xin chào ${username},
                    Chúng tôi vừa nhận được yêu cầu cấp lại mật khẩu Tào Lao Social Network của bạn vào lúc ${time}.
                    Mã đặt lại mật khẩu của bạn là ${code}. Mã này có hiệu lực trong 5 phút. Vui lòng không để lộ mã này cho người khác.             
                    Hãy truy cập http://localhost:4848/AppFacebook/auth/ResetPassword để đặt lại mật khẩu.       
                    """);

    private final String subject;
    private final String content;

}
