const API_BASE_URL = '/api';

function getStoredSession() {
  try {
    const raw = localStorage.getItem('session');
    return raw ? JSON.parse(raw) : {};
  } catch (error) {
    console.error('Unable to read session from storage', error);
    return {};
  }
}

function storeSession({ token, username, userId }) {
  const session = { token, username, userId };
  localStorage.setItem('session', JSON.stringify(session));
}

function clearSession() {
  localStorage.removeItem('session');
}

function redirectToLogin() {
  const loginPath = window.location.pathname.includes('frontend/pages')
    ? 'login.html'
    : '/frontend/pages/login.html';
  window.location.href = loginPath;
}

async function apiRequest(path, options = {}) {
  const { method = 'GET', headers = {}, body = null, query } = options;
  const url = new URL(`${API_BASE_URL}${path}`, window.location.origin);

  if (query) {
    Object.entries(query)
      .filter(([, value]) => value !== undefined && value !== null)
      .forEach(([key, value]) => url.searchParams.append(key, value));
  }

  const response = await fetch(url.toString(), {
    method,
    headers: {
      ...(body ? { 'Content-Type': 'application/json' } : {}),
      ...headers,
    },
    body: body ? JSON.stringify(body) : undefined,
  });

  const contentType = response.headers.get('Content-Type') || '';
  let data = null;
  if (contentType.includes('application/json')) {
    data = await response.json();
  } else {
    data = await response.text();
  }

  if (!response.ok) {
    const message = typeof data === 'string' ? data : data?.message || 'Request failed';
    throw new Error(message);
  }

  return data;
}

async function login(email, password) {
  const result = await apiRequest('/auth/login', {
    method: 'POST',
    body: { email, password },
  });

  if (!result.success || !result.sessionToken) {
    throw new Error(result.message || 'Login failed');
  }

  storeSession({
    token: result.sessionToken,
    username: result.username,
    userId: result.userId,
  });

  return result;
}

async function logout() {
  const { token } = getStoredSession();
  if (!token) return;

  try {
    await apiRequest('/auth/logout', {
      method: 'POST',
      body: { token },
    });
  } finally {
    clearSession();
    redirectToLogin();
  }
}

async function isSessionActive() {
  const { token } = getStoredSession();
  if (!token) return false;

  try {
    return await apiRequest('/auth/check', { query: { token } });
  } catch (error) {
    console.warn('Unable to verify session', error);
    return false;
  }
}

async function requireActiveSession() {
  const { token } = getStoredSession();

  if (!token) {
    redirectToLogin();
    return null;
  }

  const active = await isSessionActive();
  if (!active) {
    clearSession();
    redirectToLogin();
    return null;
  }

  return token;
}

async function fetchCurrentUser(token) {
  return apiRequest('/auth/me', { query: { token } });
}

async function fetchCourses() {
  return apiRequest('/courses');
}

function getDisplayName(fallback) {
  const { username } = getStoredSession();
  return username || fallback || 'User';
}

window.api = {
  apiRequest,
  clearSession,
  fetchCourses,
  fetchCurrentUser,
  getDisplayName,
  getStoredSession,
  isSessionActive,
  login,
  logout,
  redirectToLogin,
  requireActiveSession,
  storeSession,
};