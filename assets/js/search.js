/**
 * File: frontend/assets/js/search.js
 * Search Books Page JavaScript (CÓ PHÂN TRANG)
 */

let allBooks = []; // Lưu toàn bộ books
const ITEMS_PER_PAGE = 12;

// Khi tải trang
document.addEventListener("DOMContentLoaded", async function () {
  await loadProvinces();
  await searchBooks(1); // Load trang đầu tiên
  await loadDistricts();

  document.getElementById("searchForm").addEventListener("submit", handleSearch);
  document.getElementById("resetBtn").addEventListener("click", handleReset);
});

/* ============================
   LOAD PROVINCE (API VN)
============================ */
async function loadProvinces() {
  try {
    const res = await fetch("https://provinces.open-api.vn/api/?depth=2");
    const provinces = await res.json();

    const select = document.getElementById("provinceFilter");

    provinces.forEach((p) => {
      provinceMap[p.code] = p.name;
      districtMap[p.code] = p.districts; // Lưu quận theo tỉnh
      const opt = document.createElement("option");
      opt.value = p.code;
      opt.textContent = p.name;
      select.appendChild(opt);
    });
    select.addEventListener("change", loadDistricts);
  } catch (err) {
    console.error("Error loading provinces:", err);
  }
}

async function loadDistricts() {
  const provinceCode = document.getElementById("provinceFilter").value;
  const districtSelect = document.getElementById("districtFilter");

  districtSelect.innerHTML = `<option value="">Chọn quận/huyện</option>`;

  if (!provinceCode) return;

  const districts = districtMap[provinceCode];
  if (!districts) return;

  districts.forEach((d) => {
    const opt = document.createElement("option");
    opt.value = d.code;
    opt.textContent = d.name;
    districtSelect.appendChild(opt);
  });
}

/* ============================
   SEARCH BOOKS — KHỚP BACKEND
============================ */
async function searchBooks(page = 1) {
  const title = document.getElementById("titleInput").value.trim();
  const author = document.getElementById("authorInput").value.trim();
  const province = document.getElementById("provinceFilter").value.trim();
  const district = document.getElementById("districtFilter").value.trim();

  console.log("titleInput =", document.getElementById("titleInput"));
  console.log("authorInput =", document.getElementById("authorInput"));
  console.log("🔥 searchBooks() ĐÃ ĐƯỢC GỌI");

  const params = {};

  if (title !== "") params.title = title;
  if (author !== "") params.author = author;
  if (province !== "") params.province = province;
  if (district !== "") params.district = district;

  const qs = new URLSearchParams(params).toString();

  console.log("➡ Gửi API:", `${API_BASE_URL}/books/search?${qs}`);

  try {
    const res = await fetch(`${API_BASE_URL}/books/search?${qs}`);
    const books = await res.json();
    console.log("🔥 Dữ liệu books nhận từ API:", books);
    
    books.sort((a, b) => b.bookID - a.bookID);
    allBooks = books; // Lưu toàn bộ books

    renderBooksPage(allBooks, page);
    renderPagination(allBooks.length, page);
  } catch (err) {
    console.error("Error searching books:", err);
    showToast("Không thể tìm kiếm sách!", "error");
  }
}

/* ============================
   PHÂN TRANG
============================ */
function renderBooksPage(books, page) {
  const start = (page - 1) * ITEMS_PER_PAGE;
  const end = start + ITEMS_PER_PAGE;

  const booksToShow = books.slice(start, end);
  const container = document.getElementById("searchResults");

  if (!books || books.length === 0) {
    container.innerHTML = `
      <div class="col-12 text-center py-5">
        <i class="bi bi-inbox fs-1 text-muted"></i>
        <h4 class="mt-3 text-muted">Không tìm thấy sách nào</h4>
      </div>
    `;
    return;
  }

  container.innerHTML = booksToShow.map((book) => createBookCard(book)).join("");
}

function renderPagination(totalItems, currentPage) {
  const totalPages = Math.ceil(totalItems / ITEMS_PER_PAGE);
  const pagination = document.getElementById("pagination");

  if (!pagination) return; // Nếu không có element pagination thì bỏ qua

  pagination.innerHTML = "";

  if (totalPages <= 1) return; // Không cần phân trang nếu chỉ có 1 trang

  for (let i = 1; i <= totalPages; i++) {
    pagination.innerHTML += `
      <li class="page-item ${i === currentPage ? "active" : ""}">
        <a class="page-link" href="#" onclick="searchBooks(${i}); return false;">${i}</a>
      </li>
    `;
  }
}

function renderBooks(books) {
  renderBooksPage(books, 1);
  renderPagination(books.length, 1);
}

/* ============================
   FORM EVENTS
============================ */
function handleSearch(e) {
  e.preventDefault();
  searchBooks(1); // Reset về trang 1 khi search mới
}

function handleReset() {
  document.getElementById("titleInput").value = "";
  document.getElementById("authorInput").value = "";
  document.getElementById("provinceFilter").value = "";
  document.getElementById("districtFilter").value = "";

  searchBooks(1); // Reset về trang 1
}

/* ============================
   UTIL
============================ */
function showLoading(id) {
  document.getElementById(id).innerHTML = `
    <div class="text-center py-5">
      <div class="spinner-border text-primary"></div>
    </div>
  `;
}