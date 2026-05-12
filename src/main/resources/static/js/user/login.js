/**
 * login.js — Xử lý các tương tác trong trang Đăng nhập
 */

/**
 * Ẩn/Hiện mật khẩu khi người dùng nhấn vào biểu tượng con mắt
 */
function togglePassword() {
    const input = document.getElementById('password');
    const icon = document.getElementById('toggle-icon');
    if (input) {
        if (input.type === 'password') {
            // Chuyển sang hiển thị văn bản thuần
            input.type = 'text';
            if (icon) icon.textContent = 'visibility_off';
        } else {
            // Chuyển về dạng mật khẩu (dấu chấm)
            input.type = 'password';
            if (icon) icon.textContent = 'visibility';
        }
    }
}
