/**
 * File: frontend/assets/js/login.js
 * Login Page JavaScript - KẾT NỐI API BACKEND
 */

document.addEventListener("DOMContentLoaded", function () {
  // Redirect nếu đã đăng nhập
  redirectIfAuthenticated();

  // Setup form submit
  document.getElementById("loginForm").addEventListener("submit", handleLogin);
});

async function handleLogin(e) {
  e.preventDefault();

  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value;
  const errorAlert = document.getElementById("errorAlert");
  const loginBtn = document.getElementById("loginBtn");

  // Validate input
  if (!email || !password) {
    errorAlert.textContent = "Vui lòng nhập đầy đủ thông tin";
    errorAlert.classList.remove("d-none");
    return;
  }

  // Disable button và show loading
  loginBtn.disabled = true;
  loginBtn.innerHTML =
    '<span class="spinner-border spinner-border-sm me-2"></span>Đang đăng nhập...';
  errorAlert.classList.add("d-none");

  try {
    console.log("🔐 Attempting login with:", { email });

    // Gọi API: POST /api/auth/login
    const loginData = {
      email: email,
      password: password,
    };

    console.log("📡 Sending login request to:", API_BASE_URL + "/auth/login");
    const response = await authAPI.login(loginData);

    console.log("✅ Login response:", response);

    // Kiểm tra response structure
    if (!response.token || !response.userID) {
      throw new Error("Dữ liệu đăng nhập không hợp lệ");
    }

    // Lưu thông tin đăng nhập vào localStorage
    const userData = {
      userID: response.userID || response.userID,
      email: response.email,
      name: response.name,
      role: response.role,
    };

    console.log("💾 Saving auth data:", userData);
    saveAuthData(response.token, userData);

    showToast("Đăng nhập thành công!", "success");

    // Redirect về trang chủ sau 1 giây
    setTimeout(() => {
      window.location.href = "index.html";
    }, 1000);
  } catch (error) {
    console.error("❌ Login error:", error);

    // Hiển thị lỗi chi tiết
    let errorMessage = "Đăng nhập thất bại. ";

    if (error.message.includes("401")) {
      errorMessage += "Email hoặc mật khẩu không đúng.";
    } else if (error.message.includes("fetch")) {
      errorMessage +=
        "Không thể kết nối đến server. Vui lòng kiểm tra backend đã chạy chưa.";
    } else {
      errorMessage += error.message || "Vui lòng thử lại!";
    }

    errorAlert.textContent = errorMessage;
    errorAlert.classList.remove("d-none");

    // Enable button lại
    loginBtn.disabled = false;
    loginBtn.innerHTML =
      '<i class="bi bi-box-arrow-in-right me-2"></i>Đăng nhập';
  }
}

// Test connection khi load trang
window.addEventListener("load", function () {
  console.log("🌐 API Base URL:", API_BASE_URL);
  console.log("📍 Current page:", window.location.href);
  console.log("🔑 Is authenticated:", isAuthenticated());
});
