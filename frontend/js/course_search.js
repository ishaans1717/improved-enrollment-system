document.addEventListener('DOMContentLoaded', async () => {
  const token = await window.api.requireActiveSession();
  if (!token) return;

  await populateUserDetails(token);
  await loadCourses();

  const logoutBtn = document.getElementById('logout-btn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', (event) => {
      event.preventDefault();
      window.api.logout();
    });
  }
});

async function populateUserDetails(token) {
  const nameElements = document.querySelectorAll('[data-user-name]');
  let displayName = window.api.getDisplayName('Student');

  try {
    const info = await window.api.fetchCurrentUser(token);
    if (info?.userType) {
      displayName = window.api.getDisplayName(info.userType);
    }
  } catch (error) {
    console.warn('Unable to load user details', error);
  }

  nameElements.forEach((element) => {
    element.textContent = displayName;
  });
}

async function loadCourses() {
  const resultsContainer = document.getElementById('course-results');
  const resultsCount = document.getElementById('results-count');
  const feedback = document.getElementById('course-feedback');
  const searchInput = document.querySelector('.class-search');
  const searchButton = document.getElementById('search-button');

  if (!resultsContainer || !resultsCount) return;

  let courses = [];

  const renderCourses = (list) => {
    resultsContainer.innerHTML = '';

    if (!list.length) {
      const emptyState = document.createElement('p');
      emptyState.textContent = 'No courses match your search right now.';
      emptyState.style.fontWeight = '600';
      resultsContainer.appendChild(emptyState);
      resultsCount.textContent = '0 Results';
      return;
    }

    list.forEach((course) => {
      const block = document.createElement('div');
      block.className = 'result-block';

      const logo = document.createElement('div');
      logo.className = 'logo-result';
      logo.textContent = course.code ? course.code.split(' ')[0] || 'CRS' : 'CRS';

      const info = document.createElement('div');
      info.className = 'class-result-info';

      const title = document.createElement('p');
      title.style.fontWeight = 'bold';
      title.textContent = course.code ? `${course.code} ${course.title || ''}`.trim() : course.title || 'Course';

      const description = document.createElement('p');
      description.className = 'date';
      description.style.fontSize = '13px';
      description.textContent = course.description || 'No description available yet.';

      const credits = document.createElement('p');
      credits.className = 'credit-amount';
      credits.style.fontSize = '13px';
      credits.style.fontWeight = '500';
      credits.textContent = `${course.credits || 0} Credits`;

      const addButton = document.createElement('button');
      addButton.className = 'add-to-cart-btn';
      addButton.style.background = '#ef4444';
      addButton.style.color = '#fff';
      addButton.style.border = 'none';
      addButton.style.borderRadius = '6px';
      addButton.style.padding = '6px 16px';
      addButton.style.fontWeight = '600';
      addButton.style.cursor = 'pointer';
      addButton.textContent = 'Add to Cart';
      addButton.disabled = true;
      addButton.title = 'Cart functionality coming soon';

      info.appendChild(title);
      info.appendChild(description);

      block.appendChild(logo);
      block.appendChild(info);
      block.appendChild(credits);
      block.appendChild(addButton);

      resultsContainer.appendChild(block);
    });

    resultsCount.textContent = `${list.length} Result${list.length === 1 ? '' : 's'}`;
  };

  const filterCourses = () => {
    const query = (searchInput?.value || '').toLowerCase();
    if (!query) {
      renderCourses(courses);
      return;
    }

    const filtered = courses.filter((course) => {
      return [course.title, course.code, course.description]
        .filter(Boolean)
        .some((field) => field.toLowerCase().includes(query));
    });

    renderCourses(filtered);
  };

  try {
    resultsCount.textContent = 'Loading courses...';
    courses = await window.api.fetchCourses();
    renderCourses(courses);
  } catch (error) {
    console.error('Failed to load courses', error);
    if (feedback) {
      feedback.textContent = error.message || 'Unable to load courses right now.';
      feedback.style.display = 'block';
    }
    resultsCount.textContent = '0 Results';
  }

  if (searchButton) {
    searchButton.addEventListener('click', filterCourses);
  }

  if (searchInput) {
    searchInput.addEventListener('input', filterCourses);
  }
}