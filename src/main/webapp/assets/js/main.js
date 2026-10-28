/**
 * The Grand Azure Hotel & Suites - Primary Application JavaScript
 */

document.addEventListener('DOMContentLoaded', function () {
    // Mobile navigation toggle
    const navToggle = document.getElementById('navToggle');
    const navLinks = document.getElementById('navLinks');

    if (navToggle && navLinks) {
        navToggle.addEventListener('click', function () {
            navLinks.classList.toggle('open');
            const expanded = navLinks.classList.contains('open');
            navToggle.setAttribute('aria-expanded', expanded);
        });
    }

    // Auto-dismiss flash alerts after 6 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(function () {
                alert.remove();
            }, 500);
        }, 6000);

        const closeBtn = alert.querySelector('.alert-dismiss');
        if (closeBtn) {
            closeBtn.addEventListener('click', function () {
                alert.remove();
            });
        }
    });

    // Password confirmation match validator for registration form
    const regForm = document.getElementById('registrationForm');
    if (regForm) {
        regForm.addEventListener('submit', function (e) {
            const pwd = document.getElementById('regPassword');
            const confirm = document.getElementById('regConfirmPassword');
            const errorDiv = document.getElementById('passwordMismatchError');

            if (pwd && confirm && pwd.value !== confirm.value) {
                e.preventDefault();
                if (errorDiv) {
                    errorDiv.textContent = 'Passwords do not match.';
                    errorDiv.style.display = 'block';
                } else {
                    alert('Passwords do not match.');
                }
                confirm.focus();
            }
        });
    }
});

