/**
 * game.js — Xử lý logic cho trò chơi trắc nghiệm tiếng Hàn (Quiz)
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
            vocabId: q.vocabularyId // Lưu ID từ vựng để gửi tiến độ
        };
    });
} else {
    // Fallback to static data if no server questions (for testing)
    questions = [
        {
            kr: "저는 학교에 갑니다.", rom: "Jeoneun hakgyoe gamnida.",
            type: "Translate This Phrase", topic: "Sentence Patterns · 문장",
            options: ["I am going to school.", "I study at school.", "I like the school.", "I go back home."],
            correct: 0
        },
        // ... other static questions
    ];
}

// Biến quản lý trạng thái trò chơi
let current = 0,         // Câu hỏi hiện tại
    correctCount = 0,    // Số câu đúng
    wrongCount = 0,      // Số câu sai
    xpEarned = 0,        // XP kiếm được
    answered = false;    // Đã trả lời hay chưa

/**
 * Hiển thị câu hỏi hiện tại lên giao diện
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
 * Xử lý khi người dùng chọn một đáp án
 */
function selectAnswer(idx, correctIdx, clickedBtn) {
    if (answered) return;
    answered = true;

    const btns = document.querySelectorAll('.option-btn');
    btns.forEach(b => { b.disabled = true; });

    if (idx === correctIdx) {
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
 * Lưu tiến độ học tập về server
 */
async function saveProgress(vocabIds, type) {
    if (!vocabIds || vocabIds.length === 0) return;
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
        console.log('Quiz progress saved:', result);
    } catch (error) {
        console.error('Error saving quiz progress:', error);
    }
}

/**
 * Hiển thị màn hình kết quả cuối cùng
 */
async function showResult() {
    // Thu thập danh sách ID từ vựng đã học trong Quiz
    const vocabIds = questions.filter(q => q.vocabId).map(q => q.vocabId);
    if (vocabIds.length > 0) {
        await saveProgress(vocabIds, 'quiz');
    }

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
 * Phát âm nội dung câu hỏi
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
