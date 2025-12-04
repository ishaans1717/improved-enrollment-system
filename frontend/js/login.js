document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('login-form');
  const errorBox = document.getElementById('login-error');

  if (!form) return;

  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    if (errorBox) {
      errorBox.style.display = 'none';
      errorBox.textContent = '';
    }

    const email = form.email.value.trim();
    const password = form.password.value;

    if (!email || !password) {
      showError('Please enter both email and password.');
      return;
    }

    form.querySelector('button[type="submit"]').disabled = true;

    try {
      await window.api.login(email, password);
      window.location.href = 'home_page.html';
    } catch (error) {
      showError(error.message || 'Unable to login. Please try again.');
    } finally {
      form.querySelector('button[type="submit"]').disabled = false;
    }
  });

  function showError(message) {
    if (!errorBox) return;
    errorBox.textContent = message;
    errorBox.style.display = 'block';
  }
});