package com.rhythmicscholar.scholar_mvc.service;

import com.rhythmicscholar.scholar_mvc.model.Badge;
import com.rhythmicscholar.scholar_mvc.model.User;
import com.rhythmicscholar.scholar_mvc.model.UserBadge;
import com.rhythmicscholar.scholar_mvc.repository.BadgeRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserBadgeRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserWordProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service kiểm tra và trao huy hiệu (Badge) cho người dùng.
 * Được gọi sau mỗi lần học từ vựng để kiểm tra điều kiện mở khóa.
 */
@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private UserWordProgressRepository userWordProgressRepository;

    /**
     * Kiểm tra tất cả badge và trao những badge user đủ điều kiện nhưng chưa có.
     *
     * @param user User cần kiểm tra
     * @return Danh sách badge mới được trao trong lần này (để hiển thị thông báo)
     */
    public List<Badge> checkAndAwardBadges(User user) {
        List<Badge> allBadges = badgeRepository.findAllByOrderByDisplayOrderAsc();
        List<Badge> newlyEarned = new ArrayList<>();

        // Lấy số liệu cần thiết một lần để tránh query nhiều lần
        long masteredCount = userWordProgressRepository.countMasteredByUserId(user.getId());
        long studiedCount  = userWordProgressRepository.countStudiedByUserId(user.getId());
        int  currentStreak = user.getCurrentStreak() != null ? user.getCurrentStreak() : 0;
        int  longestStreak = user.getLongestStreak() != null ? user.getLongestStreak() : 0;
        int  totalXp       = user.getTotalXp() != null ? user.getTotalXp() : 0;

        for (Badge badge : allBadges) {
            // Bỏ qua nếu user đã có badge này
            if (userBadgeRepository.existsByUserIdAndBadgeId(user.getId(), badge.getId())) {
                continue;
            }

            boolean earned = switch (badge.getConditionType()) {
                case "STREAK"   -> Math.max(currentStreak, longestStreak) >= badge.getConditionValue();
                case "XP"       -> totalXp >= badge.getConditionValue();
                case "MASTERED" -> masteredCount >= badge.getConditionValue();
                case "WORDS"    -> studiedCount >= badge.getConditionValue();
                default         -> false;
            };

            if (earned) {
                UserBadge userBadge = new UserBadge();
                userBadge.setUser(user);
                userBadge.setBadge(badge);
                userBadgeRepository.save(userBadge);
                newlyEarned.add(badge);
            }
        }

        return newlyEarned;
    }

    /**
     * Seed dữ liệu badge mặc định nếu bảng badges còn trống.
     * Được gọi khi ứng dụng khởi động.
     */
    public void seedDefaultBadges() {
        if (badgeRepository.count() > 0) return;

        List<Badge> defaults = List.of(
            badge("First Step",        "Study your first word",              "👣", "emerald", "WORDS",    1,  0),
            badge("Word Collector",    "Study 10 words",                     "📚", "blue",    "WORDS",   10,  1),
            badge("Vocabulary Builder","Study 50 words",                     "🏗️", "indigo",  "WORDS",   50,  2),
            badge("Century Scholar",   "Study 100 words",                    "💯", "violet",  "WORDS",  100,  3),
            badge("Word Master",       "Study 500 words",                    "🎓", "purple",  "WORDS",  500,  4),
            badge("On Fire",           "Maintain a 3-day streak",            "🔥", "orange",  "STREAK",   3,  5),
            badge("Week Warrior",      "Maintain a 7-day streak",            "⚔️", "amber",   "STREAK",   7,  6),
            badge("Monthly Hero",      "Maintain a 30-day streak",           "🦸", "red",     "STREAK",  30,  7),
            badge("Centurion",         "Maintain a 100-day streak",          "🏆", "yellow",  "STREAK", 100,  8),
            badge("XP Rookie",         "Earn 100 XP",                        "⚡", "sky",     "XP",     100,  9),
            badge("XP Hunter",         "Earn 500 XP",                        "🎯", "cyan",    "XP",     500, 10),
            badge("XP Legend",         "Earn 1000 XP",                       "👑", "gold",    "XP",    1000, 11),
            badge("Perfectionist",     "Master 10 words",                    "✨", "teal",    "MASTERED", 10, 12),
            badge("The Grammarian",    "Master 50 words",                    "📖", "green",   "MASTERED", 50, 13),
            badge("The Legend",        "Master 200 words",                   "🌟", "pink",    "MASTERED",200, 14)
        );

        badgeRepository.saveAll(defaults);
    }

    private Badge badge(String name, String desc, String emoji, String color,
                        String condType, int condValue, int order) {
        Badge b = new Badge();
        b.setName(name);
        b.setDescription(desc);
        b.setIconEmoji(emoji);
        b.setColorTheme(color);
        b.setConditionType(condType);
        b.setConditionValue(condValue);
        b.setDisplayOrder(order);
        return b;
    }
}
