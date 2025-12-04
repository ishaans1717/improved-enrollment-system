let courses = [
  { subject: "IT", code: "326", title: "Principles of Software Engineering", credits: 3, days: "T TH", time: "2:00 PM – 3:15 PM", location: "STV 240" },
  { subject: "IT", code: "383", title: "Principles of Operating Systems", credits: 3, days: "M W", time: "3:35 PM – 4:50 PM", location: "STV 108" },
  { subject: "IT", code: "355", title: "Secure Software Development", credits: 3, days: "T TH", time: "2:00 PM – 3:15 PM", location: "ITC 101" },
  { subject: "ACC", code: "131", title: "Financial Accounting", credits: 3, days: "M W", time: "11:00 AM – 12:15 PM", location: "CTK 201" },
  { subject: "MAT", code: "145", title: "Calculus I", credits: 4, days: "M W F", time: "9:00 AM – 9:50 AM", location: "STV 101" },
  { subject: "MAT", code: "175", title: "Discrete Mathematics", credits: 3, days: "T TH", time: "12:30 PM – 1:45 PM", location: "STV 215" }
];

function renderCourses() {
  const tbody = document.querySelector(".course-table tbody");
  tbody.innerHTML = "";
  courses.forEach(course => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${course.subject}</td>
      <td>${course.code}</td>
      <td>${course.title}</td>
      <td>${course.credits}</td>
      <td>${course.days}</td>
      <td>${course.time}</td>
      <td>${course.location}</td>
    `;
    tbody.appendChild(tr);
  });
  document.querySelector(".card-meta").textContent = `Showing ${courses.length} courses`;
}

document.addEventListener("DOMContentLoaded", () => {
  renderCourses();
  document.querySelector(".add-course-form").addEventListener("submit", function(e) {
    e.preventDefault();
    const subject = document.getElementById("subject").value.trim();
    const code = document.getElementById("code").value.trim();
    const title = document.getElementById("title").value.trim();
    const credits = document.getElementById("credits").value.trim();
    const days = document.getElementById("days").value.trim();
    const time = document.getElementById("time").value.trim();
    const location = document.getElementById("location").value.trim();
    if (subject && code && title && credits && days && time && location) {
      courses.push({ subject, code, title, credits, days, time, location });
      renderCourses();
      this.reset();
    }
  });
});
