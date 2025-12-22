/**
 * File: frontend/assets/js/main.js
 * Main Utilities & Common Functions
 */

document.addEventListener("DOMContentLoaded", () => {
  const btnPostBook = document.getElementById("btnPostBook");
  if (!btnPostBook) return;

  btnPostBook.addEventListener("click", () => {
    const token = localStorage.getItem("authToken");

    if (!token) {
      const modal = new bootstrap.Modal(document.getElementById("loginRequiredModal"));
      modal.show();
      return;
    }

    // Đã đăng nhập -> chuyển về post-book
    window.location.href = "post-book.html";
  });
});

// Format giá tiền
function formatPrice(price) {
  return new Intl.NumberFormat("vi-VN").format(price) + "đ";
}

// Format ngày tháng
function formatDate(dateString) {
  const date = new Date(dateString);
  return date.toLocaleDateString("vi-VN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
}

// Hiển thị loading spinner
function showLoading(elementId) {
  const element = document.getElementById(elementId);
  if (element) {
    element.innerHTML = `
      <div class="loading-spinner">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Đang tải...</span>
        </div>
      </div>
    `;
  }
}

// Ẩn loading spinner
function hideLoading(elementId) {
  const element = document.getElementById(elementId);
  if (element) {
    element.innerHTML = "";
  }
}

// Hiển thị toast notification
function showToast(message, type = "success") {
  // Tạo toast container nếu chưa có
  let container = document.querySelector(".toast-container");
  if (!container) {
    container = document.createElement("div");
    container.className = "toast-container";
    document.body.appendChild(container);
  }

  // Tạo toast element
  const toast = document.createElement("div");
  toast.className = `toast toast-${type} show`;
  toast.setAttribute("role", "alert");

  const icon = type === "success" ? "✓" : type === "error" ? "✗" : "⚠";

  toast.innerHTML = `
    <div class="d-flex align-items-center p-3">
      <strong class="me-2">${icon}</strong>
      <div>${message}</div>
      <button type="button" class="btn-close btn-close-white ms-auto" onclick="this.parentElement.parentElement.remove()"></button>
    </div>
  `;

  container.appendChild(toast);

  // Tự động ẩn sau 3 giây
  setTimeout(() => {
    toast.remove();
  }, 3000);
}

// Tạo BookCard HTML
function createBookCard(book) {
  const defaultImage = "https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=600&q=80";
  const image = book.image || defaultImage;

  const provinceName = getProvinceName(book.province);
  const districtName = getDistrictName(book.province, book.district);

  return `
    <div class="col-sm-6 col-md-4 col-lg-3">
      <div class="card book-card h-100">
        <img src="${image}" class="card-img-top" alt="${book.title}" style="height: 400px
             onerror="this.src='${defaultImage}'">
        <div class="card-body d-flex flex-column">
          <h5 class="card-title">${book.title}</h5>

          <p class="book-meta flex-grow-1">
            <strong>Tác giả:</strong> ${book.author || "Không rõ"}<br>
            <strong>Tình trạng:</strong> ${book.bookCondition || "Cũ"}<br>
            <strong>Khu vực:</strong> 
              ${provinceName || "Không rõ"} 
              ${districtName ? " - " + districtName : ""}<br>
            <strong>Đăng:</strong> ${timeAgo(book.createdAt)}

          </p>
     

          <div class="d-flex justify-content-between align-items-center mt-auto">
            <span class="fw-bold text-danger fs-5">${formatPrice(book.price)}</span>
            <a href="book-detail.html?id=${book.bookID}" 
               class="btn btn-primary btn-sm">
              Xem chi tiết
            </a>
          </div>
        </div>
      </div>
    </div>
  `;
}

function timeAgo(dateString) {
  if (!dateString) return "Không rõ";

  // backend kiểu: "2025-12-03 13:31:14.126766"
  const date = new Date(dateString.replace(" ", "T"));

  if (isNaN(date.getTime())) return "Không rõ";

  const now = new Date();
  const diff = (now - date) / 1000; // giây

  if (diff < 60) return "mới đăng";

  if (diff < 3600) {
    const mins = Math.floor(diff / 60);
    return mins <= 1 ? "mới đăng" : `${mins} phút trước`;
  }

  if (diff < 86400) {
    const hours = Math.floor(diff / 3600);
    return `${hours} giờ trước`;
  }

  const days = Math.floor(diff / 86400);
  return `${days} ngày trước`;
}

// Update navbar theo trạng thái đăng nhập
function updateNavbar() {
  const user = getCurrentUser();
  const isLoggedIn = isAuthenticated();

  const authLinks = document.getElementById("authLinks");
  if (!authLinks) return;

  if (isLoggedIn) {
    authLinks.innerHTML = `
      <li class="nav-item">
        <a class="nav-link" href="post-book.html">
          <i class="bi bi-pencil-square me-1"></i>Đăng bài
        </a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="my-posts.html">
          <i class="bi bi-collection me-1"></i>Bài đăng của tôi
        </a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="account.html">
          <i class="bi bi-person-circle me-1"></i>${user?.name || "Tài khoản"}
        </a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="#" onclick="logout(); return false;">
          <i class="bi bi-box-arrow-right me-1"></i>Đăng xuất
        </a>
      </li>
    `;
  } else {
    authLinks.innerHTML = `
      <li class="nav-item">
        <a class="nav-link" href="login.html">
          <i class="bi bi-box-arrow-in-right me-1"></i>Đăng nhập
        </a>
      </li>
    `;
  }
}

// Set active navbar link
function setActiveNavLink() {
  const currentPage = window.location.pathname.split("/").pop() || "index.html";
  const navLinks = document.querySelectorAll(".nav-link");

  navLinks.forEach((link) => {
    const href = link.getAttribute("href");
    if (href && href.includes(currentPage)) {
      link.classList.add("active");
    }
  });
}

// Đăng xuất
function logout() {
  localStorage.removeItem("userToken"); // tên mới
  localStorage.removeItem("adminToken"); // nếu có
  localStorage.removeItem("authToken"); // xoá tên cũ luôn cho chắc

  localStorage.removeItem("user");
  window.location.href = "index.html";
}

// Lấy URL parameter
function getUrlParameter(name) {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get(name);
}

// Validate email
function isValidEmail(email) {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return re.test(email);
}

// Initialize common features khi DOM loaded
document.addEventListener("DOMContentLoaded", function () {
  updateNavbar();
  setActiveNavLink();
});
