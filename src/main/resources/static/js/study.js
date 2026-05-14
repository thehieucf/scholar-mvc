/**
 * study.js — Xử lý logic cho phiên học Flashcard
 */

let currentIndex = 0; // Vị trí từ vựng hiện tại trong danh sách

// Lấy các phần tử DOM cần thiết
const container = document.getElementById('flashcard-container');
const cardKorean = document.getElementById('card-korean');
const cardRomaji = document.getElementById('card-romaji');
const cardEnglish = document.getElementById('card-english');
const cardMnemonic = document.getElementById('card-mnemonic');
const mnemonicContainer = document.getElementById('mnemonic-container');
const currentCount = document.getElementById('current-count');
const totalCount = document.getElementById('total-count');
const progressBar = document.getElementById('progress-bar');
const progressLabel = document.getElementById('progress-label');
const scoreLabel = document.getElementById('score-label');

const prevBtn = document.getElementById('prev-btn');
const nextBtn = document.getElementById('next-btn');
const nextBtnText = document.getElementById('next-btn-text');
const nextBtnIcon = document.getElementById('next-btn-icon');

/**
 * Cập nhật nội dung hiển thị trên thẻ Flashcard
 */
function updateCard() {
    if (vocabList.length === 0) return;

    const word = vocabList[currentIndex];
    
    // Reset lại trạng thái lật thẻ (luôn hiển thị mặt trước khi chuyển từ)
    container.classList.remove('is-flipped');

    // Cập nhật nội dung văn bản
    cardKorean.textContent = word.koreanWord;
    cardRomaji.textContent = word.romaji;
    cardEnglish.textContent = word.englishMeaning;
    
    // Hiển thị gợi ý ghi nhớ nếu có
    if (word.mnemonic) {
        cardMnemonic.textContent = word.mnemonic;
        mnemonicContainer.classList.remove('hidden');
    } else {
        mnemonicContainer.classList.add('hidden');
    }

    // Cập nhật thông tin tiến độ (Số từ, thanh progress)
    const currentNum = currentIndex + 1;
    const totalNum = vocabList.length;
    const percentage = Math.round((currentNum / totalNum) * 100);

    currentCount.textContent = currentNum;
    if (totalCount) totalCount.textContent = totalNum;
    
    if (progressBar) progressBar.style.width = `${percentage}%`;
    if (progressLabel) progressLabel.textContent = `${currentNum} trên ${totalNum} từ`;
    if (scoreLabel) scoreLabel.textContent = `Đã học ${percentage}%`;

    // Cập nhật trạng thái các nút điều hướng
    prevBtn.disabled = currentIndex === 0;
    
    // Nếu là từ cuối cùng, đổi nút "Tiếp theo" thành "Bắt đầu Quiz"
    if (currentIndex === vocabList.length - 1) {
        nextBtnText.textContent = "Bắt đầu Quiz";
        nextBtnIcon.textContent = "quiz";
        nextBtn.classList.replace('bg-primary', 'bg-secondary');
    } else {
        nextBtnText.textContent = "Từ tiếp theo";
        nextBtnIcon.textContent = "arrow_forward";
        nextBtn.classList.replace('bg-secondary', 'bg-primary');
    }
}

/**
 * Ghi nhận tiến độ học từ hiện tại lên server.
 * Được gọi khi user lật thẻ (xem nghĩa) — tức là đã "học" từ đó.
 */
function recordProgress(vocabId) {
    if (!vocabId) return;
    fetch('/api/study/progress?vocabId=' + vocabId, { method: 'POST' })
        .catch(function() { /* silent fail — không block UX */ });
}

/**
 * Sự kiện lật thẻ khi nhấn vào container
 */
container.addEventListener('click', () => {
    const wasFlipped = container.classList.contains('is-flipped');
    container.classList.toggle('is-flipped');
    // Ghi nhận tiến độ khi user lật thẻ lần đầu (xem mặt sau)
    if (!wasFlipped && vocabList.length > 0) {
        recordProgress(vocabList[currentIndex].id);
    }
});

/**
 * Sự kiện quay lại từ trước đó
 */
prevBtn.addEventListener('click', () => {
    if (currentIndex > 0) {
        currentIndex--;
        updateCard();
    }
});

/**
 * Sự kiện chuyển sang từ tiếp theo hoặc bắt đầu Quiz
 */
nextBtn.addEventListener('click', () => {
    if (currentIndex < vocabList.length - 1) {
        currentIndex++;
        updateCard();
    } else {
        // Chuyển hướng sang trang Game với danh sách ID các từ vừa học
        const ids = vocabList.map(v => v.id).join(',');
        window.location.href = `/game?vocabIds=${ids}`;
    }
});

// Khởi tạo nội dung lần đầu khi trang tải xong
document.addEventListener('DOMContentLoaded', () => {
    updateCard();
});
