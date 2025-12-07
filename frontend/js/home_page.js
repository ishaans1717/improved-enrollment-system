// Handles search on home page and redirects to course_search.html 
const homeSearchInput = document.getElementById('home-search-input');
const homeSearchBtn = document.getElementById('home-search-btn');

if (homeSearchBtn && homeSearchInput) {
  homeSearchBtn.addEventListener('click', () => {
    const query = homeSearchInput.value.trim();
    if (query) {
      sessionStorage.setItem('courseSearchQuery', query);
      window.location.href = 'course_search.html';
    }
  });
  homeSearchInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      homeSearchBtn.click();
    }
  });
}

document.addEventListener("DOMContentLoaded", loadHomeSchedule);

function loadHomeSchedule() {
  const scheduleListHome = document.getElementById("home-schedule-list");
  if (!scheduleListHome) return;

  const stored = localStorage.getItem("studentSchedule");

  scheduleListHome.innerHTML = "";

  if (!stored) {
    scheduleListHome.innerHTML =
      '<p style="font-size:0.9rem;color:#6b7280;">No courses scheduled yet.</p>';
    return;
  }

  let schedule;
  try {
    schedule = JSON.parse(stored);
  } catch (e) {
    console.error("Failed to parse stored schedule", e);
    scheduleListHome.innerHTML =
      '<p style="font-size:0.9rem;color:#b91c1c;">Error loading schedule.</p>';
    return;
  }

  if (!Array.isArray(schedule) || schedule.length === 0) {
    scheduleListHome.innerHTML =
      '<p style="font-size:0.9rem;color:#6b7280;">No courses scheduled yet.</p>';
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

    scheduleListHome.appendChild(item);
  });
}
