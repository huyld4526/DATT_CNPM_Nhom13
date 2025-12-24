/**
 * File: frontend/assets/js/auth.js
 * Authentication Utilities
 */

// Kiểm tra user đã đăng nhập chưa
function isAuthenticated() {
  return !!localStorage.getItem("authToken");
}

// Lấy thông tin user từ localStorage
function getCurrentUser() {
  const userStr = localStorage.getItem("user");
  if (userStr) {
    try {
      return JSON.parse(userStr);
    } catch (e) {
      return null;
    }
  }
  return null;
}

// Lưu thông tin đăng nhập
function saveAuthData(token, user) {
  localStorage.setItem("authToken", token);
  localStorage.setItem("user", JSON.stringify(user));
}

// Đăng xuất
function logout() {
  localStorage.removeItem("authToken");
  localStorage.removeItem("user");
  window.location.href = "/login.html";
}

// Lấy token
function getToken() {
  return localStorage.getItem("authToken");
}

// Lấy userID
function getUserId() {
  const user = getCurrentUser();
  return user?.userId || user?.userID || null;
}

// Lấy email
function getUserEmail() {
  const user = getCurrentUser();
  return user?.email || null;
}

// Kiểm tra và redirect nếu chưa đăng nhập
function requireAuth() {
  if (!isAuthenticated()) {
    showToast("Vui lòng đăng nhập để tiếp tục", "warning");
    setTimeout(() => {
      window.location.href = "/login.html";
    }, 1000);
    return false;
  }
  return true;
}

// Kiểm tra và redirect nếu đã đăng nhập
function redirectIfAuthenticated() {
  if (isAuthenticated()) {
    window.location.href = "/index.html";
  }
}
