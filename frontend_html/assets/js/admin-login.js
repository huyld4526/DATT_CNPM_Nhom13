document
  .getElementById("adminLoginForm")
  .addEventListener("submit", async (e) => {
    e.preventDefault();

    // 1. Lấy các phần tử giao diện
    const errorEl = document.getElementById("errorAlert");
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const loginBtn = document.getElementById("loginBtn");

    // Reset trạng thái lỗi & Disable nút login để tránh bấm nhiều lần
    errorEl.classList.add("d-none");
    errorEl.innerText = "";
    loginBtn.disabled = true;
    loginBtn.innerHTML =
      '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Đang xử lý...';

    try {
      // 2. Gọi API đăng nhập (Sử dụng api.js mới)
      // Hàm này sẽ tự động lưu 'adminToken' và 'admin' vào localStorage nếu thành công
      await window.api.authAPI.adminLogin({ email, password });

      // 3. Nếu không có lỗi, chuyển hướng sang Dashboard
      window.location.href = "admin-dashboard.html";
    } catch (err) {
      // 4. Xử lý lỗi
      console.error("Login Error:", err);
      errorEl.innerText = err.message || "Email hoặc mật khẩu không chính xác!";
      errorEl.classList.remove("d-none");

      loginBtn.disabled = false;
      loginBtn.innerHTML =
        '<i class="bi bi-box-arrow-in-right me-2"></i> Đăng nhập';
    }
  });
