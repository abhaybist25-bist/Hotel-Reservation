/**
 * The Grand Azure Hotel & Suites - Administrator Portal Script
 */

document.addEventListener('DOMContentLoaded', function () {
    // Delete room confirmation
    const deleteForms = document.querySelectorAll('.delete-room-form');
    deleteForms.forEach(function (form) {
        form.addEventListener('submit', function (e) {
            const roomNumber = this.getAttribute('data-room-number') || 'this room';
            const confirmed = confirm('Are you sure you want to permanently delete Room ' + roomNumber + '?\n\n' +
                'Note: If this room has historical reservations, deletion will be blocked to maintain data integrity. Deactivate the room instead if you wish to retain booking history.');
            if (!confirmed) {
                e.preventDefault();
            }
        });
    });

    // Cancel reservation confirmation
    const cancelForms = document.querySelectorAll('.admin-cancel-booking-form');
    cancelForms.forEach(function (form) {
        form.addEventListener('submit', function (e) {
            const bookingNumber = this.getAttribute('data-booking-number') || 'this reservation';
            const confirmed = confirm('Are you sure you want to cancel booking #' + bookingNumber + '?\n\nThis will immediately release the room for other guests.');
            if (!confirmed) {
                e.preventDefault();
            }
        });
    });
});

