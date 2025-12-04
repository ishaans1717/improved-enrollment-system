// fake database to test
const COURSES_BY_SUBJECT = {
  IT: [
    {
      code: "IT 326",
      title: "Principles of Software Engineering",
      days: "T TH",
      time: "2:00 PM - 3:15 PM",
      location: "STV 240",
      credits: 3
    },
    {
      code: "IT 383",
      title: "Principles of Operating Systems",
      days: "M W",
      time: "3:35 PM - 4:50 PM",
      location: "STV 108",
      credits: 3
    },
    {
      code: "IT 355",
      title: "Secure Software Development",
      days: "T TH",
      time: "2:00 PM - 3:15 PM",
      location: "ITC 101",
      credits: 3
    }
  ],
  ACC: [
    {
      code: "ACC 131",
      title: "Financial Accounting",
      days: "M W",
      time: "11:00 AM - 12:15 PM",
      location: "CTK 201",
      credits: 3
    },
    {
      code: "ACC 132",
      title: "Managerial Accounting",
      days: "T TH",
      time: "2:00 PM - 3:15 PM",
      location: "CTK 203",
      credits: 3
    },
    {
      code: "ACC 261",
      title: "Business Information Systems",
      days: "M W",
      time: "3:00 PM - 4:15 PM",
      location: "STV 120",
      credits: 3
    }
  ],
  MAT: [
    {
      code: "MAT 145",
      title: "Calculus I",
      days: "M W F",
      time: "9:00 AM - 9:50 AM",
      location: "STV 101",
      credits: 4
    },
    {
      code: "MAT 146",
      title: "Calculus II",
      days: "M W F",
      time: "11:00 AM - 11:50 AM",
      location: "STV 101",
      credits: 4
    },
    {
      code: "MAT 175",
      title: "Discrete Mathematics",
      days: "T TH",
      time: "12:30 PM - 1:45 PM",
      location: "STV 215",
      credits: 3
    }
  ]
};

// State 
let currentResults = [];
let cart = [];
let schedule = [];

const searchInput = document.getElementById("search-input");
const searchBtn = document.getElementById("search-btn");
const searchError = document.getElementById("search-error");

const resultCountSpan = document.getElementById("result-count");
const resultsContainer = document.getElementById("results-container");

const cartContainer = document.getElementById("cart-container");
const submitBtn = document.getElementById("submit-btn");

const scheduleList = document.getElementById("schedule-list");


searchBtn.addEventListener("click", handleSearch);
searchInput.addEventListener("keydown", (e) => {
  if (e.key === "Enter") handleSearch();
});

submitBtn.addEventListener("click", handleSubmitRegistration);

document.addEventListener("DOMContentLoaded", () => {
  // load schedule
  const stored = localStorage.getItem("studentSchedule");
  if (stored) {
    try {
      schedule = JSON.parse(stored);
    } catch (e) {
      console.error("Failed to parse stored schedule", e);
      schedule = [];
    }
  }
  renderSchedule();

  // auto-search from home page
  const query = sessionStorage.getItem("courseSearchQuery");
  if (query) {
    searchInput.value = query;
    handleSearch();
    sessionStorage.removeItem("courseSearchQuery");
  }
});


// Load existing schedule from localStorage when the page opens
document.addEventListener("DOMContentLoaded", () => {
  const stored = localStorage.getItem("studentSchedule");
  if (stored) {
    try {
      schedule = JSON.parse(stored);
    } catch (e) {
      console.error("Failed to parse stored schedule", e);
      schedule = [];
    }
  }
  renderSchedule();
});


// Functions 

// Handle search click
function handleSearch() {
  const subject = searchInput.value.trim().toUpperCase();

  searchInput.classList.remove("input-error");
  searchError.textContent = "";

  resultsContainer.innerHTML = "";
  currentResults = [];
  resultCountSpan.textContent = "0";

  if (!subject || !COURSES_BY_SUBJECT[subject]) {
    searchInput.classList.add("input-error");
    searchError.textContent =
      "Please enter a valid subject (e.g., IT, ACC, MAT).";
    return;
  }

  const courses = COURSES_BY_SUBJECT[subject];
  currentResults = courses;
  resultCountSpan.textContent = courses.length.toString();

  renderResults(courses);
}

// Render course result cards
function renderResults(courses) {
  resultsContainer.innerHTML = "";

  courses.forEach((course, index) => {
    const card = document.createElement("div");
    card.className = "result-block";

    card.innerHTML = `
      <div class="logo-result">${course.code.split(" ")[0]}</div>
      <div class="class-result-info">
        <p class="result-title">${course.code} ${course.title}</p>
        <p class="date">${course.days} ${course.time}</p>
      </div>
      <p class="credit-amount">${course.credits} Credits</p>
      <button
        class="add-to-cart-btn"
        data-index="${index}"
      >
        Add to Cart
      </button>
    `;

    resultsContainer.appendChild(card);
  });

  const buttons = resultsContainer.querySelectorAll(".add-to-cart-btn");
  buttons.forEach((btn) => {
    btn.addEventListener("click", () => {
      const idx = parseInt(btn.getAttribute("data-index"), 10);
      const course = currentResults[idx];
      addToCart(course);
    });
  });
}

function addToCart(course) {
  if (cart.find((c) => c.code === course.code)) {
    return; // already in cart
  }
  cart.push(course);
  renderCart();
}

function renderCart() {
  cartContainer.innerHTML = "";

  if (cart.length === 0) {
    cartContainer.innerHTML =
      '<p style="font-size: 0.9rem; color:#6b7280;">No courses in cart yet.</p>';
    return;
  }

  cart.forEach((course, idx) => {
    const block = document.createElement("div");
    block.className = "cart-block";

    block.innerHTML = `
      <div class="cart-class">
        <p class="title">${course.code} ${course.title}</p>
        <p class="text">${course.days} ${course.time}</p>
      </div>
      <button class="remove-cart-btn" data-index="${idx}" style="background:#ef4444;color:#fff;border:none;border-radius:6px;padding:6px 12px;font-weight:600;cursor:pointer;margin-left:10px;height:35px;">Remove</button>
    `;

    cartContainer.appendChild(block);
  });

  // Attach listeners to remove buttons
  const removeBtns = cartContainer.querySelectorAll('.remove-cart-btn');
  removeBtns.forEach((btn) => {
    btn.addEventListener('click', () => {
      const idx = parseInt(btn.getAttribute('data-index'), 10);
      cart.splice(idx, 1);
      renderCart();
    });
  });
}

function handleSubmitRegistration() {
  if (cart.length === 0) {
    alert("Your cart is empty.");
    return;
  }

  schedule = [...cart];

  localStorage.setItem("studentSchedule", JSON.stringify(schedule));

  renderSchedule();

  alert("Registration submitted! Schedule updated.");
}


// Render schedule panel
function renderSchedule() {
  scheduleList.innerHTML = "";

  if (schedule.length === 0) {
    scheduleList.innerHTML =
      '<p style="font-size: 0.9rem; color:#6b7280;">No courses scheduled yet.</p>';
    return;
  }

  schedule.forEach((course) => {
    const item = document.createElement("div");
    item.className = "schedule-item";

    item.innerHTML = `
      <div class="schedule-day-pill">${course.days}</div>
      <div class="schedule-info">
        <p class="schedule-course">
          ${course.code} ${course.title}
        </p>
        <p class="schedule-time">${course.time} • ${course.location}</p>
      </div>
    `;

    scheduleList.appendChild(item);
  });
}

// Initial render for empty cart + schedule
renderCart();
renderSchedule();
