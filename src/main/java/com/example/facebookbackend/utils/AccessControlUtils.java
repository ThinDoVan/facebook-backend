package com.example.facebookbackend.utils;

import com.example.facebookbackend.entities.Post;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.enums.ERole;
import com.example.facebookbackend.repositories.FriendshipRepository;
import com.example.facebookbackend.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccessControlUtils {
    private final FriendshipRepository friendshipRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AccessControlUtils(FriendshipRepository friendshipRepository, RoleRepository roleRepository) {
        this.friendshipRepository = friendshipRepository;
        this.roleRepository = roleRepository;
    }

    public boolean isFriend(User user1, User user2) {
        return (friendshipRepository.findByUser1AndUser2(user1, user2) != null)
                || (friendshipRepository.findByUser1AndUser2(user2, user1) != null);
    }

    public boolean isAdmin(User user) {
        return user.getRoleSet().contains(roleRepository.findByRole(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Role")));
    }

    public boolean isAuthor(User user, User author) {
        return user.equals(author);
    }



    public boolean checkEditPermission(User user, User author) {
        return isAuthor(user, author);
    }

    public boolean checkDeletePermission(User user, User author) {
        return isAuthor(user, author)
                || isAdmin(user);
    }

    public boolean checkReadPermission(User user, Post post) {
        if ((user.equals(post.getCreatedUser()))
                || (isAdmin(user))) {
            return true;
        } else {
            switch (post.getAudience().getAudienceType()) {
                case ONLYME -> {
                    return false;
                }
                case FRIENDS -> {
                    return isFriend(user, post.getCreatedUser());
                }
                default -> {
                    return true;
                }
            }
        }
    }
}
