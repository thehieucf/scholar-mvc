/**
 * study.js — Phiên học 2 pha (Flashcard + Listening)
 *
 * Luồng học:
 *   Phase 1 (Flashcard)  →  Màn hình chuyển tiếp  →  Phase 2 (Listening)  →  Quiz Game
 *
 * Các pha được ẩn/hiện bằng cách toggle class CSS "hidden".
 * Người dùng PHẢI trải qua cả 2 pha theo thứ tự (không có chế độ tự chọn pha).
 *
 * Dữ liệu từ server:
 *   - vocabList: mảng từ vựng được nhúng sẵn từ Thymeleaf vào trang HTML.
 *   - currentLang: ngôn ngữ hiện tại ('en' hoặc 'vi'), cũng nhúng từ Thymeleaf.
 */

// ─── Biến trạng thái ─────────────────────────────────────────────────────────────────────
let flashIndex   = 0;          // chỉ số từ hiện tại trong pha flashcard
let listenIndex  = 0;          // chỉ số từ hiện tại trong pha listening
let listenPlayCount  = 0;      // số lần đã phát âm từ hiện tại
let listenAnswered   = false;  // đã trả lời câu listening hiện tại chưa
let listenCorrect    = 0;      // số câu đúng trong pha listening
let listenWrong      = 0;      // số câu sai trong pha listening
let flashXpEarned    = 0;      // XP tích lũy trong pha flashcard (hiển thị trên màn hình chuyển tiếp)
let lastStreakValue  = 0;      // giá trị streak gần nhất từ API

/**
 * Trả về nghĩa hiển thị theo ngôn ngữ hiện tại.
 * Nếu ngôn ngữ là 'vi' và từ có vietnameseMeaning → dùng tiếng Việt.
 * Ngược lại dùng englishMeaning.
 */
function getMeaning(word) {
    if (typeof currentLang !== 'undefined' && currentLang === 'vi'
            && word.vietnameseMeaning) {
        return word.vietnameseMeaning;
    }
    return word.englishMeaning;
}

// ─── DOM: Pha Flashcard ─────────────────────────────────────────────────────────────
const flashContainer   = document.getElementById('flashcard-container');
const cardKorean       = document.getElementById('card-korean');
const cardRomaji       = document.getElementById('card-romaji');
const cardEnglish      = document.getElementById('card-english');
const cardMnemonic     = document.getElementById('card-mnemonic');
const mnemonicBox      = document.getElementById('mnemonic-container');
const prevBtn          = document.getElementById('prev-btn');
const nextBtn          = document.getElementById('next-btn');
const nextBtnText      = document.getElementById('next-btn-text');
const nextBtnIcon      = document.getElementById('next-btn-icon');
const currentCountEl   = document.getElementById('current-count');
const totalCountEl     = document.getElementById('total-count');

// ─── DOM: Màn hình chuyển tiếp ───────────────────────────────────────────────────────
const transitionWordCount = document.getElementById('transition-word-count');
const transitionTotal     = document.getElementById('transition-total');
const transitionStreak    = document.getElementById('transition-streak');
const transitionXp        = document.getElementById('transition-xp');

// ─── DOM: Pha Listening ─────────────────────────────────────────────────────────────
const listenPlayBtn    = document.getElementById('listen-play-btn');
const listenIcon       = document.getElementById('listen-icon');
const soundRings       = document.getElementById('sound-rings');
const romajiHint       = document.getElementById('romaji-hint');
const listenRomaji     = document.getElementById('listen-romaji');
const playCountEl      = document.getElementById('play-count');
const answerChoices    = document.getElementById('answer-choices');
const listenResult     = document.getElementById('listen-result');
const listenResultIcon = document.getElementById('listen-result-icon');
const listenResultText = document.getElementById('listen-result-text');
const listenResultDetail = document.getElementById('listen-result-detail');
const listenPrevBtn    = document.getElementById('listen-prev-btn');
const listenNextBtn    = document.getElementById('listen-next-btn');
const listenNextText   = document.getElementById('listen-next-text');
const listenNextIcon   = document.getElementById('listen-next-icon');
const listenCurrentEl  = document.getElementById('listen-current-count');
const listenTotalEl    = document.getElementById('listen-total-count');
const listenCorrectEl  = document.getElementById('listen-correct-count');
const listenWrongEl    = document.getElementById('listen-wrong-count');

