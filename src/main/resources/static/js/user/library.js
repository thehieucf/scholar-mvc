/**
 * library.js — Xử lý tương tác cho Modal thư viện từ vựng
 */

/**
 * Bảng màu cho icon container trong modal.
 * Key là tên colorTheme từ database, value là { bg, text } dạng hex.
 */
const COLOR_MAP = {
    red:     { bg: '#fee2e2', text: '#dc2626' },
    orange:  { bg: '#ffedd5', text: '#ea580c' },
    amber:   { bg: '#fef3c7', text: '#d97706' },
    yellow:  { bg: '#fef9c3', text: '#ca8a04' },
    lime:    { bg: '#ecfccb', text: '#65a30d' },
    green:   { bg: '#dcfce7', text: '#16a34a' },
    emerald: { bg: '#d1fae5', text: '#059669' },
    teal:    { bg: '#ccfbf1', text: '#0d9488' },
    cyan:    { bg: '#cffafe', text: '#0891b2' },
    sky:     { bg: '#e0f2fe', text: '#0284c7' },
    blue:    { bg: '#dbeafe', text: '#2563eb' },
    indigo:  { bg: '#e0e7ff', text: '#4f46e5' },
    violet:  { bg: '#ede9fe', text: '#7c3aed' },
    purple:  { bg: '#f3e8ff', text: '#9333ea' },
    fuchsia: { bg: '#fae8ff', text: '#c026d3' },
    pink:    { bg: '#fce7f3', text: '#db2777' },
    rose:    { bg: '#ffe4e6', text: '#e11d48' },
};

// Lấy các phần tử DOM cần thiết
const modal = document.getElementById('word-modal');
const modalTitle = document.getElementById('modal-title');
const modalIcon = document.getElementById('modal-icon');
const modalIconContainer = document.getElementById('modal-icon-container');
const wordListContainer = document.getElementById('modal-word-list');
const practiceBtn = document.getElementById('modal-practice-btn');

/**
 * Mở modal hiển thị danh sách từ vựng của một danh mục.
 * @param {string} categoryId ID của danh mục
 * @param {string} title Tên danh mục (tiếng Anh)
 * @param {string} icon Tên icon Material Symbols
 * @param {string} colorTheme Tên màu chủ đề (VD: emerald, blue)
 */
function openModal(categoryId, title, icon, colorTheme) {
    console.log('Đang mở modal cho danh mục:', categoryId, title);
    
    // Cập nhật thông tin Header của Modal
    modalTitle.textContent = title;
    modalIcon.textContent = icon;
    modalIconContainer.className = `w-10 h-10 rounded-lg flex items-center justify-center bg-${colorTheme}-100 text-${colorTheme}-600`;

    // Cập nhật đường dẫn cho nút "Practice" (Luyện tập)
    if (practiceBtn) {
        practiceBtn.href = `/study?id=${categoryId}`;
    }

    // Hiển thị trạng thái đang tải (Loading)
    wordListContainer.innerHTML = `
        <div class="flex flex-col items-center justify-center p-12 text-on-surface-variant">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mb-4"></div>
            <p class="text-xs font-medium uppercase tracking-widest">Loading vocabulary...</p>
        </div>`;
    
    // Gọi API để lấy danh sách từ vựng thuộc danh mục này
    fetch(`/api/categories/${categoryId}/vocabularies`)
        .then(response => response.json())
        .then(words => {
            wordListContainer.innerHTML = '';
            
            // Kiểm tra nếu danh mục trống
            if (!words || words.length === 0) {
                wordListContainer.innerHTML = `
                    <div class="text-center p-12 bg-surface-container-low rounded-2xl border border-dashed border-outline-variant/30">
                        <span class="material-symbols-outlined text-4xl text-outline mb-3">sentiment_dissatisfied</span>
                        <p class="text-on-surface-variant font-medium">No vocabulary in this category yet.</p>
                    </div>`;
                return;
            }

            // Tạo các thẻ từ vựng (Word Cards) và thêm vào danh sách
            words.forEach(word => {
                const wordCard = document.createElement('div');
                wordCard.className = 'bg-surface-container-lowest rounded-xl p-4 flex items-center justify-between border border-outline-variant/15 hover:border-primary/30 transition-colors group shadow-sm';
                wordCard.innerHTML = `
                    <div class="flex items-center gap-5">
                        <div class="text-2xl font-bold font-headline text-on-surface min-w-[100px] group-hover:text-primary transition-colors">${word.koreanWord}</div>
                        <div>
                            <div class="text-sm font-bold text-on-surface">${word.englishMeaning}</div>
                            <div class="text-xs text-on-surface-variant font-medium">${word.romaji}</div>
                        </div>
                    </div>
                    <button onclick="playWord('${word.koreanWord}')" class="w-8 h-8 rounded-full bg-surface-container-low flex items-center justify-center text-on-surface-variant group-hover:bg-primary-container group-hover:text-primary transition-colors flex-shrink-0">
                        <span class="material-symbols-outlined text-sm">volume_up</span>
                    </button>
                `;
                wordListContainer.appendChild(wordCard);
            });
        })
        .catch(error => {
            console.error('Lỗi khi lấy từ vựng:', error);
            wordListContainer.innerHTML = '<div class="text-center p-8 text-error">Unable to load vocabulary list.</div>';
        });

    // Hiển thị Modal với hiệu ứng chuyển động
    modal.classList.remove('hidden');
    setTimeout(() => {
        modal.classList.add('show');
    }, 10);

    // Ngăn cuộn trang web khi modal đang mở
    document.body.style.overflow = 'hidden';
}

/**
 * Phát âm từ tiếng Hàn sử dụng Web Speech API.
 * @param {string} text Từ cần phát âm
 */
function playWord(text) {
    if ('speechSynthesis' in window) {
        const utter = new SpeechSynthesisUtterance(text);
        utter.lang = 'ko-KR'; // Đặt ngôn ngữ là tiếng Hàn
        speechSynthesis.speak(utter);
    }
}

/**
 * Đóng modal.
 */
function closeModal() {
    modal.classList.remove('show');
    // Đợi hiệu ứng chuyển động kết thúc rồi mới ẩn hoàn toàn
    setTimeout(() => {
        modal.classList.add('hidden');
    }, 300); // Khớp với thời gian transition 0.3s trong CSS

    // Cho phép cuộn trang web lại bình thường
    document.body.style.overflow = '';
}
