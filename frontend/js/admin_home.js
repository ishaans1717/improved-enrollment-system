const API_URL = 'http://localhost:8080/api';

let courses = [];
let filteredCourses = [];

document.addEventListener("DOMContentLoaded", async () => {
  console.log('📚 Admin dashboard loading...');

  const token = localStorage.getItem('sessionToken');
  const userType = localStorage.getItem('userType');

  if (!token) {
    console.log('❌ No session token, redirecting to login');
    alert('Please login first');
    window.location.href = 'login.html';
    return;
  }

  if (userType !== 'ADMINISTRATOR' && userType !== 'ADVISOR') {
    console.log('❌ Not an admin/advisor account');
    alert('Access denied. This page is for administrators only.');
    window.location.href = 'login.html';
    return;
  }

  console.log('✅ Authentication valid, loading courses...');

  await loadCoursesFromBackend();
  setupFormSubmission();
  setupFilterButton();
});

async function refreshCourses() {
  console.log('🔄 Manual refresh triggered');
  await loadCoursesFromBackend();
}

window.refreshCourses = refreshCourses;

function setupFilterButton() {
  const filterBtn = document.querySelector('.search-row .main-btn');
  const filterInput = document.querySelector('.search-row .class-search');

  if (filterBtn && filterInput) {
    filterBtn.addEventListener('click', () => {
      filterCourses(filterInput.value);
    });

    filterInput.addEventListener('keyup', (e) => {
      if (e.key === 'Enter') {
        filterCourses(filterInput.value);
      }
    });

    filterInput.addEventListener('input', (e) => {
      if (e.target.value === '') {
        filteredCourses = [...courses];
        renderCourses();
      }
    });
  }
}

function filterCourses(query) {
  const searchTerm = query.trim().toLowerCase();

  if (!searchTerm) {
    filteredCourses = [...courses];
    renderCourses();
    return;
  }

  console.log('🔍 Filtering courses by:', searchTerm);

  filteredCourses = courses.filter(course => {
    const subject = (course.subject || '').toLowerCase();
    const code = (course.code || '').toLowerCase();
    const title = (course.title || '').toLowerCase();

    return subject.includes(searchTerm) ||
           code.includes(searchTerm) ||
           title.includes(searchTerm) ||
           (subject + ' ' + code).includes(searchTerm);
  });

  console.log('✅ Found', filteredCourses.length, 'matching courses');
  renderCourses();
}

async function loadCoursesFromBackend() {
  try {
    console.log('🔄 Fetching courses from backend...');

    const response = await fetch(`${API_URL}/courses`);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    courses = await response.json();
    filteredCourses = [...courses];
    console.log('✅ Loaded ' + courses.length + ' courses:', courses);

    renderCourses();

  } catch (error) {
    console.error('❌ Error loading courses:', error);

    const tbody = document.querySelector(".course-table tbody");
    tbody.innerHTML = `
      <tr>
        <td colspan="7" style="text-align: center; color: #ef4444; padding: 20px;">
          ⚠️ Error loading courses. Make sure the backend is running on http://localhost:8080
          <br><small>${error.message}</small>
        </td>
      </tr>
    `;

    document.querySelector(".card-meta").textContent = 'Failed to load courses';
  }
}

function renderCourses() {
  const tbody = document.querySelector(".course-table tbody");
  tbody.innerHTML = "";

  const coursesToRender = filteredCourses.length > 0 || courses.length === 0 ? filteredCourses : courses;

  if (coursesToRender.length === 0) {
    const filterInput = document.querySelector('.search-row .class-search');
    const hasFilter = filterInput && filterInput.value.trim() !== '';

    tbody.innerHTML = `
      <tr>
        <td colspan="7" style="text-align: center; padding: 20px; color: #6b7280;">
          ${hasFilter ? 'No courses match your filter. Try a different search term.' : 'No courses found. Add some courses using the form on the right.'}
        </td>
      </tr>
    `;
    document.querySelector(".card-meta").textContent = 'Showing 0 courses';
    return;
  }

  const sortedCourses = [...coursesToRender].sort((a, b) => {
    if (a.subject !== b.subject) {
      return (a.subject || '').localeCompare(b.subject || '');
    }
    const codeA = parseInt(a.code) || 0;
    const codeB = parseInt(b.code) || 0;
    return codeA - codeB;
  });

  sortedCourses.forEach(course => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${course.subject || 'IT'}</td>
      <td>${course.code}</td>
      <td>${course.title}</td>
      <td>${course.credits}</td>
      <td>${course.capacity || 30}</td>
      <td>-</td>
      <td>-</td>
    `;
    tbody.appendChild(tr);
  });

  const totalCourses = courses.length;
  const displayedCourses = coursesToRender.length;

  if (displayedCourses < totalCourses) {
    document.querySelector(".card-meta").textContent = `Showing ${displayedCourses} of ${totalCourses} courses`;
  } else {
    document.querySelector(".card-meta").textContent = `Showing ${displayedCourses} courses`;
  }

  console.log('✅ Rendered ' + coursesToRender.length + ' courses in table');
}

function setupFormSubmission() {
  const form = document.querySelector(".add-course-form");

  form.addEventListener("submit", async function(e) {
    e.preventDefault();

    const subject = document.getElementById("subject").value.trim();
    const code = document.getElementById("code").value.trim();
    const title = document.getElementById("title").value.trim();
    const credits = document.getElementById("credits").value.trim();
    const days = document.getElementById("days").value.trim();
    const time = document.getElementById("time").value.trim();
    const location = document.getElementById("location").value.trim();

    if (!subject || !code || !title || !credits) {
      alert('Please fill in all required fields (Subject, Code, Title, Credits)');
      return;
    }

    console.log('📝 Adding new course...');

    const newCourse = {
      subject: subject.toUpperCase(),
      code: code,
      title: title,
      credits: parseInt(credits),
      description: `${subject.toUpperCase()} ${code} - ${title}`,
      capacity: 30
    };

    try {
      const response = await fetch(`${API_URL}/courses?catalogId=1`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newCourse)
      });

      if (!response.ok) {
        if (response.status === 500 || response.status === 404) {
          console.log('📋 Creating default catalog...');
          await createDefaultCatalog();

          const retryResponse = await fetch(`${API_URL}/courses?catalogId=1`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify(newCourse)
          });

          if (!retryResponse.ok) {
            throw new Error('Failed to add course after creating catalog');
          }

          const addedCourse = await retryResponse.json();
          console.log('✅ Course added:', addedCourse);

        } else {
          const errorText = await response.text();
          throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
        }
      } else {
        const addedCourse = await response.json();
        console.log('✅ Course added:', addedCourse);
      }

      console.log('🔄 Reloading courses from database...');
      await loadCoursesFromBackend();

      form.reset();

      alert('✅ Course added successfully!');

    } catch (error) {
      console.error('❌ Error adding course:', error);
      alert('Error adding course: ' + error.message + '\n\nCheck browser console for details.');
    }
  });
}

async function createDefaultCatalog() {
  try {
    const catalog = {
      name: "Spring 2026 Catalog",
      term: "Spring",
      year: 2026
    };

    const response = await fetch(`${API_URL}/catalogs`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(catalog)
    });

    if (!response.ok) {
      throw new Error('Failed to create catalog');
    }

    console.log('✅ Default catalog created');

  } catch (error) {
    console.error('❌ Error creating catalog:', error);
    throw error;
  }
}