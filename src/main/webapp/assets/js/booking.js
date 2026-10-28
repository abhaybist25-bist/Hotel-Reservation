/**
 * The Grand Azure Hotel & Suites - Dynamic Booking and Date Calculation Script
 */

document.addEventListener('DOMContentLoaded', function () {
    const checkInInput = document.getElementById('bookingCheckIn');
    const checkOutInput = document.getElementById('bookingCheckOut');
    const nightsDisplay = document.getElementById('displayNights');
    const subtotalDisplay = document.getElementById('displaySubtotal');
    const pricePerNightInput = document.getElementById('roomPricePerNight');
    const guestInput = document.getElementById('bookingGuests');
    const maxCapacityInput = document.getElementById('roomMaxCapacity');

    // Establish minimum allowed dates
    const today = new Date().toISOString().split('T')[0];

    if (checkInInput) {
        if (!checkInInput.min) {
            checkInInput.min = today;
        }

        checkInInput.addEventListener('change', function () {
            if (this.value) {
                // Minimum check-out is checkIn + 1 day
                const inDate = new Date(this.value);
                inDate.setDate(inDate.getDate() + 1);
                const minOut = inDate.toISOString().split('T')[0];

                if (checkOutInput) {
                    checkOutInput.min = minOut;
                    if (checkOutInput.value && checkOutInput.value <= this.value) {
                        checkOutInput.value = minOut;
                    }
                }
            }
            updateCostEstimation();
        });
    }

    if (checkOutInput) {
        checkOutInput.addEventListener('change', updateCostEstimation);
    }

    function updateCostEstimation() {
        if (!checkInInput || !checkOutInput || !checkInInput.value || !checkOutInput.value) {
            return;
        }

        const start = new Date(checkInInput.value);
        const end = new Date(checkOutInput.value);

        if (end <= start) {
            return;
        }

        const diffTime = Math.abs(end - start);
        const nights = Math.max(1, Math.ceil(diffTime / (1000 * 60 * 60 * 24)));

        if (nightsDisplay) {
            nightsDisplay.textContent = nights + (nights === 1 ? ' night' : ' nights');
        }

        if (pricePerNightInput && subtotalDisplay) {
            const price = parseFloat(pricePerNightInput.value);
            if (!isNaN(price)) {
                const subtotal = (price * nights).toFixed(2);
                subtotalDisplay.textContent = '$' + Number(subtotal).toLocaleString(undefined, { minimumFractionDigits: 2 });
            }
        }
    }

    // Capacity client check
    const bookingForm = document.getElementById('roomBookingForm');
    if (bookingForm && guestInput && maxCapacityInput) {
        bookingForm.addEventListener('submit', function (e) {
            const guests = parseInt(guestInput.value, 10);
            const maxCap = parseInt(maxCapacityInput.value, 10);

            if (guests > maxCap) {
                e.preventDefault();
                alert('The number of guests (' + guests + ') exceeds the room capacity of ' + maxCap + '. Please select another room or adjust guest count.');
                guestInput.focus();
            }
        });
    }

    // Trigger initial calculation
    updateCostEstimation();
});

