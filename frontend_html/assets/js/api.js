// ========================
// API CLIENT HOÀN CHỈNH
// ========================

const API_BASE_URL = "http://localhost:8080/api";

// ===== TOKEN =====
function getAuthToken() {
  return localStorage.getItem("userToken");
}

function getAdminToken() {
  return localStorage.getItem("adminToken");
}

// ===== SAVE AUTH =====
function saveUserAuth(token, user) {
  localStorage.setItem("userToken", token);
  localStorage.setItem("user", JSON.stringify(user));
}

function saveAdminAuth(token, admin) {
  localStorage.setItem("adminToken", token);
  localStorage.setItem("admin", JSON.stringify(admin));
}

// ===== CLEAR AUTH =====
function clearUserAuth() {
  localStorage.removeItem("userToken");
  localStorage.removeItem("user");
}

function clearAdminAuth() {
  localStorage.removeItem("adminToken");
  localStorage.removeItem("admin");
}

// ===== HEADERS =====
function getHeaders(isJson = true, isAdmin = false) {
  const token = isAdmin ? getAdminToken() : getAuthToken();
  const headers = {};

  if (isJson) headers["Content-Type"] = "application/json";
  if (token) headers["Authorization"] = `Bearer ${token}`;

  return headers;
}

// ===== HANDLE RESPONSE =====
async function handleResponse(response) {
  if (response.status === 401) {
    throw new Error("401 Unauthorized");
  }

  if (!response.ok) {
    const json = await response.json().catch(() => ({}));
    const message = json.message || json.error || "Request failed";
    const err = new Error(message);
    err.status = response.status;
    err.payload = json;
    throw err;
  }

  const text = await response.text();
  return text ? JSON.parse(text) : null;
}

// =============================================================
// AUTH API
// =============================================================
const authAPI = {
  async register(payload) {
    const res = await fetch(`${API_BASE_URL}/auth/register`, {
      method: "POST",
      headers: getHeaders(),
      body: JSON.stringify(payload),
    });
    return handleResponse(res);
  },

  async login(payload) {
    const res = await fetch(`${API_BASE_URL}/auth/login`, {
      method: "POST",
      headers: getHeaders(),
      body: JSON.stringify(payload),
    });

    const data = await handleResponse(res);

    saveUserAuth(data.token, {
      userID: data.userID,
      name: data.name,
      email: data.email,
      role: "USER",
    });

    return data;
  },

  async adminLogin(payload) {
    const res = await fetch(`${API_BASE_URL}/auth/admin/login`, {
      method: "POST",
      headers: getHeaders(),
      body: JSON.stringify(payload),
    });

    const data = await handleResponse(res);

    saveAdminAuth(data.token, {
      adminID: data.userID,
      name: data.name,
      email: data.email,
      role: "ADMIN",
    });

    return data;
  },

  logoutUser() {
    clearUserAuth();
  },

  logoutAdmin() {
    clearAdminAuth();
  },
};

