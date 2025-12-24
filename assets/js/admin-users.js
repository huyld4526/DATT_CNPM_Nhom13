// ==============================
// BIẾN TOÀN CỤC
// ==============================
let allUsers = [];

// ==============================
// KIỂM TRA QUYỀN + LOAD USER
// ==============================
document.addEventListener("DOMContentLoaded", async () => {
  await loadLocationData();
  const admin = JSON.parse(localStorage.getItem("admin") || "{}");

  if (!admin || admin.role !== "ADMIN") {
    alert("Bạn không có quyền truy cập!");
    window.location.href = "loginAdmin.html";
    return;
  }

  loadUsers();
});

// ==============================
// LOAD DANH SÁCH USER
// ==============================
async function loadUsers() {
  const tbody = document.getElementById("userTable");
  tbody.innerHTML = `<tr><td colspan="8" class="text-center">Đang tải...</td></tr>`;

  try {
    const users = await api.adminAPI.listUsers();
    allUsers = Array.isArray(users) ? users : [];
    renderUsers(allUsers);
  } catch (err) {
    alert("Lỗi tải danh sách user: " + err.message);
  }
}

// ==============================
// HIỂN THỊ BẢNG USER
// ==============================
function renderUsers(users) {
  const tbody = document.getElementById("userTable");
  tbody.innerHTML = "";

  if (!users.length) {
    tbody.innerHTML = `<tr><td colspan="8" class="text-center">Không có user</td></tr>`;
    return;
  }

  users.forEach((u) => {
    const provinceName = getProvinceName(u.province);
    const districtName = getDistrictName(u.province, u.district);
    const wardName = getWardName(u.district, u.ward);

    const address = [provinceName, districtName, wardName].filter(Boolean).join(" - ");

    const tr = document.createElement("tr");

    tr.innerHTML = `
      <td>${u.userID}</td>
      <td>${u.name}</td>
      <td>${u.email}</td>
      <td>${u.phone || ""}</td>
      <td>${address || "Chưa cập nhật"}</td>

      <td>
        <span class="badge ${getStatusBadge(u.status)}">
          ${u.status}
        </span>
      </td>

      <td>${formatDate(u.createdAt)}</td>

      <td>
        ${
          u.status === "ACTIVE" || u.status === "SUSPENDED"
            ? `
            <button class="btn btn-warning btn-sm"
              onclick="toggleStatus(${u.userID}, '${u.status}')">
              ${u.status === "ACTIVE" ? "Khóa" : "Mở"}
            </button>
          `
            : ""
        }

        <button class="btn btn-danger btn-sm"
          onclick="deleteUser(${u.userID})">
          Xóa
        </button>
      </td>
    `;

    tbody.appendChild(tr);
  });
}

// ==============================
// TÌM KIẾM REALTIME
// ==============================
function searchUsers() {
  const keyword = document.getElementById("searchInput").value.toLowerCase();

  const filtered = allUsers.filter(
    (u) =>
      u.name.toLowerCase().includes(keyword) ||
      u.email.toLowerCase().includes(keyword) ||
      (u.phone || "").includes(keyword)
  );

  renderUsers(filtered);
}

// ==============================
// KHÓA / MỞ USER (ACTIVE ↔ SUSPENDED)
// ==============================
async function toggleStatus(userID, currentStatus) {
  let newStatus = null;

  if (currentStatus === "ACTIVE") {
    newStatus = "SUSPENDED"; // KHÓA
  } else if (currentStatus === "SUSPENDED") {
    newStatus = "ACTIVE"; // MỞ
  } else {
    alert("Không thể đổi trạng thái user này!");
    return;
  }

  if (!confirm(`Đổi trạng thái thành ${newStatus}?`)) return;

  try {
    await api.adminAPI.updateUserStatus(userID, { status: newStatus });
    alert("Cập nhật trạng thái thành công!");
    loadUsers();
  } catch (err) {
    alert("Lỗi cập nhật: " + err.message);
  }
}

// ==============================
// XÓA USER
// ==============================
async function deleteUser(userID) {
  if (!confirm("Bạn có chắc muốn xóa user này?")) return;

  try {
    await api.adminAPI.deleteUser(userID); // ✅ đã fix TEXT bên api.js
    alert("Xóa user thành công!");
    loadUsers();
  } catch (err) {
    alert("Lỗi xóa user: " + err.message);
  }
}

// ==============================
// FORMAT NGÀY GIỜ
// ==============================
function formatDate(dateString) {
  if (!dateString) return "";
  const d = new Date(dateString);
  return d.toLocaleString("vi-VN");
}

// ==============================
// MÀU TRẠNG THÁI THEO ENUM BACKEND
// ==============================
function getStatusBadge(status) {
  switch (status) {
    case "ACTIVE":
      return "bg-success";
    case "PENDING":
      return "bg-warning text-dark";
    case "SUSPENDED":
      return "bg-secondary";
    case "BANNED":
      return "bg-danger";
    case "DELETED":
      return "bg-dark";
    default:
      return "bg-light text-dark";
  }
}
