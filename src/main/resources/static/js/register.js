/**
 * register.js — Handles interactions on the Register page
 */

/**
 * Toggle password visibility for a specific input field
 * @param {string} inputId ID of the password input element
 * @param {HTMLElement} btn Button that triggered the toggle
 */
function togglePassword(inputId, btn) {
    const input = document.getElementById(inputId);
    const icon = btn.querySelector('.material-symbols-outlined');
    if (input && icon) {
        if (input.type === 'password') {
            input.type = 'text';
            icon.textContent = 'visibility_off';
        } else {
            input.type = 'password';
            icon.textContent = 'visibility';
        }
    }
}

/**
 * Select a proficiency level (Beginner, Intermediate, Advanced)
 * @param {HTMLElement} btn The level button that was clicked
 */
function selectLevel(btn) {
    // Remove active state from all buttons
    const btns = document.querySelectorAll('#level-selector .level-btn');
    btns.forEach(b => b.classList.remove('active'));

    // Set active state on the clicked button
    btn.classList.add('active');

    // Update the hidden input value to send to the server
    const hidden = document.getElementById('currentLevel');
    if (hidden) {
        hidden.value = btn.textContent.trim();
    }
}
