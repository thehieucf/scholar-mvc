/**
 * study.js — Handles logic for the Flashcard study session
 */

let currentIndex = 0; // Current vocabulary index in the list

// Get required DOM elements
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
 * Update the content displayed on the Flashcard
 */
function updateCard() {
    if (vocabList.length === 0) return;

    const word = vocabList[currentIndex];
    
    // Reset flip state (always show front face when switching words)
    container.classList.remove('is-flipped');

    // Update text content
    cardKorean.textContent = word.koreanWord;
    cardRomaji.textContent = word.romaji;
    cardEnglish.textContent = word.englishMeaning;
    
    // Show mnemonic hint if available
    if (word.mnemonic) {
        cardMnemonic.textContent = word.mnemonic;
        mnemonicContainer.classList.remove('hidden');
    } else {
        mnemonicContainer.classList.add('hidden');
    }

    // Update progress info (word count, progress bar)
    const currentNum = currentIndex + 1;
    const totalNum = vocabList.length;
    const percentage = Math.round((currentNum / totalNum) * 100);

    currentCount.textContent = currentNum;
    if (totalCount) totalCount.textContent = totalNum;
    
    if (progressBar) progressBar.style.width = `${percentage}%`;
    if (progressLabel) progressLabel.textContent = `${currentNum} of ${totalNum} words`;
    if (scoreLabel) scoreLabel.textContent = `${percentage}% studied`;

    // Update navigation button states
    prevBtn.disabled = currentIndex === 0;
    
    // If last word, change "Next" button to "Start Quiz"
    if (currentIndex === vocabList.length - 1) {
        nextBtnText.textContent = "Start Quiz";
        nextBtnIcon.textContent = "quiz";
        nextBtn.classList.replace('bg-primary', 'bg-secondary');
    } else {
        nextBtnText.textContent = "Next word";
        nextBtnIcon.textContent = "arrow_forward";
        nextBtn.classList.replace('bg-secondary', 'bg-primary');
    }
}

/**
 * Record study progress for the current word on the server.
 * Called when the user flips the card (views the meaning).
 */
function recordProgress(vocabId) {
    if (!vocabId) return;
    fetch('/api/study/progress?vocabId=' + vocabId, { method: 'POST' })
        .catch(function() { /* silent fail — does not block UX */ });
}

/**
 * Flip card event when clicking the container
 */
container.addEventListener('click', () => {
    const wasFlipped = container.classList.contains('is-flipped');
    container.classList.toggle('is-flipped');
    // Record progress when user flips the card for the first time (views back face)
    if (!wasFlipped && vocabList.length > 0) {
        recordProgress(vocabList[currentIndex].id);
    }
});

/**
 * Go to previous word
 */
prevBtn.addEventListener('click', () => {
    if (currentIndex > 0) {
        currentIndex--;
        updateCard();
    }
});

/**
 * Go to next word or start Quiz
 */
nextBtn.addEventListener('click', () => {
    if (currentIndex < vocabList.length - 1) {
        currentIndex++;
        updateCard();
    } else {
        // Redirect to Game page with the list of studied word IDs
        const ids = vocabList.map(v => v.id).join(',');
        window.location.href = `/game?vocabIds=${ids}`;
    }
});

// Initialize content on first page load
document.addEventListener('DOMContentLoaded', () => {
    updateCard();
});
