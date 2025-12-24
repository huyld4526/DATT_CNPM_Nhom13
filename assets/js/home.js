/**
 * File: frontend/assets/js/home.js
 * Trang chủ - Hiển thị sách nổi bật và dữ liệu tổng quan
 */

document.addEventListener("DOMContentLoaded", async () => {
  await loadLocationData();
  loadBooks(1); // load trang đầu tiên
});

const ITEMS_PER_PAGE = 12;

async function loadBooks(page) {
  try {
    const books = await bookAPI.list(); // GET /books
    books.sort((a, b) => b.bookID - a.bookID);
    renderBooksPage(books, page);
    renderPagination(books.length, page);
  } catch (err) {
    console.error(err);
    showToast("Không thể tải dữ liệu sách!", "error");
  }
}

function renderBooksPage(books, page) {
  const start = (page - 1) * ITEMS_PER_PAGE;
  const end = start + ITEMS_PER_PAGE;

  const booksToShow = books.slice(start, end);
  const container = document.getElementById("featuredBooks");

  container.innerHTML = booksToShow.map((book) => createBookCard(book)).join("");
}

function renderPagination(totalItems, currentPage) {
  const totalPages = Math.ceil(totalItems / ITEMS_PER_PAGE);
  const pagination = document.getElementById("pagination");

  pagination.innerHTML = "";

  for (let i = 1; i <= totalPages; i++) {
    pagination.innerHTML += `
            <li class="page-item ${i === currentPage ? "active" : ""}">
                <a class="page-link" href="#" onclick="loadBooks(${i})">${i}</a>
            </li>
        `;
  }
}
