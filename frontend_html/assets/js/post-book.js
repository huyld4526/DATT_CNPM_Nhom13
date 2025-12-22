/**
 * File: frontend/assets/js/post-book.js
 * Đăng bán sách – phiên bản dùng API upload ảnh mới
 */

document.addEventListener("DOMContentLoaded", async () => {
  if (!requireAuth()) return;

  await loadCategories();
  setupImagePreview();
  setupSubmit();
});

let selectedImageFile = null;

// Load categories: GET /api/categories
async function loadCategories() {
  try {
    const categories = await categoryAPI.getAll();
    const select = document.getElementById("category");

    select.innerHTML = `
      <option value="">-- Chọn thể loại --</option>
    `;

    categories.forEach((cat) => {
      const opt = document.createElement("option");
      opt.value = cat.categoryID;
      opt.textContent = cat.categoryName;
      select.appendChild(opt);
    });
  } catch (error) {
    console.error("Error loading categories:", error);
    showToast("Không thể tải danh sách thể loại", "error");
  }
}

// Preview Image
function setupImagePreview() {
  document.getElementById("bookImage").addEventListener("change", (e) => {
    selectedImageFile = e.target.files[0];

    if (selectedImageFile) {
      const reader = new FileReader();
      reader.onload = (ev) => {
        document.getElementById("imagePreview").src = ev.target.result;
      };
      reader.readAsDataURL(selectedImageFile);
    }
  });
}

// Submit form
function setupSubmit() {
  document
    .getElementById("postBookForm")
    .addEventListener("submit", async (e) => {
      e.preventDefault();

      const btn = document.getElementById("submitBtn");
      btn.disabled = true;
      btn.innerHTML = `<span class="spinner-border spinner-border-sm me-2"></span>Đang đăng...`;

      try {
        // 1️⃣ Upload ảnh nếu có
        async function uploadImage(file) {
          const formData = new FormData();
          formData.append("file", file);

          const res = await fetch(`${API_BASE_URL}/images/upload`, {
            method: "POST",
            headers: {
              Authorization: "Bearer " + getToken(), // ⚠️ phải có token
            },
            body: formData,
          });

          const data = await res.json();
          if (!data.success) throw new Error(data.message);

          return data.fileUrl; // ⭐ quan trọng
        }

        const file = document.getElementById("bookImage").files[0];

        const imageUrl = await uploadImage(file);

        // 2️⃣ Lấy categories đã chọn
        const selectedCategories = Array.from(
          document.querySelectorAll("#categoriesCheckbox input:checked")
        ).map((cb) => Number(cb.value));

        // 3️⃣ Tạo book object đúng backend
        const bookData = {
          title: document.getElementById("title").value,
          author: document.getElementById("author").value,
          bookCondition: document.getElementById("bookCondition").value,
          price: parseFloat(document.getElementById("price").value),
          postDescription: document.getElementById("description").value,
          image: imageUrl,
          contactInfo: document.getElementById("contactInfo").value,
          categoryID: parseInt(document.getElementById("category").value),
          province: document.getElementById("province").value,
          district: document.getElementById("district").value,
        };

        // 4️⃣ Gửi bài đăng: POST /api/posts
        const userID = getUserId();
        await postAPI.create(bookData);

        showToast("Đăng bài thành công!", "success");
        setTimeout(() => (location.href = "my-posts.html"), 1200);
      } catch (err) {
        console.error(err);
        showToast(err.message || "Lỗi đăng bài!", "error");

        btn.disabled = false;
        btn.innerHTML = `<i class="bi bi-upload me-2"></i>Đăng bài`;
      }
    });

  const province = document.getElementById("province");
  const district = document.getElementById("district");

  // Load danh sách tỉnh
  fetch("https://provinces.open-api.vn/api/p/")
    .then((res) => res.json())
    .then((data) => {
      data.forEach((p) => {
        province.innerHTML += `<option value="${p.code}">${p.name}</option>`;
      });
    });

  // Khi chọn tỉnh → load huyện
  province.addEventListener("change", async function () {
    district.innerHTML = "<option value=''>-- Chọn Quận/Huyện --</option>";
    const res = await fetch(
      `https://provinces.open-api.vn/api/p/${this.value}?depth=2`
    );
    const data = await res.json();

    data.districts.forEach((d) => {
      district.innerHTML += `<option value="${d.code}">${d.name}</option>`;
    });
  });
}
