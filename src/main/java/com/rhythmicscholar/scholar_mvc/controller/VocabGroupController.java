package com.rhythmicscholar.scholar_mvc.controller;

import com.rhythmicscholar.scholar_mvc.model.VocabGroup;
import com.rhythmicscholar.scholar_mvc.model.VocabGroupItem;
import com.rhythmicscholar.scholar_mvc.repository.VocabGroupItemRepository;
import com.rhythmicscholar.scholar_mvc.repository.VocabGroupRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller quản lý nhóm từ vựng cá nhân (VocabGroup) và từ trong nhóm (VocabGroupItem).
 * Tất cả endpoint yêu cầu user đã đăng nhập (userId lấy từ session).
 */
@RestController
@RequestMapping("/api/vocab-groups")
public class VocabGroupController {

    private final VocabGroupRepository vocabGroupRepository;
    private final VocabGroupItemRepository vocabGroupItemRepository;

    public VocabGroupController(VocabGroupRepository vocabGroupRepository,
                                VocabGroupItemRepository vocabGroupItemRepository) {
        this.vocabGroupRepository = vocabGroupRepository;
        this.vocabGroupItemRepository = vocabGroupItemRepository;
    }

    // ============================================================
    // HELPER: lấy userId từ session (fallback = 1 để dev/demo)
    // ============================================================
    private Long getUserId(HttpSession session) {
        Object sessionUserId = session.getAttribute("userId");
        if (sessionUserId instanceof Long l) return l;
        if (sessionUserId instanceof Integer i) return i.longValue();
        if (sessionUserId instanceof String s) return Long.parseLong(s);
        return 1L; // Fallback
    }

