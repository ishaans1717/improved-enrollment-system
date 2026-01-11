document.addEventListener('DOMContentLoaded', function() {
  const editBtn = document.getElementById('editBtn');
  const accountForm = document.getElementById('accountForm');
  const details = document.querySelector('.account-details');
  const cancelBtn = document.getElementById('cancelBtn');

  editBtn.onclick = function() {
    accountForm.style.display = 'flex';
    details.style.display = 'none';
    editBtn.style.display = 'none';
  };

  cancelBtn.onclick = function() {
    accountForm.style.display = 'none';
    details.style.display = 'block';
    editBtn.style.display = 'block';
  };
// Changing student details
  accountForm.onsubmit = function(e) {
    e.preventDefault();
    document.getElementById('detail-name').textContent = accountForm['full-name'].value;
    document.getElementById('detail-dob').textContent = new Date(accountForm['dob'].value).toLocaleDateString('en-US', {year:'numeric', month:'short', day:'numeric'});
    document.getElementById('detail-email').textContent = accountForm['email'].value;
    document.getElementById('detail-major').textContent = accountForm['major'].value;
    accountForm.style.display = 'none';
    details.style.display = 'block';
    editBtn.style.display = 'block';
    alert('Account details updated!');
  };
});
