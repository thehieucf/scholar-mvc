package com.rhythmicscholar.scholar_mvc.service;

import com.rhythmicscholar.scholar_mvc.model.*;
import com.rhythmicscholar.scholar_mvc.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service tập trung xử lý tiến độ học tập:
 *  - Cập nhật UserWordProgress (trạng thái, SM-2 spaced repetition)
 *  - Cộng XP và tự động nâng level
 *  - Cập nhật streak (daily learning streak)
 *  - Upsert UserProgress (completion % theo category)
 *
 * Được dùng chung bởi cả flashcard (/api/study/progress) và quiz (/api/game/result).
 */
@Service
public class StudyProgressService {

    @Autowired private UserRepository userRepository;
    @Autowired private VocabularyRepository vocabularyRepository;
    @Autowired private UserWordProgressRepository userWordProgressRepository;
    @Autowired private UserProgressRepository userProgressRepository;
    @Autowired private CategoryRepository categoryRepository;

    // ----------------------------------------------------------------
    // XP thresholds
    // ----------------------------------------------------------------
    public String calculateLevel(int xp) {
        if (xp >= 2000) return "Master";
        if (xp >= 800)  return "Advanced";
        if (xp >= 250)  return "Intermediate";
        return "Beginner";
    }

    // ----------------------------------------------------------------
    // SM-2 simplified: tính nextReviewDate dựa trên consecutiveCorrect
    // Interval: 1 → 3 → 7 → 14 → 30 ngày
    // ----------------------------------------------------------------
    private LocalDate calcNextReviewDate(int consecutiveCorrect) {
        int days = switch (consecutiveCorrect) {
            case 0  -> 1;
            case 1  -> 1;
            case 2  -> 3;
            case 3  -> 7;
            case 4  -> 14;
            default -> 30;
        };
        return LocalDate.now().plusDays(days);
    }

    // ----------------------------------------------------------------
    // Cập nhật streak cho user (gọi 1 lần mỗi ngày là đủ)
    // ----------------------------------------------------------------
    private void updateStreak(User user) {
        LocalDate today = LocalDate.now();
        LocalDate lastDate = user.getLastStudiedDate();

        if (lastDate == null || lastDate.isBefore(today.minusDays(1))) {
            // Bỏ lỡ ít nhất 1 ngày → reset streak về 1
            user.setCurrentStreak(1);
        } else if (lastDate.isBefore(today)) {
            // Học ngày hôm qua → tăng streak
            user.setCurrentStreak((user.getCurrentStreak() != null ? user.getCurrentStreak() : 0) + 1);
        }
        // Nếu lastDate == today → không thay đổi streak (đã tính rồi)

        int streak = user.getCurrentStreak() != null ? user.getCurrentStreak() : 1;
        int longest = user.getLongestStreak() != null ? user.getLongestStreak() : 0;
        if (streak > longest) user.setLongestStreak(streak);
        user.setLastStudiedDate(today);
    }

    // ----------------------------------------------------------------
    // Upsert UserProgress (completion % theo category)
    // ----------------------------------------------------------------
    private void updateCategoryProgress(User user, Long categoryId) {
        long totalWords   = vocabularyRepository.countByCategoryId(categoryId);
        if (totalWords == 0) return;

        long studiedWords = userWordProgressRepository.countStudiedWordsByCategoryId(user.getId(), categoryId);
        int pct = (int) Math.min(100, studiedWords * 100 / totalWords);

        Optional<UserProgress> existing = userProgressRepository.findByUserIdAndCategoryId(user.getId(), categoryId);
        UserProgress up = existing.orElseGet(() -> {
            UserProgress newUp = new UserProgress();
            newUp.setUser(user);
            Category cat = categoryRepository.findById(categoryId).orElse(null);
            newUp.setCategory(cat);
            return newUp;
        });
        up.setCompletionPercentage(pct);
        userProgressRepository.save(up);
    }