// ─── DOM: Dùng chung ────────────────────────────────────────────────────────────────
const progressBar    = document.getElementById('progress-bar');
const progressLabel  = document.getElementById('progress-label');
const scoreLabel     = document.getElementById('score-label');
const sessionTitle   = document.getElementById('session-title');

// ─── Các phần (section) của từng pha ───────────────────────────────────────────────────
const phaseFlashcard  = document.getElementById('phase-flashcard');
const phaseTransition = document.getElementById('phase-transition');
const phaseListening  = document.getElementById('phase-listening');

// ─── Phần tử stepper (thanh bước) ─────────────────────────────────────────────────────
const step1 = document.getElementById('step-1');
const step2 = document.getElementById('step-2');
const step3 = document.getElementById('step-3');
const conn1 = document.getElementById('connector-1');
const conn2 = document.getElementById('connector-2');

// ─── Tổng hợp giọng nói (Speech Synthesis) ───────────────────────────────────────────
const synth = window.speechSynthesis;
let koreanVoice = null;

function loadVoices() {
    const voices = synth.getVoices();
    koreanVoice = voices.find(v => v.lang.startsWith('ko')) || null;
}
if (synth.onvoiceschanged !== undefined) synth.onvoiceschanged = loadVoices;
loadVoices();

/**
 * Phát âm một từ tiếng Hàn bằng Web Speech API.
 *
 * @param {Event|null} e             - Sự kiện click (để gọi stopPropagation, tránh lật thẻ)
 * @param {string}     word          - Từ tiếng Hàn cần đọc
 * @param {boolean}    withAnimation - Nếu true, hiển thị animation sóng âm trên nút play
 */
function speak(e, word, withAnimation) {
    if (e) e.stopPropagation();
    if (!word) return;
    synth.cancel();

    const utt = new SpeechSynthesisUtterance(word);
    utt.lang  = 'ko-KR';
    utt.rate  = 0.85;
    utt.pitch = 1.0;
    if (koreanVoice) utt.voice = koreanVoice;

    if (withAnimation) {
        listenPlayBtn.classList.add('speaking');
        soundRings.classList.add('playing');
        listenIcon.textContent = 'graphic_eq';
        utt.onend = utt.onerror = () => {
            listenPlayBtn.classList.remove('speaking');
            soundRings.classList.remove('playing');
            listenIcon.textContent = 'volume_up';
        };
    }
    synth.speak(utt);
}

/** Được gọi khi user nhấn nút loa trên thẻ Flashcard. */
function speakCurrentWord(e) {
    if (vocabList.length === 0) return;
    speak(e, vocabList[flashIndex].koreanWord, false);
}

/**
 * Được gọi khi user bấm nút phát âm lớn trong Listening phase.
 * Sau ≥2 lần bấm, hiện gợi ý Romaji giúp người học đọc đúng chính tả.
 */
function speakListenWord() {
    if (vocabList.length === 0) return;
    listenPlayCount++;
    if (playCountEl) playCountEl.textContent = listenPlayCount;
    if (listenPlayCount >= 2 && romajiHint) romajiHint.classList.remove('hidden');
    speak(null, vocabList[listenIndex].koreanWord, true);
}

// ─── Hàm hỗ trợ chuyển pha ──────────────────────────────────────────────────────────
function showOnly(section) {
    [phaseFlashcard, phaseTransition, phaseListening].forEach(el => {
        if (el) el.classList.add('hidden');
    });
    if (section) section.classList.remove('hidden');
}

function setStepperPhase(phase) {
    // Reset tất cả bước
    [step1, step2, step3].forEach(s => { if (s) { s.classList.remove('active', 'done'); } });
    [conn1, conn2].forEach(c => { if (c) c.classList.remove('done'); });

    if (phase === 1) {
        step1?.classList.add('active');
    } else if (phase === 2) {
        step1?.classList.add('done');
        conn1?.classList.add('done');
        step2?.classList.add('active');
    } else if (phase === 3) {
        step1?.classList.add('done');
        step2?.classList.add('done');
        conn1?.classList.add('done');
        conn2?.classList.add('done');
        step3?.classList.add('active');
    }
}

