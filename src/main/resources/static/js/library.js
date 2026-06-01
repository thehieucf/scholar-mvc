/**
 * library.js — Xử lý tương tác cho trang thư viện từ vựng (Library)
 *
 * Chức năng chính:
 *   - openModal(categoryId, ...): Gọi API lấy danh sách từ vựng theo category,
 *     hiển thị trong modal overlay.
 *   - closeModal(): Ẩn modal và khôi phục scroll.
 *   - playWord(text): Phát âm từ tiếng Hàn bằng Web Speech API.
 *   - getMeaning(word): Trả về nghĩa theo ngôn ngữ hiện tại (en/vi).
 */

/**
 * Trả về nghĩa hiển thị theo ngôn ngữ hiện tại.
 * currentLang được inject từ template trước khi script này chạy.
 */
function getMeaning(word) {
    var lang = (typeof currentLang !== 'undefined') ? currentLang : 'en';
    if (lang === 'vi' && word.vietnameseMeaning) {
        return word.vietnameseMeaning;
    }
    return word.englishMeaning;
}

// Get required DOM elements
const modal = document.getElementById('word-modal');
const modalTitle = document.getElementById('modal-title');
const modalIcon = document.getElementById('modal-icon');
const modalIconContainer = document.getElementById('modal-icon-container');
const wordListContainer = document.getElementById('modal-word-list');
const practiceBtn = document.getElementById('modal-practice-btn');

/**
 * Open the modal showing vocabulary words for a category.
 * @param {string} categoryId Category ID
 * @param {string} title Category name (English)
 * @param {string} icon Material Symbols icon name
 * @param {string} colorTheme Color theme name (e.g. emerald, blue)
 */
function openModal(categoryId, title, icon, colorTheme) {
    // Update modal header
    modalTitle.textContent = title;
    modalIcon.textContent = icon;
    modalIconContainer.className = `w-10 h-10 rounded-lg flex items-center justify-center bg-${colorTheme}-container text-on-${colorTheme}-container`;

    // Update the Practice button link
    if (practiceBtn) {
        practiceBtn.href = `/study?id=${categoryId}`;
    }

    // Show loading state
    wordListContainer.innerHTML = `
        <div class="flex flex-col items-center justify-center p-12 text-on-surface-variant">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mb-4"></div>
            <p class="text-xs font-medium uppercase tracking-widest">Loading vocabulary...</p>
        </div>`;

    // Fetch vocabulary words for this category
    fetch(`/api/categories/${categoryId}/vocabularies`)
        .then(response => response.json())
        .then(words => {
            wordListContainer.innerHTML = '';

            // Handle empty category
            if (!words || words.length === 0) {
                wordListContainer.innerHTML = `
                    <div class="text-center p-12 bg-surface-container-low rounded-2xl border border-dashed border-outline-variant/30">
                        <span class="material-symbols-outlined text-4xl text-outline mb-3">sentiment_dissatisfied</span>
                        <p class="text-on-surface-variant font-medium">No vocabulary words in this category yet.</p>
                    </div>`;
                return;
            }

            // Build word cards and append to list
            words.forEach(word => {
                const wordCard = document.createElement('div');
                wordCard.className = 'bg-surface-container-lowest rounded-xl p-4 flex items-center justify-between border border-outline-variant/15 hover:border-primary/30 transition-colors group shadow-sm';
                wordCard.innerHTML = `
                    <div class="flex items-center gap-5">
                        <div class="text-2xl font-bold font-headline text-on-surface min-w-[100px] group-hover:text-primary transition-colors">${word.koreanWord}</div>
                        <div>
                            <div class="text-sm font-bold text-on-surface">${getMeaning(word)}</div>
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
            console.error('Error fetching vocabulary:', error);
            wordListContainer.innerHTML = '<div class="text-center p-8 text-error">Could not load vocabulary list.</div>';
        });

    // Show modal with animation
    modal.classList.remove('hidden');
    setTimeout(() => {
        modal.classList.add('show');
    }, 10);

    // Prevent page scroll while modal is open
    document.body.style.overflow = 'hidden';
}

/**
 * Play pronunciation of a Korean word using the Web Speech API.
 * @param {string} text Word to pronounce
 */
function playWord(text) {
    if ('speechSynthesis' in window) {
        const utter = new SpeechSynthesisUtterance(text);
        utter.lang = 'ko-KR';
        speechSynthesis.speak(utter);
    }
}

/**
 * Close the modal.
 */
function closeModal() {
    modal.classList.remove('show');
    // Wait for transition to finish before hiding completely
    setTimeout(() => {
        modal.classList.add('hidden');
    }, 300); // Matches the 0.3s CSS transition

    // Re-enable page scroll
    document.body.style.overflow = '';
}
