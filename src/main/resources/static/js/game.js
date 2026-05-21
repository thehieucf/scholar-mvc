/**
 * game.js — Handles logic for the Korean vocabulary quiz game
 */

let questions = [];

if (typeof serverQuestions !== 'undefined' && serverQuestions.length > 0) {
    questions = serverQuestions.map(q => {
        const options = [q.correctAnswer, q.wrongAnswer1, q.wrongAnswer2, q.wrongAnswer3];
        // Shuffle options
        for (let i = options.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [options[i], options[j]] = [options[j], options[i]];
        }
        return {
            kr: q.koreanText,
            rom: q.romaji,
            type: q.questionType,
            topic: q.topic,
            options: options,
            correct: options.indexOf(q.correctAnswer),
            vocabId: q.vocabularyId   // dùng để gọi API lưu kết quả
        };
    });
} else {
    // Fallback to static data if no server questions (for testing)
    questions = [
        {
            kr: "저는 학교에 갑니다.", rom: "Jeoneun hakgyoe gamnida.",
            type: "Translate This Phrase", topic: "Sentence Patterns · 문장",
            options: ["I am going to school.", "I study at school.", "I like the school.", "I go back home."],
            correct: 0,
            vocabId: null
        },
    ];
}

// Game state variables
let current = 0,         // Current question index
    correctCount = 0,    // Number of correct answers
    wrongCount = 0,      // Number of wrong answers
    xpEarned = 0,        // XP earned
    answered = false;    // Whether current question has been answered

/**
 * Gọi API lưu kết quả một câu trả lời quiz.
 * Không block UI — fire-and-forget, chỉ log lỗi nếu có.
 */
function saveQuizResult(vocabId, correct) {
    if (!vocabId) return;  // câu hỏi không liên kết từ vựng → bỏ qua
    const params = new URLSearchParams({ vocabId, correct });
    fetch('/api/game/result', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString()
    })
    .then(res => res.json())
    .then(data => {
        if (data.error) {
            console.warn('[Quiz] API error:', data.error);
            return;
        }
        // Cập nhật XP hiển thị nếu có element
        const statXp = document.getElementById('stat-xp');
        if (statXp && data.xpGained > 0) {
            const current = parseInt(statXp.textContent.replace('+', '')) || 0;
            statXp.textContent = '+' + (current + data.xpGained);
        }
        // Hiển thị badge mới nếu có
        if (data.newBadges && data.newBadges.length > 0) {
            data.newBadges.forEach(b => {
                showBadgeToast(b.emoji, b.name, b.description);
            });
        }
    })
    .catch(err => console.warn('[Quiz] Failed to save result:', err));
}

/**
 * Hiển thị toast thông báo badge mới.
 */
function showBadgeToast(emoji, name, description) {
    const toast = document.createElement('div');
    toast.style.cssText = `
        position:fixed; bottom:1.5rem; right:1.5rem; z-index:9999;
        background:#1e293b; color:#fff; border-radius:12px;
        padding:0.75rem 1.25rem; display:flex; align-items:center; gap:0.75rem;
        box-shadow:0 8px 32px rgba(0,0,0,0.3); font-family:inherit;
        animation: slideIn 0.3s ease;
    `;
    toast.innerHTML = `<span style="font-size:1.5rem">${emoji}</span>
        <div><div style="font-weight:700;font-size:0.9rem">Badge Unlocked: ${name}</div>
        <div style="font-size:0.78rem;opacity:0.75">${description}</div></div>`;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 4000);
}

/**
 * Render the current question on screen
 */
function renderQuestion() {
    const q = questions[current];
    const qKr = document.getElementById('question-kr');
    const qRom = document.getElementById('question-rom');
    const qType = document.getElementById('q-type');
    const topicBadge = document.getElementById('topic-badge');
    const progressBar = document.getElementById('progress-bar');
    const progressLabel = document.getElementById('progress-label');

    if (qKr) qKr.textContent = q.kr;
    if (qRom) qRom.textContent = q.rom;
    if (qType) qType.textContent = q.type;
    if (topicBadge) topicBadge.textContent = q.topic;
    if (progressBar) progressBar.style.width = ((current / questions.length) * 100) + '%';
    if (progressLabel) progressLabel.textContent = `${current + 1} of ${questions.length} questions`;
    answered = false;

    const grid = document.getElementById('options-grid');
    if (grid) {
        grid.innerHTML = '';
        q.options.forEach((opt, i) => {
            const btn = document.createElement('button');
            btn.className = 'option-btn';
            btn.innerHTML = `<span>${opt}</span><span class="material-symbols-outlined opt-icon" style="opacity:0;font-size:1.25rem;">${i === q.correct ? 'check_circle' : 'cancel'}</span>`;
            btn.onclick = () => selectAnswer(i, q.correct, btn);
            grid.appendChild(btn);
        });
    }
}