function updateProgressBar(index, total) {
    const pct = total > 0 ? Math.round(((index + 1) / total) * 100) : 0;
    if (progressBar)   progressBar.style.width = pct + '%';
    if (progressLabel) progressLabel.textContent = (index + 1) + ' of ' + total + ' words';
    if (scoreLabel)    scoreLabel.textContent = pct + '%';
}

// ─── PHA 1: FLASHCARD ─────────────────────────────────────────────────────────────
function renderFlashcard() {
    if (vocabList.length === 0) return;
    const word = vocabList[flashIndex];

    flashContainer.classList.remove('is-flipped');
    cardKorean.textContent  = word.koreanWord;
    cardRomaji.textContent  = word.romaji;
    cardEnglish.textContent = getMeaning(word);

    if (word.mnemonic) {
        cardMnemonic.textContent = word.mnemonic;
        mnemonicBox.classList.remove('hidden');
    } else {
        mnemonicBox.classList.add('hidden');
    }

    prevBtn.disabled = flashIndex === 0;

    const isLast = flashIndex === vocabList.length - 1;
    nextBtnText.textContent = isLast ? 'Finish Flashcards' : 'Next Word';
    nextBtnIcon.textContent = isLast ? 'check_circle' : 'arrow_forward';
    if (isLast) {
        nextBtn.classList.replace('bg-primary', 'bg-secondary');
    } else {
        nextBtn.classList.replace('bg-secondary', 'bg-primary');
    }

    if (currentCountEl) currentCountEl.textContent = flashIndex + 1;
    if (totalCountEl)   totalCountEl.textContent   = vocabList.length;
    updateProgressBar(flashIndex, vocabList.length);
}

// Lật thẻ khi click vào flashcard.
// Khi lật lần đầu (chưa flipped), gọi API ghi nhận tiến độ học.
flashContainer.addEventListener('click', () => {
    const wasFlipped = flashContainer.classList.contains('is-flipped');
    flashContainer.classList.toggle('is-flipped');
    if (!wasFlipped && vocabList.length > 0) {
        recordProgress(vocabList[flashIndex].id);
    }
});

prevBtn.addEventListener('click', () => {
    if (flashIndex > 0) { flashIndex--; renderFlashcard(); }
});

nextBtn.addEventListener('click', () => {
    if (flashIndex < vocabList.length - 1) {
        flashIndex++;
        renderFlashcard();
    } else {
        // Hoàn thành flashcard → hiển thị màn hình chuyển tiếp
        enterTransition();
    }
});

// ─── MÀN HÌNH CHUYỂN TIẾPP ────────────────────────────────────────────────────────
function enterTransition() {
    synth.cancel();
    showOnly(phaseTransition);
    setStepperPhase(2);
    if (sessionTitle) sessionTitle.textContent = 'Great job!';

    const n = vocabList.length;
    if (transitionWordCount) transitionWordCount.textContent = n;
    if (transitionTotal)     transitionTotal.textContent     = n;
    if (transitionStreak)    transitionStreak.textContent    = lastStreakValue;
    if (transitionXp)        transitionXp.textContent        = '+' + flashXpEarned;

    // Reset thanh tiến trình về 0 cho pha listening
    if (progressBar)   progressBar.style.width = '0%';
    if (progressLabel) progressLabel.textContent = '0 of ' + n + ' words';
    if (scoreLabel)    scoreLabel.textContent = '0%';
}

function startListeningPhase() {
    listenIndex = 0;
    listenCorrect = 0;
    listenWrong   = 0;
    showOnly(phaseListening);
    setStepperPhase(2);
    if (sessionTitle) sessionTitle.textContent = 'Listening';
    renderListeningCard();
}

function skipToQuiz() {
    const ids = vocabList.map(v => v.id).join(',');
    window.location.href = '/game?vocabIds=' + ids;
}

