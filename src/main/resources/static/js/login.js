/**
 * login.js — Handles interactions on the Login page
 */

/**
 * Toggle password visibility when the user clicks the eye icon
 */
function togglePassword() {
    const input = document.getElementById('password');
    const icon = document.getElementById('toggle-icon');
    if (input) {
        if (input.type === 'password') {
            // Switch to plain text
            input.type = 'text';
            if (icon) icon.textContent = 'visibility_off';
        } else {
            // Switch back to password (dots)
            input.type = 'password';
            if (icon) icon.textContent = 'visibility';
        }
    }
}