/**
 * Handle answer selection
 */
function selectAnswer(idx, correctIdx, clickedBtn) {
    if (answered) return;
    answered = true;

    const btns = document.querySelectorAll('.option-btn');
    btns.forEach(b => { b.disabled = true; });

    const isCorrect = idx === correctIdx;
    const q = questions[current];

    // Lưu kết quả lên server (async, không block UI)
    saveQuizResult(q.vocabId, isCorrect);

    if (isCorrect) {
        clickedBtn.classList.add('correct');
        correctCount++;
        xpEarned += 20;
        const statCorrect = document.getElementById('stat-correct');
        const statXp = document.getElementById('stat-xp');
        if (statCorrect) statCorrect.textContent = correctCount;
        if (statXp) statXp.textContent = '+' + xpEarned;
    } else {
        clickedBtn.classList.add('wrong');
        btns[correctIdx].classList.add('correct');
        wrongCount++;
        const statWrong = document.getElementById('stat-wrong');
        if (statWrong) statWrong.textContent = wrongCount;
    }

    const scoreLabel = document.getElementById('score-label');
    if (scoreLabel) scoreLabel.textContent = `Score: ${correctCount}/${current + 1}`;

    setTimeout(() => {
        current++;
        if (current < questions.length) {
            renderQuestion();
        } else {
            showResult();
        }
    }, 1400);
}

/**
 * Show the final results screen
 */
function showResult() {
    const main = document.querySelector('main');
    if (main) {
        main.innerHTML = `
            <div class="text-center max-w-lg mx-auto px-4">
                <div class="text-7xl mb-6">${correctCount >= questions.length * 0.8 ? '🎉' : correctCount >= questions.length * 0.5 ? '💪' : '📖'}</div>
                <h2 class="text-4xl font-extrabold font-headline text-on-surface mb-2">Quiz Complete!</h2>
                <p class="text-on-surface-variant mb-8 text-lg">${correctCount} out of ${questions.length} correct · +${xpEarned} XP earned</p>
                <div class="grid grid-cols-3 gap-4 mb-10">
                    <div class="bg-surface-container-lowest rounded-xl p-5 shadow-sm border border-outline-variant/15">
                        <p class="text-3xl font-black text-primary">${correctCount}</p>
                        <p class="text-xs uppercase font-bold text-on-surface-variant mt-1">Correct</p>
                    </div>
                    <div class="bg-surface-container-lowest rounded-xl p-5 shadow-sm border border-outline-variant/15">
                        <p class="text-3xl font-black text-error">${wrongCount}</p>
                        <p class="text-xs uppercase font-bold text-on-surface-variant mt-1">Wrong</p>
                    </div>
                    <div class="bg-surface-container-lowest rounded-xl p-5 shadow-sm border border-outline-variant/15">
                        <p class="text-3xl font-black text-tertiary">+${xpEarned}</p>
                        <p class="text-xs uppercase font-bold text-on-surface-variant mt-1">XP</p>
                    </div>
                </div>
                <div class="flex flex-col sm:flex-row gap-4 justify-center">
                    <button id="restart-btn" class="bg-primary text-on-primary font-bold py-4 px-10 rounded-full hover:opacity-90 transition-all active:scale-95 flex items-center justify-center gap-2">
                        <span class="material-symbols-outlined">refresh</span>Play Again
                    </button>
                    <a href="/" class="bg-surface-container-low text-on-surface font-bold py-4 px-10 rounded-full hover:bg-surface-container-high transition-colors flex items-center justify-center gap-2 border border-outline-variant">
                        <span class="material-symbols-outlined">dashboard</span>Dashboard
                    </a>
                </div>
            </div>`;

        const restartBtn = document.getElementById('restart-btn');
        if (restartBtn) restartBtn.onclick = restartQuiz;
    }
}

function restartQuiz() {
    location.reload();
}

/**
 * Play audio for the current question
 */
function playAudio() {
    const q = questions[current];
    if ('speechSynthesis' in window) {
        const utter = new SpeechSynthesisUtterance(q.kr);
        utter.lang = 'ko-KR';
        speechSynthesis.speak(utter);
    }
}

// Initial render
document.addEventListener('DOMContentLoaded', () => {
    renderQuestion();
    const playBtn = document.querySelector('button[onclick="playAudio()"]');
    if (playBtn) {
        playBtn.removeAttribute('onclick');
        playBtn.onclick = playAudio;
    }
});
