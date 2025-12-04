document.addEventListener('DOMContentLoaded', async () => {
  const token = await window.api.requireActiveSession();
  if (!token) return;

  await populateUserDetails(token);

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