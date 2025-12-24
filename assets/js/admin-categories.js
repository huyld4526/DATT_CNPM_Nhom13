document.addEventListener("DOMContentLoaded", () => {
    const admin = JSON.parse(localStorage.getItem("admin") || "null");
    if (!admin || admin.role !== "ADMIN") {
        window.location.href = "loginAdmin.html";
        return;
    }

    // 2. KHAI BÁO BIẾN
    const categoryForm = document.getElementById("categoryForm");
    const categoryID = document.getElementById("categoryID");
    const categoryName = document.getElementById("categoryName");
    const categoryTable = document.getElementById("categoryTable");
    const btnCancel = document.getElementById("btnCancel");
    const btnSubmit = document.getElementById("btnSubmit");
    const formTitle = document.getElementById("formTitle"); // Tiêu đề form

    // Cấu hình thông báo nhỏ gọn (Toast)
    const Toast = Swal.mixin({
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 3000,
        timerProgressBar: true
    });

    // 3. HÀM RESET FORM (Về trạng thái thêm mới)
    function resetForm() {
        categoryID.value = "";
        categoryForm.reset();
        
        // Đổi giao diện về "Thêm mới"
        if(btnSubmit) btnSubmit.innerHTML = '<i class="bi bi-save me-1"></i> Lưu';
        if(btnSubmit) btnSubmit.classList.remove("btn-warning");
        if(btnSubmit) btnSubmit.classList.add("btn-primary");
        
        if(formTitle) formTitle.innerHTML = '<i class="bi bi-plus-circle me-2 text-primary"></i>Thêm Danh Mục Mới';
    }

    btnCancel.addEventListener("click", resetForm);

    // 4. XỬ LÝ SUBMIT (THÊM HOẶC SỬA)
    categoryForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const nameValue = categoryName.value.trim();
        if (!nameValue) {
            Toast.fire({ icon: 'warning', title: 'Tên danh mục không được để trống!' });
            return;
        }

        const payload = { categoryName: nameValue };
        const isEdit = !!categoryID.value; // Có ID là đang Sửa

        try {
            // Hiện loading
            Swal.fire({
                title: 'Đang xử lý...',
                didOpen: () => Swal.showLoading()
            });

            if (isEdit) {
                // Gọi API cập nhật
                await api.adminAPI.updateCategory(categoryID.value, payload);
                Toast.fire({ icon: 'success', title: 'Cập nhật danh mục thành công!' });
            } else {
                // Gọi API tạo mới
                await api.adminAPI.createCategory(payload);
                Toast.fire({ icon: 'success', title: 'Thêm danh mục thành công!' });
            }

            // Đóng loading swal
            Swal.close();

            // Reset và tải lại bảng
            resetForm();
            await loadCategories();

        } catch (err) {
            console.error(err);
            Swal.fire({
                icon: 'error',
                title: 'Lỗi!',
                text: err.message || "Không thể lưu danh mục."
            });
        }
    });

    // 5. TẢI DANH SÁCH
    async function loadCategories() {
        try {
            categoryTable.innerHTML = `<tr><td colspan="3" class="text-center py-4 text-muted">Đang tải dữ liệu...</td></tr>`;

            const data = await api.categoryAPI.getAll();

            if (!Array.isArray(data)) throw new Error("Dữ liệu lỗi.");

            renderTable(data);
        } catch (err) {
            console.error(err);
            categoryTable.innerHTML = `<tr><td colspan="3" class="text-center text-danger py-4">Lỗi tải dữ liệu!</td></tr>`;
        }
    }

    // 6. RENDER BẢNG
    function renderTable(categories) {
        categoryTable.innerHTML = "";
        
        // Sắp xếp ID mới nhất lên đầu (nếu muốn)
        // categories.sort((a, b) => b.categoryID - a.categoryID);

        if (categories.length === 0) {
            categoryTable.innerHTML = `<tr><td colspan="3" class="text-center py-4 text-muted">Chưa có danh mục nào</td></tr>`;
            return;
        }

        categories.forEach((c) => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td class="ps-4 fw-bold text-secondary">#${c.categoryID}</td>
                <td><span class="fw-bold text-dark">${c.categoryName}</span></td>
                <td class="text-center">
                    <button class="btn btn-sm btn-outline-warning me-2 btn-edit" 
                            data-id="${c.categoryID}" 
                            data-name="${c.categoryName}">
                        <i class="bi bi-pencil-square"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger btn-delete" 
                            data-id="${c.categoryID}">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            `;
            categoryTable.appendChild(tr);
        });
    }

    // 7. SỰ KIỆN CLICK TRONG BẢNG (SỬA / XÓA)
    categoryTable.addEventListener("click", async (e) => {
        const editBtn = e.target.closest(".btn-edit");
        const deleteBtn = e.target.closest(".btn-delete");

        // --- CHỨC NĂNG SỬA ---
        if (editBtn) {
            const id = editBtn.dataset.id;
            const name = editBtn.dataset.name;

            // Đưa dữ liệu lên form
            categoryID.value = id;
            categoryName.value = name;
            
            // Đổi giao diện form sang chế độ "Cập nhật"
            formTitle.innerHTML = `<i class="bi bi-pencil-square me-2 text-warning"></i>Chỉnh Sửa Danh Mục #${id}`;
            btnSubmit.innerHTML = `<i class="bi bi-check2-circle me-1"></i> Cập nhật`;
            btnSubmit.classList.remove("btn-primary");
            btnSubmit.classList.add("btn-warning");

            // Focus và cuộn lên
            categoryName.focus();
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }

        // --- CHỨC NĂNG XÓA ---
        if (deleteBtn) {
            const id = deleteBtn.dataset.id;
            
            const result = await Swal.fire({
                title: 'Xóa danh mục?',
                text: "Bạn có chắc chắn muốn xóa danh mục này?",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Xóa ngay',
                cancelButtonText: 'Hủy'
            });

            if (result.isConfirmed) {
                try {
                    await api.adminAPI.deleteCategory(id);
                    Toast.fire({ icon: 'success', title: 'Đã xóa danh mục!' });
                    await loadCategories();
                } catch (err) {
                    Swal.fire('Lỗi', err.message || 'Không thể xóa', 'error');
                }
            }
        }
    });

    // Chạy lần đầu
    loadCategories();
});