    // ============================================================
    // GET /api/vocab-groups  –  Lấy tất cả nhóm của user hiện tại
    // ============================================================
    /**
     * Trả về danh sách tất cả nhóm từ vựng của user.
     * Mỗi nhóm bao gồm thêm trường "wordCount" (số từ trong nhóm).
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllGroups(HttpSession session) {
        Long userId = getUserId(session);
        List<VocabGroup> groups = vocabGroupRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        // Lấy tất cả counts trong 1 query duy nhất thay vì N query
        List<Object[]> countsRaw = vocabGroupItemRepository.countItemsGroupedByGroupId();
        Map<Long, Long> countsMap = new HashMap<>();
        for (Object[] row : countsRaw) {
            countsMap.put(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
        }

        // Bổ sung wordCount vào từng nhóm
        List<Map<String, Object>> result = groups.stream().map(g -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", g.getId());
            map.put("name", g.getName());
            map.put("colorTheme", g.getColorTheme());
            map.put("iconEmoji", g.getIconEmoji());
            map.put("createdAt", g.getCreatedAt());
            map.put("wordCount", countsMap.getOrDefault(g.getId(), 0L));
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

    // ============================================================
    // POST /api/vocab-groups  –  Tạo nhóm mới
    // ============================================================
    /**
     * Tạo một nhóm từ vựng mới cho user.
     * Body: { "name": "...", "colorTheme": "...", "iconEmoji": "..." }
     */
    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody Map<String, String> body, HttpSession session) {
        String name = body.get("name");
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tên nhóm không được để trống"));
        }

        Long userId = getUserId(session);
        VocabGroup group = new VocabGroup();
        group.setUserId(userId);
        group.setName(name.trim());
        group.setColorTheme(body.getOrDefault("colorTheme", "emerald"));
        group.setIconEmoji(body.getOrDefault("iconEmoji", "📚"));

        VocabGroup saved = vocabGroupRepository.save(group);

        // Trả về object đầy đủ kèm wordCount = 0
        Map<String, Object> response = new HashMap<>();
        response.put("id", saved.getId());
        response.put("name", saved.getName());
        response.put("colorTheme", saved.getColorTheme());
        response.put("iconEmoji", saved.getIconEmoji());
        response.put("createdAt", saved.getCreatedAt());
        response.put("wordCount", 0);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============================================================
    // DELETE /api/vocab-groups/{id}  –  Xóa nhóm và toàn bộ từ trong nhóm
    // ============================================================
    /**
     * Xóa một nhóm từ vựng (và tất cả từ thuộc nhóm đó).
     * Chỉ cho phép xóa nhóm của chính user đang đăng nhập.
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteGroup(@PathVariable Long id, HttpSession session) {
        Long userId = getUserId(session);
        Optional<VocabGroup> groupOpt = vocabGroupRepository.findById(id);

        if (groupOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        VocabGroup group = groupOpt.get();
        if (!group.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Bạn không có quyền xóa nhóm này"));
        }

        // Xóa tất cả từ trong nhóm trước, rồi xóa nhóm
        vocabGroupItemRepository.deleteByGroupId(id);
        vocabGroupRepository.deleteById(id);

        return ResponseEntity.ok(Map.of("message", "Đã xóa nhóm thành công"));
    }

    // ============================================================
    // GET /api/vocab-groups/{id}/items  –  Lấy tất cả từ trong nhóm
    // ============================================================
    /**
     * Trả về danh sách từ vựng thuộc một nhóm cụ thể.
     */
    @GetMapping("/{id}/items")
    public ResponseEntity<?> getGroupItems(@PathVariable Long id, HttpSession session) {
        Long userId = getUserId(session);
        Optional<VocabGroup> groupOpt = vocabGroupRepository.findById(id);

        if (groupOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        VocabGroup group = groupOpt.get();
        if (!group.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<VocabGroupItem> items = vocabGroupItemRepository.findByGroupIdOrderByAddedAtDesc(id);
        return ResponseEntity.ok(items);
    }

    // ============================================================
    // POST /api/vocab-groups/{id}/items  –  Thêm từ vào nhóm
    // ============================================================
    /**
     * Thêm một từ mới vào nhóm.
     * Body: { "koreanWord": "...", "romaji": "...", "englishMeaning": "...", "wordType": "..." }
     */
    @PostMapping("/{id}/items")
    public ResponseEntity<?> addWordToGroup(@PathVariable Long id,
                                             @RequestBody Map<String, String> body,
                                             HttpSession session) {
        Long userId = getUserId(session);
        Optional<VocabGroup> groupOpt = vocabGroupRepository.findById(id);

        if (groupOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        VocabGroup group = groupOpt.get();
        if (!group.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Bạn không có quyền chỉnh sửa nhóm này"));
        }

        String koreanWord = body.get("koreanWord");
        String englishMeaning = body.get("englishMeaning");
        if (koreanWord == null || koreanWord.isBlank() || englishMeaning == null || englishMeaning.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Từ tiếng Hàn và nghĩa tiếng Anh không được để trống"));
        }

        VocabGroupItem item = new VocabGroupItem();
        item.setGroupId(id);
        item.setKoreanWord(koreanWord.trim());
        item.setRomaji(body.getOrDefault("romaji", ""));
        item.setEnglishMeaning(englishMeaning.trim());
        item.setWordType(body.getOrDefault("wordType", "OTHER"));

        VocabGroupItem saved = vocabGroupItemRepository.save(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ============================================================
    // DELETE /api/vocab-groups/{id}/items/{itemId}  –  Xóa từ khỏi nhóm
    // ============================================================
    /**
     * Xóa một từ vựng khỏi nhóm.
     */
    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<?> removeWordFromGroup(@PathVariable Long id,
                                                  @PathVariable Long itemId,
                                                  HttpSession session) {
        Long userId = getUserId(session);
        Optional<VocabGroup> groupOpt = vocabGroupRepository.findById(id);

        if (groupOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        VocabGroup group = groupOpt.get();
        if (!group.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<VocabGroupItem> itemOpt = vocabGroupItemRepository.findById(itemId);
        if (itemOpt.isEmpty() || !itemOpt.get().getGroupId().equals(id)) {
            return ResponseEntity.notFound().build();
        }

        vocabGroupItemRepository.deleteById(itemId);
        return ResponseEntity.ok(Map.of("message", "Đã xóa từ thành công"));
    }
}
