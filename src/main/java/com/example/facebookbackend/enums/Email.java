package com.example.facebookbackend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Email {
    REGISTER("Mã xác nhận Đăng ký Tài khoản",
            """
                    Xin chào ${username},
                    Chúng tôi đã nhận được thông tin đăng ký tài khoản Tào Lao Social Network của bạn vào lúc ${time}.
                    Mã kích hoạt tài khoản của bạn là ${code}. Mã này có hiệu lực trong 5 phút.
                    Trân trọng!"""),
    DISABLE_ACCOUNT("Thông báo Khóa tài khoản",
            """
                    Xin chào ${username},
                    Tài khoản của bạn đã vi phạm tiêu chuẩn cộng đồng của Tào Lao Social Network 3 lần.
                    Chúng tôi quyết định khóa tài khoản của bạn
                    Trân trọng!"""),
    REMOVE_POST("Thông báo Gỡ bài viết",
            """
                    Xin chào ${username},
                    Bài viết của bạn đã vi phạm tiêu chuẩn cộng đồng của Tào Lao Social Network. Chúng tôi quyết định gỡ bài viết này của bạn.
                    Ngoài ra, tài khoản của bạn sẽ bị tạm khóa trong vòng 24h tính từ thời điểm hiện tại
                    Trân trọng!"""),
    CHANGE_PASSWORD("Mã xác nhận Thay đổi mật khẩu",
            """
                    Xin chào ${username},
                    Bạn vừa thực hiện yêu cầu đặt lại mật khẩu trên Tào Lao Social Network vào lúc ${time}.
                    Mã đặt lại mật khẩu của bạn là ${code}. Mã này có hiệu lực trong 5 phút. Vui lòng không để lộ mã này cho người khác.
                    Hãy truy cập http://localhost:4848/AppFacebook/auth/ResetPassword để đặt lại mật khẩu.
                    Trân trọng!
                    """);

    private final String subject;
    private final String content;

}
