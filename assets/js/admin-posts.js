document.addEventListener("DOMContentLoaded", () => {
    const admin = JSON.parse(localStorage.getItem("admin") || "null");
    if (!admin || admin.role !== "ADMIN") {
        window.location.href = "loginAdmin.html";
        return;
    }
    // Cấu hình Toast
    window.Toast = Swal.mixin({
        toast: true, position: 'top-end', showConfirmButton: false, timer: 3000, timerProgressBar: true
    });
    loadData();
});

const formatCurrency = (amount) => new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(amount || 0);
const formatDate = (dateString) => dateString ? new Date(dateString).toLocaleDateString("vi-VN") : "N/A";

// Biến lưu trữ dữ liệu gốc
let allPosts = [];
let currentFilter = 'ALL'; // Trạng thái lọc hiện tại

// ==========================================
// 1. TẢI DỮ LIỆU
// ==========================================
async function loadData() {
    const tbody = document.getElementById("postsTableBody");
    const countEl = document.getElementById("totalPostsCount");

    try {
        tbody.innerHTML = `<tr><td colspan="7" class="text-center py-5"><div class="spinner-border text-primary"></div> Đang tải...</td></tr>`;

        // Chỉ cần gọi API lấy Posts, không cần List Users nữa vì đã ẩn cột người bán
        const postsRes = await window.api.adminAPI.getAllPosts();

        // Xử lý dữ liệu trả về (mảng hoặc object chứa mảng)
        allPosts = Array.isArray(postsRes) ? postsRes : (postsRes.data || []);

        // Sắp xếp mới nhất lên đầu
        allPosts.sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0));

        if (countEl) countEl.innerText = `Tổng số: ${allPosts.length} bài đăng`;
        
        // Render dữ liệu
        applyFilterAndRender();

    } catch (error) {
        console.error(error);
        if (error.message.includes("403")) {
            alert("Hết phiên đăng nhập!"); window.location.href = "loginAdmin.html";
        }
        tbody.innerHTML = `<tr><td colspan="7" class="text-center text-danger">Lỗi: ${error.message}</td></tr>`;
    }
}

// ==========================================
// 2. LOGIC LỌC (FILTER) & TÌM KIẾM
// ==========================================

// Hàm này được gọi khi bấm nút lọc
// btn: nút vừa bấm (this), statusKey: trạng thái cần lọc
window.filterPosts = function (btn, statusKey) {
    // 1. Update UI nút bấm (Xóa active cũ, thêm active mới)
    const buttons = document.querySelectorAll(".btn-group .btn");
    buttons.forEach(b => b.classList.remove("active"));
    if(btn) btn.classList.add("active");

    // 2. Cập nhật trạng thái và render lại
    currentFilter = statusKey;
    applyFilterAndRender();
};

// Hàm tìm kiếm
window.searchPosts = function () {
    applyFilterAndRender();
};

// Hàm trung tâm: Kết hợp Lọc + Tìm kiếm + Render
function applyFilterAndRender() {
    const keyword = document.getElementById("searchInput").value.toLowerCase();
    
    const filtered = allPosts.filter(p => {
        // 1. Kiểm tra Lọc theo Trạng thái
        const pStatus = (p.status || p.postStatus || "").toUpperCase();
        const matchesStatus = (currentFilter === 'ALL') || (pStatus === currentFilter);

        // 2. Kiểm tra Tìm kiếm (Chỉ tìm theo Tên sách vì đã ẩn người bán)
        const title = (p.book?.title || p.title || "").toLowerCase();
        const matchesSearch = title.includes(keyword);

        return matchesStatus && matchesSearch;
    });

    renderPosts(filtered);
}

// ==========================================
// 3. RENDER BẢNG (ĐÃ BỎ CỘT NGƯỜI BÁN)
// ==========================================
function renderPosts(postsData) {
    const tbody = document.getElementById("postsTableBody");
    tbody.innerHTML = "";

    if (!postsData || postsData.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted py-4">Không tìm thấy bài đăng phù hợp</td></tr>';
        return;
    }

    postsData.forEach(post => {
        const pID = post.postID || post.id || post._id;
        
        // Thông tin sách
        const bTitle = post.book?.title || post.title || "Không tiêu đề";
        const bPrice = post.book?.price || post.price || 0;
        const bImage = post.book?.image || post.image || "assets/images/no-image.png";
        
        // Trạng thái
        const status = (post.status || post.postStatus || "PENDING").toUpperCase();
        let badge = getStatusBadge(status);

        // Actions
        let actions = '';
        if (status === 'PENDING') {
            actions = `
                <button class="btn btn-sm btn-success me-1" onclick="updatePostStatus('${pID}', 'APPROVED')" title="Duyệt"><i class="bi bi-check-lg"></i></button>
                <button class="btn btn-sm btn-danger" onclick="updatePostStatus('${pID}', 'DECLINED')" title="Từ chối"><i class="bi bi-x-lg"></i></button>`;
        } else if (status === 'APPROVED') {
            actions = `<button class="btn btn-sm btn-outline-secondary" onclick="updatePostStatus('${pID}', 'SOLD')">Đã bán</button>`;
        } else {
            actions = `<span class="text-muted small">--</span>`;
        }

        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td class="fw-bold text-secondary">#${pID}</td>
            <td><img src="${bImage}" class="book-thumb" onerror="this.src='https://via.placeholder.com/50'"></td>
            <td class="fw-bold text-dark text-wrap" style="max-width: 350px;">${bTitle}</td>
            
            <td class="fw-bold text-danger">${formatCurrency(bPrice)}</td>
            <td class="small text-muted">${formatDate(post.createdAt)}</td>
            <td>${badge}</td>
            <td class="text-end">${actions}</td>
        `;
        tbody.appendChild(tr);
    });
}

function getStatusBadge(status) {
    switch (status) {
        case "PENDING": return `<span class="badge bg-warning text-dark">⏳ Chờ duyệt</span>`;
        case "APPROVED": return `<span class="badge bg-success">✔ Đã duyệt</span>`;
        case "DECLINED": 
        case "REJECTED": return `<span class="badge bg-danger">❌ Từ chối</span>`;
        case "SOLD": return `<span class="badge bg-secondary">💰 Đã bán</span>`;
        default: return `<span class="badge bg-light text-dark">? ${status}</span>`;
    }
}

// ==========================================
// 4. HÀNH ĐỘNG
// ==========================================
window.updatePostStatus = async function (id, status) {
    const result = await Swal.fire({
        title: 'Xác nhận?',
        text: status === 'APPROVED' ? 'Duyệt bài này?' : (status === 'DECLINED' ? 'Từ chối bài này?' : 'Đánh dấu đã bán?'),
        icon: 'question', showCancelButton: true, confirmButtonText: 'Đồng ý'
    });

    if (result.isConfirmed) {
        try {
            Swal.showLoading();
            await window.api.adminAPI.updatePostStatus(id, { status: status });
            await loadData(); // Load lại dữ liệu
            Swal.close();
            window.Toast.fire({ icon: 'success', title: 'Thành công!' });
        } catch (e) {
            Swal.fire('Lỗi', e.message, 'error');
        }
    }
}