// ─── PHA 2: LISTENING ──────────────────────────────────────────────────────────────
/**
 * Xây dựng mảng 4 đáp án: 1 đúng + 3 sai (distractor).
 * Distractor được chọn ngẫu nhiên từ vocabList (trừ đáp án đúng).
 * Nếu vocabList nhỏ hơn 4, bổ sung bằng placeholder để luôn có đủ 4 nút.
 *
 * @param {Object} word - Từ vựng hiện tại (đáp án đúng)
 * @returns {Array<{text: string, isCorrect: boolean}>} Mảng 4 đáp án đã xáo trộn
 */
function buildChoices(word) {
    const correctMeaning = getMeaning(word);
    // Tạo pool đáp án sai — loại bỏ đáp án đúng
    const distractorPool = vocabList
        .filter(v => getMeaning(v) && getMeaning(v) !== correctMeaning)
        .map(v => getMeaning(v))
        // loại bỏ trùng lặp
        .filter((m, i, arr) => arr.indexOf(m) === i)
        .sort(() => Math.random() - 0.5);

    // Lấy tối đa 3 đáp án sai
    const distractors = distractorPool.slice(0, 3);

    // Nếu chưa đủ 3, bổ sung bằng giá trị mặc định
    const fallbacks = ['(unknown)', '(other)', '(none)'];
    while (distractors.length < 3) {
        distractors.push(fallbacks[distractors.length]);
    }

    const choices = [
        { text: correctMeaning, isCorrect: true },
        ...distractors.map(m => ({ text: m, isCorrect: false }))
    ].sort(() => Math.random() - 0.5);

    return choices;
}

function renderListeningCard() {
    if (vocabList.length === 0) return;
    const word = vocabList[listenIndex];

    // Reset trạng thái cho từng từ
    listenPlayCount = 0;
    listenAnswered  = false;
    if (playCountEl)  playCountEl.textContent = '0';
    if (romajiHint)   romajiHint.classList.add('hidden');
    if (listenRomaji) listenRomaji.textContent = word.romaji;
    if (listenResult) {
        listenResult.classList.add('hidden');
        listenResult.className = 'hidden mt-4 w-full px-5 py-4 rounded-2xl flex items-center gap-3';
    }
    listenPlayBtn.classList.remove('speaking');
    soundRings.classList.remove('playing');
    listenIcon.textContent = 'volume_up';

    // Xây dựng các lựa chọn đáp án
    answerChoices.innerHTML = '';
    const choices = buildChoices(word);
    
    choices.forEach((choice, index) => {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'answer-choice';
        btn.textContent = choice.text || '(unknown)';
        btn.setAttribute('data-correct', choice.isCorrect ? 'true' : 'false');
        btn.style.cssText = 'display: inline-block !important;';
        btn.addEventListener('click', function() {
            handleAnswer(this, choice.isCorrect, word);
        });
        answerChoices.appendChild(btn);
    });

    // Điều hướng (Navigation)
    listenPrevBtn.disabled = listenIndex === 0;
    listenPrevBtn.style.opacity = listenIndex === 0 ? '0.35' : '1';
    listenPrevBtn.style.pointerEvents = listenIndex === 0 ? 'none' : 'auto';
    const isLast = listenIndex === vocabList.length - 1;
    listenNextText.textContent = isLast ? 'Finish & Quiz' : 'Next Word';
    listenNextIcon.textContent = isLast ? 'quiz' : 'arrow_forward';
    listenNextBtn.style.background = isLast ? '#6366f1' : '#059669';

    // Thống kê
    if (listenCurrentEl) listenCurrentEl.textContent = listenIndex + 1;
    if (listenTotalEl)   listenTotalEl.textContent   = vocabList.length;
    updateProgressBar(listenIndex, vocabList.length);

    // Tự động phát âm sau một khoảng ngắn
    setTimeout(speakListenWord, 450);
}

function handleAnswer(clickedBtn, isCorrect, word) {
    if (listenAnswered) return;
    listenAnswered = true;

    answerChoices.querySelectorAll('.answer-choice').forEach(btn => {
        btn.disabled = true;
        if (btn.dataset.correct === 'true') btn.classList.add('correct');
    });

    if (isCorrect) {
        clickedBtn.classList.add('correct');
        listenCorrect++;
        if (listenCorrectEl) listenCorrectEl.textContent = listenCorrect;
        showListenResult(true, word);
        recordProgress(word.id);
    } else {
        clickedBtn.classList.add('wrong');
        listenWrong++;
        if (listenWrongEl) listenWrongEl.textContent = listenWrong;
        showListenResult(false, word);
    }
}

