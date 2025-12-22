/**
 * File: edit-post.js
 * Chỉnh sửa bài đăng – FULLY FIXED
 */

document.addEventListener("DOMContentLoaded", async () => {
  if (!requireAuth()) return;

  await loadAddressOptions(); // load tỉnh + huyện
  await loadCategories(); // load thể loại
  await loadPostData(); // load dữ liệu bài đăng

  setupImagePreview();
  setupSubmit();
});

let currentImageUrl = null;
let selectedImageFile = null;

// Lấy ID bài đăng từ URL
function getPostId() {
  const params = new URLSearchParams(window.location.search);
  return params.get("id");
}

/* ============================================================
   LOAD TỈNH / HUYỆN (giống account.js nhưng bỏ xã)
============================================================ */
async function loadAddressOptions() {
  const provinceSelect = document.getElementById("province");
  const districtSelect = document.getElementById("district");

  // Load tỉnh
  const provinces = await fetch("https://provinces.open-api.vn/api/p/").then((r) => r.json());

  provinceSelect.innerHTML = '<option value="">-- Chọn Tỉnh/Thành --</option>';
  provinces.forEach((p) => {
    provinceSelect.innerHTML += `<option value="${p.code}">${p.name}</option>`;
  });

  // Khi chọn tỉnh → load huyện
  provinceSelect.addEventListener("change", async function () {
    districtSelect.innerHTML = '<option value="">-- Chọn Quận/Huyện --</option>';

    if (!this.value) return;

    const selectedProvince = await fetch(`https://provinces.open-api.vn/api/p/${this.value}?depth=2`).then((r) =>
      r.json()
    );

    selectedProvince.districts.forEach((d) => {
      districtSelect.innerHTML += `<option value="${d.code}">${d.name}</option>`;
    });
  });
}

/* ============================================================
   LOAD THỂ LOẠI
============================================================ */
async function loadCategories() {
  const select = document.getElementById("category");
  const list = await categoryAPI.getAll();

  select.innerHTML = `<option value="">-- Chọn thể loại --</option>`;
  list.forEach((c) => {
    select.innerHTML += `<option value="${c.categoryID}">${c.categoryName}</option>`;
  });
}

/* ============================================================
   LOAD BÀI ĐĂNG CỦA NGƯỜI DÙNG
============================================================ */
async function loadPostData() {
  try {
    const postID = getPostId();

    const myPosts = await postAPI.getMyPosts();
    const post = myPosts.find((p) => p.postID == postID);

    if (!post) {
      showToast("Không tìm thấy bài đăng!", "error");
      return;
    }

    // Gán dữ liệu vào form
    document.getElementById("title").value = post.title;
    document.getElementById("author").value = post.author;
    document.getElementById("bookCondition").value = post.bookCondition;
    document.getElementById("price").value = post.price;
    document.getElementById("category").value = post.categoryID;
    document.getElementById("description").value = post.postDescription;
    document.getElementById("contactInfo").value = post.contactInfo;

    // Ảnh
    currentImageUrl = post.image;
    document.getElementById("imagePreview").src = post.image;

    // ĐỊA CHỈ (mã tỉnh, mã huyện)
    const provinceSelect = document.getElementById("province");
    const districtSelect = document.getElementById("district");

    // Set tỉnh
    provinceSelect.value = post.province;

    // Load huyện theo mã tỉnh
    const provinceData = await fetch(`https://provinces.open-api.vn/api/p/${post.province}?depth=2`).then((r) =>
      r.json()
    );

    districtSelect.innerHTML = '<option value="">-- Chọn Quận/Huyện --</option>';

    provinceData.districts.forEach((d) => {
      districtSelect.innerHTML += `<option value="${d.code}">${d.name}</option>`;
    });

    // Set huyện
    districtSelect.value = post.district;
  } catch (err) {
    console.error(err);
    showToast("Không thể tải dữ liệu bài đăng!", "error");
  }
}

/* ============================================================
   PREVIEW ẢNH
============================================================ */
function setupImagePreview() {
  document.getElementById("bookImage").addEventListener("change", (e) => {
    selectedImageFile = e.target.files[0];
    if (!selectedImageFile) return;

    const reader = new FileReader();
    reader.onload = (ev) => (document.getElementById("imagePreview").src = ev.target.result);
    reader.readAsDataURL(selectedImageFile);
  });
}

/* ============================================================
   SUBMIT FORM
============================================================ */
function setupSubmit() {
  document.getElementById("editPostForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const btn = document.getElementById("saveBtn");
    btn.disabled = true;
    btn.innerHTML = `<span class="spinner-border spinner-border-sm me-2"></span>Đang lưu...`;

    try {
      // Upload ảnh nếu có
      let finalImageUrl = currentImageUrl;

      if (selectedImageFile) {
        const formData = new FormData();
        formData.append("file", selectedImageFile);

        const res = await fetch(`${API_BASE_URL}/images/upload`, {
          method: "POST",
          headers: { Authorization: "Bearer " + getToken() },
          body: formData,
        });

        const data = await res.json();
        if (!data.success) throw new Error(data.message);
        finalImageUrl = data.fileUrl;
      }

      // Payload gửi API
      const payload = {
        title: document.getElementById("title").value,
        author: document.getElementById("author").value,
        price: parseFloat(document.getElementById("price").value),
        bookCondition: document.getElementById("bookCondition").value,
        categoryID: parseInt(document.getElementById("category").value),
        postDescription: document.getElementById("description").value,
        contactInfo: document.getElementById("contactInfo").value,
        image: finalImageUrl,

        // Lưu dạng MÃ tỉnh / huyện
        province: document.getElementById("province").value,
        district: document.getElementById("district").value,
      };

      await postAPI.update(getPostId(), payload);

      showToast("Cập nhật bài đăng thành công!", "success");
      setTimeout(() => (location.href = "my-posts.html"), 1200);
    } catch (err) {
      console.error(err);
      showToast(err.message || "Lỗi cập nhật bài đăng!", "error");
    }

    btn.disabled = false;
    btn.innerHTML = `<i class="bi bi-save me-2"></i>Lưu thay đổi`;
  });
}
