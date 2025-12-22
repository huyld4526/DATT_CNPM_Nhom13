let provinceMap = {};
let districtMap = {};
let wardMap = {};

async function loadLocationData() {
  if (window.__locationLoaded) return;

  const res = await fetch("https://provinces.open-api.vn/api/?depth=3");
  const provinces = await res.json();

  provinces.forEach((p) => {
    provinceMap[p.code] = p.name; // Tỉnh

    districtMap[p.code] = p.districts; // Danh sách huyện theo tỉnh

    // Lưu ward theo từng districtCode
    p.districts.forEach((d) => {
      wardMap[d.code] = d.wards; // Danh sách phường theo huyện
    });
  });

  window.__locationLoaded = true;
}

function getProvinceName(code) {
  return provinceMap[code] || "Không rõ";
}

function getDistrictName(provinceCode, districtCode) {
  const districts = districtMap[provinceCode];
  if (!districts) return "";
  const d = districts.find((x) => x.code == districtCode);
  return d ? d.name : "";
}

function getWardName(districtCode, wardCode) {
  if (!wardMap[districtCode]) return "";

  const ward = wardMap[districtCode].find((w) => w.code == wardCode);
  return ward ? ward.name : "";
}
