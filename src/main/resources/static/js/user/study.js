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
    if (progressLabel) progressLabel.textContent = `${currentNum} of ${totalNum} words`;
    if (scoreLabel) scoreLabel.textContent = `Learned ${percentage}%`;

    // Cập nhật trạng thái các nút điều hướng
    prevBtn.disabled = currentIndex === 0;
    
    // Nếu là từ cuối cùng, đổi nút "Tiếp theo" thành "Bắt đầu Quiz"
    if (currentIndex === vocabList.length - 1) {
        nextBtnText.textContent = "Start Quiz";
        nextBtnIcon.textContent = "quiz";
        nextBtn.classList.replace('bg-primary', 'bg-secondary');
    } else {
        nextBtnText.textContent = "Next Word";
        nextBtnIcon.textContent = "arrow_forward";
        nextBtn.classList.replace('bg-secondary', 'bg-primary');
    }
}

/**
 * Sự kiện lật thẻ khi nhấn vào container
 */
container.addEventListener('click', () => {
    container.classList.toggle('is-flipped');
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
 * Lưu tiến độ học tập về server
 */
async function saveProgress(vocabIds, type) {
    try {
        const response = await fetch('/api/progress/save', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                vocabIds: vocabIds,
                type: type
            })
        });
        const result = await response.json();
        console.log('Progress saved:', result);
    } catch (error) {
        console.error('Error saving progress:', error);
    }
}

/**
 * Sự kiện chuyển sang từ tiếp theo hoặc bắt đầu Quiz
 */
nextBtn.addEventListener('click', async () => {
    if (currentIndex < vocabList.length - 1) {
        currentIndex++;
        updateCard();
    } else {
        // Lưu tiến độ flashcard trước khi chuyển sang Quiz
        const ids = vocabList.map(v => v.id);
        await saveProgress(ids, 'flashcard');
        
        // Chuyển hướng sang trang Game với danh sách ID các từ vừa học
        window.location.href = `/game?vocabIds=${ids.join(',')}`;
    }
});

// Khởi tạo nội dung lần đầu khi trang tải xong
document.addEventListener('DOMContentLoaded', () => {
    updateCard();
});

// --- My Vocabulary Save Feature ---
let userGroups = null;

function openSaveModal() {
    const modal = document.getElementById('modal-save-vocab');
    modal.classList.remove('hidden');
    modal.classList.add('flex');
    
    if (userGroups === null) {
        fetchGroupsForSave();
    } else {
        renderGroupsForSave();
    }
}

function closeSaveModal() {
    const modal = document.getElementById('modal-save-vocab');
    modal.classList.add('hidden');
    modal.classList.remove('flex');
    
    // Reset create group form
    const form = document.getElementById('create-group-form');
    const btn = document.getElementById('btn-show-create-group');
    if (form && btn) {
        form.classList.add('hidden');
        form.classList.remove('flex');
        btn.classList.remove('hidden');
        document.getElementById('new-group-name').value = '';
    }
}

function fetchGroupsForSave() {
    const list = document.getElementById('save-groups-list');
    list.innerHTML = '<p class="text-center text-on-surface-variant text-sm py-4">Loading groups...</p>';
    
    fetch('/api/vocab-groups')
        .then(response => {
            if (!response.ok) throw new Error('Failed to fetch groups');
            return response.json();
        })
        .then(data => {
            userGroups = data;
            renderGroupsForSave();
        })
        .catch(err => {
            console.error(err);
            list.innerHTML = '<p class="text-center text-error text-sm py-4">Error loading groups.</p>';
        });
}

function renderGroupsForSave() {
    const list = document.getElementById('save-groups-list');
    if (!userGroups || userGroups.length === 0) {
        list.innerHTML = `
            <div class="text-center py-4">
                <p class="text-sm text-on-surface-variant mb-3">No groups found.</p>
                <a href="/vocabulary" class="inline-block text-sm font-semibold text-primary hover:underline">Create a group in My Vocabulary</a>
            </div>
        `;
        return;
    }
    
    list.innerHTML = userGroups.map(g => {
        return `
            <div onclick="saveWordToGroup(${g.id})" class="group cursor-pointer flex items-center justify-between p-3 rounded-xl border border-outline-variant/20 hover:bg-surface-container-low transition-colors">
                <div class="flex items-center gap-3">
                    <span class="text-2xl">${g.iconEmoji || '📚'}</span>
                    <div>
                        <h4 class="font-bold text-sm text-on-surface">${g.name}</h4>
                        <p class="text-xs text-on-surface-variant">${g.wordCount || 0} words</p>
                    </div>
                </div>
                <span class="material-symbols-outlined text-primary opacity-0 group-hover:opacity-100 transition-opacity">add_circle</span>
            </div>
        `;
    }).join('');
}

function saveWordToGroup(groupId) {
    if (vocabList.length === 0) return;
    const word = vocabList[currentIndex];
    
    fetch('/api/vocab-groups/' + groupId + '/items', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            koreanWord: word.koreanWord,
            romaji: word.romaji,
            englishMeaning: word.englishMeaning,
            wordType: word.wordType || 'OTHER'
        })
    })
    .then(response => {
        if (!response.ok) throw new Error('Failed to save word');
        return response.json();
    })
    .then(data => {
        // Update local count
        const g = userGroups.find(x => x.id === groupId);
        if (g) g.wordCount = (g.wordCount || 0) + 1;
        
        closeSaveModal();
        showToastNotification();
    })
    .catch(err => {
        console.error(err);
        alert('Failed to save word to group.');
    });
}

function showToastNotification() {
    const toast = document.getElementById('toast-notification');
    if (!toast) return;
    
    toast.classList.remove('opacity-0', 'translate-y-4', 'pointer-events-none');
    toast.classList.add('opacity-100', 'translate-y-0');
    
    setTimeout(() => {
        toast.classList.add('opacity-0', 'translate-y-4', 'pointer-events-none');
        toast.classList.remove('opacity-100', 'translate-y-0');
    }, 2500);
}

function toggleCreateGroupForm() {
    const form = document.getElementById('create-group-form');
    const btn = document.getElementById('btn-show-create-group');
    
    if (form.classList.contains('hidden')) {
        form.classList.remove('hidden');
        form.classList.add('flex');
        btn.classList.add('hidden');
        document.getElementById('new-group-name').focus();
    } else {
        form.classList.add('hidden');
        form.classList.remove('flex');
        btn.classList.remove('hidden');
    }
}

function createNewGroupAndSave() {
    const nameInput = document.getElementById('new-group-name');
    const name = nameInput.value.trim();
    
    if (!name) {
        alert('Please enter a group name');
        nameInput.focus();
        return;
    }
    
    // Default values for quick creation
    const colorTheme = 'emerald';
    const iconEmoji = '📚';
    
    fetch('/api/vocab-groups', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            name: name,
            colorTheme: colorTheme,
            iconEmoji: iconEmoji
        })
    })
    .then(response => {
        if (!response.ok) throw new Error('Failed to create group');
        return response.json();
    })
    .then(newGroup => {
        // Add to local list and render
        if (!userGroups) userGroups = [];
        userGroups.unshift(newGroup);
        renderGroupsForSave();
        
        // Save the current word to this new group
        saveWordToGroup(newGroup.id);
    })
    .catch(err => {
        console.error(err);
        alert('Failed to create new group.');
    });
}