    // ----------------------------------------------------------------
    // Ghi nhận học flashcard (lật thẻ = đã xem từ)
    // ----------------------------------------------------------------
    @Transactional
    public ProgressResult recordFlashcard(Long userId, Long vocabId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ProgressResult.error("User not found");

        Vocabulary vocab = vocabularyRepository.findById(vocabId).orElse(null);
        if (vocab == null) return ProgressResult.error("Vocabulary not found");

        // Cập nhật UserWordProgress
        Optional<UserWordProgress> existing = userWordProgressRepository.findByUserIdAndVocabularyId(userId, vocabId);
        boolean isNew = existing.isEmpty();

        UserWordProgress progress = existing.orElseGet(UserWordProgress::new);
        if (isNew) {
            progress.setUser(user);
            progress.setVocabulary(vocab);
            progress.setLearningStatus("LEARNING");
            progress.setConsecutiveCorrect(0);
        }
        progress.setIsFlashcardDone(true);
        progress.setLastStudiedAt(LocalDateTime.now());
        // Flashcard chỉ đánh dấu đã xem, không tăng consecutiveCorrect
        // nextReviewDate dựa trên consecutive hiện tại
        int consec = progress.getConsecutiveCorrect() != null ? progress.getConsecutiveCorrect() : 0;
        progress.setNextReviewDate(calcNextReviewDate(consec));
        userWordProgressRepository.save(progress);

        // XP: +5 từ mới, +2 ôn lại
        int xpGain = isNew ? 5 : 2;
        int newXp = (user.getTotalXp() != null ? user.getTotalXp() : 0) + xpGain;
        user.setTotalXp(newXp);
        user.setCurrentLevel(calculateLevel(newXp));
        updateStreak(user);
        userRepository.save(user);

        // Cập nhật UserProgress cho category
        if (vocab.getCategory() != null) {
            updateCategoryProgress(user, vocab.getCategory().getId());
        }

        return new ProgressResult(xpGain, user.getTotalXp(), user.getCurrentStreak(),
                user.getCurrentLevel(), progress.getLearningStatus(), false);
    }

    // ----------------------------------------------------------------
    // Ghi nhận kết quả quiz (đúng/sai)
    // ----------------------------------------------------------------
    @Transactional
    public ProgressResult recordQuizAnswer(Long userId, Long vocabId, boolean correct) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ProgressResult.error("User not found");

        Vocabulary vocab = vocabularyRepository.findById(vocabId).orElse(null);
        if (vocab == null) return ProgressResult.error("Vocabulary not found");

        // Cập nhật UserWordProgress
        Optional<UserWordProgress> existing = userWordProgressRepository.findByUserIdAndVocabularyId(userId, vocabId);
        boolean isNew = existing.isEmpty();

        UserWordProgress progress = existing.orElseGet(UserWordProgress::new);
        if (isNew) {
            progress.setUser(user);
            progress.setVocabulary(vocab);
            progress.setLearningStatus("LEARNING");
            progress.setConsecutiveCorrect(0);
        }

        int consec = progress.getConsecutiveCorrect() != null ? progress.getConsecutiveCorrect() : 0;
        if (correct) {
            consec++;
            progress.setConsecutiveCorrect(consec);
            // MASTERED sau 5 lần đúng liên tiếp
            if (consec >= 5) progress.setLearningStatus("MASTERED");
        } else {
            // Trả lời sai → reset consecutive về 0, quay về LEARNING
            progress.setConsecutiveCorrect(0);
            if (!"MASTERED".equals(progress.getLearningStatus())) {
                progress.setLearningStatus("LEARNING");
            }
        }

        progress.setIsQuizDone(true);
        progress.setLastStudiedAt(LocalDateTime.now());
        progress.setNextReviewDate(calcNextReviewDate(progress.getConsecutiveCorrect()));
        userWordProgressRepository.save(progress);

        // XP: +10 đúng từ mới, +5 đúng ôn lại, 0 nếu sai
        int xpGain = 0;
        if (correct) {
            xpGain = isNew ? 10 : 5;
        }
        if (xpGain > 0) {
            int newXp = (user.getTotalXp() != null ? user.getTotalXp() : 0) + xpGain;
            user.setTotalXp(newXp);
            user.setCurrentLevel(calculateLevel(newXp));
            updateStreak(user);
            userRepository.save(user);
        }

        // Cập nhật UserProgress cho category
        if (vocab.getCategory() != null) {
            updateCategoryProgress(user, vocab.getCategory().getId());
        }

        return new ProgressResult(xpGain, user.getTotalXp(), user.getCurrentStreak(),
                user.getCurrentLevel(), progress.getLearningStatus(), correct);
    }

    // ----------------------------------------------------------------
    // Result DTO nội bộ
    // ----------------------------------------------------------------
    public static class ProgressResult {
        public final int xpGained;
        public final int totalXp;
        public final int streak;
        public final String level;
        public final String status;
        public final boolean correct;
        public final String errorMessage;

        public ProgressResult(int xpGained, int totalXp, int streak,
                              String level, String status, boolean correct) {
            this.xpGained = xpGained;
            this.totalXp = totalXp;
            this.streak = streak;
            this.level = level;
            this.status = status;
            this.correct = correct;
            this.errorMessage = null;
        }

        private ProgressResult(String error) {
            this.xpGained = 0; this.totalXp = 0; this.streak = 0;
            this.level = "Beginner"; this.status = "NEW"; this.correct = false;
            this.errorMessage = error;
        }

        public static ProgressResult error(String msg) { return new ProgressResult(msg); }
        public boolean isError() { return errorMessage != null; }
    }
}