// =============================================================
// BOOKS API
// =============================================================
const bookAPI = {
  list: async () => {
    const res = await fetch(`${API_BASE_URL}/books`, { headers: getHeaders() });
    return handleResponse(res);
  },

  get: async (bookID) => {
    const res = await fetch(`${API_BASE_URL}/books/${bookID}`, {
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  search: async (params) => {
    const qs = new URLSearchParams(params).toString();
    const res = await fetch(`${API_BASE_URL}/books/search?${qs}`);
    return handleResponse(res);
  },

  byProvince: async (province) => {
    const res = await fetch(`${API_BASE_URL}/books/province/${province}`, {
      headers: getHeaders(),
    });
    return handleResponse(res);
  },
};

// =============================================================
// CATEGORY API
// =============================================================
const categoryAPI = {
  async getAll() {
    const res = await fetch(`${API_BASE_URL}/categories`, {
      headers: getHeaders(),
    });
    return handleResponse(res);
  },
};

// =============================================================
// POST API (USER)
// =============================================================
const postAPI = {
  create: async (data) => {
    const res = await fetch(`${API_BASE_URL}/posts`, {
      method: "POST",
      headers: getHeaders(),
      body: JSON.stringify(data),
    });
    return handleResponse(res);
  },

  getMyPosts: async () => {
    const res = await fetch(`${API_BASE_URL}/my-posts`, {
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  update: async (postID, payload) => {
    const res = await fetch(`${API_BASE_URL}/my-posts/${postID}`, {
      method: "PUT",
      headers: getHeaders(),
      body: JSON.stringify(payload),
    });
    return handleResponse(res);
  },

  delete: async (postID) => {
    const res = await fetch(`${API_BASE_URL}/my-posts/${postID}`, {
      method: "DELETE",
      headers: getHeaders(false),
    });
    return handleResponse(res);
  },

  markSold: async (postID) => {
    const res = await fetch(`${API_BASE_URL}/my-posts/${postID}/sold`, {
      method: "PUT",
      headers: getHeaders(false),
    });
    return handleResponse(res);
  },
};

// =============================================================
// USER API
// =============================================================
const userAPI = {
  getById: async (id) => {
    const res = await fetch(`${API_BASE_URL}/users/${id}`, {
      headers: getHeaders(true, false),
    });
    return handleResponse(res);
  },

  updateProfile: async (id, payload) => {
    const res = await fetch(`${API_BASE_URL}/users/${id}`, {
      method: "PUT",
      headers: getHeaders(true, false),
      body: JSON.stringify(payload),
    });
    return handleResponse(res);
  },

  changePassword: async (id, payload) => {
    const res = await fetch(`${API_BASE_URL}/users/${id}/change-password`, {
      method: "POST",
      headers: getHeaders(true, false),
      body: JSON.stringify(payload),
    });
    return handleResponse(res);
  },
};

// =============================================================
// IMAGE API
// =============================================================
const imageAPI = {
  upload: async (file) => {
    const fd = new FormData();
    fd.append("file", file);

    const res = await fetch(`${API_BASE_URL}/images/upload`, {
      method: "POST",
      headers: { Authorization: "Bearer " + getAuthToken() },
      body: fd,
    });

    return handleResponse(res);
  },

  delete: async (fileName) => {
    const res = await fetch(`${API_BASE_URL}/images/${fileName}`, {
      method: "DELETE",
      headers: getHeaders(false),
    });
    return handleResponse(res);
  },
};

// =============================================================
// ADMIN API
// =============================================================
const adminAPI = {
  async getAllPosts() {
    const res = await fetch(`${API_BASE_URL}/admin/posts`, {
      headers: getHeaders(true, true),
    });
    return handleResponse(res);
  },

  async getPostsByStatus(status) {
    const res = await fetch(
      `${API_BASE_URL}/admin/posts/status/${encodeURIComponent(status)}`,
      {
        headers: getHeaders(true, true),
      }
    );
    return handleResponse(res);
  },

  async updatePostStatus(postID, payload) {
    const res = await fetch(`${API_BASE_URL}/admin/posts/${postID}/status`, {
      method: "PUT",
      headers: getHeaders(true, true),
      body: JSON.stringify(payload),
    });
    return handleResponse(res);
  },

  async listUsers() {
    const res = await fetch(`${API_BASE_URL}/admin/users`, {
      headers: getHeaders(true, true),
    });
    return handleResponse(res);
  },

  async updateUserStatus(userID, payload) {
    const res = await fetch(`${API_BASE_URL}/admin/users/${userID}/status`, {
      method: "PUT",
      headers: getHeaders(true, true),
      body: JSON.stringify(payload),
    });
    return handleResponse(res);
  },

  async deleteUser(userID) {
    const res = await fetch(`${API_BASE_URL}/admin/users/${userID}`, {
      method: "DELETE",
      headers: getHeaders(false, true),
    });
    return handleResponse(res);
  },

  async createCategory(payload) {
    const res = await fetch(`${API_BASE_URL}/admin/categories`, {
      method: "POST",
      headers: getHeaders(true, true),
      body: JSON.stringify(payload),
    });
    return handleResponse(res);
  },

  async updateCategory(categoryID, payload) {
    const res = await fetch(`${API_BASE_URL}/admin/categories/${categoryID}`, {
      method: "PUT",
      headers: getHeaders(true, true),
      body: JSON.stringify(payload),
    });
    if (!res.ok) {
      throw new Error("Lỗi cập nhật từ Server");
    }
    try {
      return await res.json();
    } catch (err) {
      console.warn("Backend trả về HTML nhưng status OK -> Coi như thành công.");
      return { success: true };
    }
  },
  async deleteCategory(categoryID) {
    const res = await fetch(`${API_BASE_URL}/admin/categories/${categoryID}`, {
      method: "DELETE",
      headers: getHeaders(false, true),
    });

    if (!res.ok) {
      throw new Error("Không thể xóa danh mục này (có thể do lỗi Server)");
    }
    try {
      return await res.json();
    } catch (err) {
      console.warn("Delete thành công nhưng Backend không trả về JSON.");
      return { success: true };
    }
  },
};

// EXPORT
window.api = {
  authAPI,
  bookAPI,
  postAPI,
  imageAPI,
  categoryAPI,
  userAPI,
  adminAPI,
};