function showListenResult(isCorrect, word) {
    listenResult.classList.remove('hidden');
    if (isCorrect) {
        listenResult.classList.add('correct-result');
        listenResult.classList.remove('wrong-result');
        listenResultIcon.textContent = 'check_circle';
        listenResultText.textContent = 'Correct! 🎉';
        listenResultDetail.textContent = word.koreanWord + ' = ' + getMeaning(word);
    } else {
        listenResult.classList.add('wrong-result');
        listenResult.classList.remove('correct-result');
        listenResultIcon.textContent = 'cancel';
        listenResultText.textContent = 'Not quite!';
        listenResultDetail.textContent = 'Answer: ' + getMeaning(word);
    }
}

listenPrevBtn.addEventListener('click', () => {
    if (listenIndex > 0) { listenIndex--; renderListeningCard(); }
});

listenNextBtn.addEventListener('click', () => {
    if (listenIndex < vocabList.length - 1) {
        listenIndex++;
        renderListeningCard();
    } else {
        // Hoàn thành listening → chuyển sang Quiz
        setStepperPhase(3);
        const ids = vocabList.map(v => v.id).join(',');
        window.location.href = '/game?vocabIds=' + ids;
    }
});

// ─── API ghi nhận tiến độ ─────────────────────────────────────────────────────────────
function recordProgress(vocabId) {
    if (!vocabId) return;
    fetch('/api/study/progress?vocabId=' + vocabId, { method: 'POST' })
        .then(r => r.json())
        .then(data => {
            if (data.xpGained) {
                flashXpEarned += data.xpGained;
                showXpToast(data.xpGained);
            }
            if (data.streak) lastStreakValue = data.streak;
            if (data.newBadges && data.newBadges.length > 0) {
                data.newBadges.forEach((b, i) =>
                    setTimeout(() => showBadgeToast(b), 800 + i * 1200));
            }
        })
        .catch(() => {});
}

// ─── Thông báo toast ──────────────────────────────────────────────────────────────
function showXpToast(xp) {
    const t = document.createElement('div');
    t.className = 'fixed top-20 right-4 z-50 flex items-center gap-2 bg-secondary text-on-secondary px-4 py-2 rounded-full shadow-lg text-sm font-bold';
    t.innerHTML = '<span style="font-variation-settings:\'FILL\' 1" class="material-symbols-outlined text-base">bolt</span>+' + xp + ' XP';
    document.body.appendChild(t);
    setTimeout(() => { t.style.transition = 'opacity .5s'; t.style.opacity = '0'; }, 1500);
    setTimeout(() => t.remove(), 2000);
}

/** Hiển thị thông báo mở khóa badge mới ở giữa dưới màn hình, tự biến mất sau 3.6 giây. */
function showBadgeToast(badge) {
    const t = document.createElement('div');
    t.className = 'fixed bottom-24 left-1/2 -translate-x-1/2 z-50 flex items-center gap-3 bg-white border border-amber-200 shadow-xl px-5 py-4 rounded-2xl max-w-xs w-full';
    t.innerHTML =
        '<div class="text-3xl">' + badge.emoji + '</div>' +
        '<div>' +
            '<p class="text-xs font-bold text-amber-600 uppercase tracking-widest">Badge Unlocked!</p>' +
            '<p class="font-bold text-slate-800">' + badge.name + '</p>' +
            '<p class="text-xs text-slate-500">' + badge.description + '</p>' +
        '</div>';
    document.body.appendChild(t);
    setTimeout(() => { t.style.transition = 'opacity .6s'; t.style.opacity = '0'; }, 3000);
    setTimeout(() => t.remove(), 3600);
}

// ─── Khởi tạo ─────────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    if (vocabList.length === 0) return;
    showOnly(phaseFlashcard);
    setStepperPhase(1);
    if (sessionTitle) sessionTitle.textContent = 'Flashcard';
    renderFlashcard();
});
