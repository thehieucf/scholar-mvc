/**
 * register.js — Xử lý các tương tác trong trang Đăng ký
 */

/**
 * Ẩn/Hiện mật khẩu cho ô nhập liệu cụ thể
 * @param {string} inputId ID của ô input mật khẩu
 * @param {HTMLElement} btn Nút nhấn kích hoạt việc ẩn/hiện
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
 * Lựa chọn trình độ học tập (Beginner, Intermediate, Advanced)
 * @param {HTMLElement} btn Nút trình độ được chọn
 */
function selectLevel(btn) {
    // Xóa trạng thái hoạt động của các nút khác
    const btns = document.querySelectorAll('#level-selector .level-btn');
    btns.forEach(b => b.classList.remove('active'));
    
    // Thêm trạng thái hoạt động cho nút vừa nhấn
    btn.classList.add('active');

    // Cập nhật giá trị vào ô input ẩn để gửi lên server
    const hidden = document.getElementById('currentLevel');
    if (hidden) {
        hidden.value = btn.textContent.trim();
    }
}
