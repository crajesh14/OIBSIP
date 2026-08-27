document.getElementById('convert-btn').addEventListener('click', function () {
    const degreesInput = document.getElementById('degrees').value.trim();
    const unit = document.getElementById('unit').value;
    const errorMsg = document.getElementById('error-msg');
    
    const resCelsius = document.getElementById('result-celsius');
    const resFahrenheit = document.getElementById('result-fahrenheit');
    const resKelvin = document.getElementById('result-kelvin');

    // Reset UI state
    errorMsg.classList.add('hidden');
    errorMsg.textContent = '';

    // 1. Input Validation: Check if empty or non-numeric
    if (degreesInput === '' || isNaN(degreesInput)) {
        showError('Please enter a valid numeric temperature value.');
        return;
    }

    const tempVal = parseFloat(degreesInput);
    let c, f, k;

    // 2. Perform conversions based on selected input unit
    if (unit === 'celsius') {
        c = tempVal;
        f = (tempVal * 9 / 5) + 32;
        k = tempVal + 273.15;
    } else if (unit === 'fahrenheit') {
        c = (tempVal - 32) * 5 / 9;
        f = tempVal;
        k = c + 273.15;
    } else if (unit === 'kelvin') {
        c = tempVal - 273.15;
        f = (c * 9 / 5) + 32;
        k = tempVal;
    }

    // 3. Edge Case Handling: Absolute Zero Check (-273.15 °C or 0 K)
    if (k < 0) {
        showError('Temperature cannot be below absolute zero (-273.15°C / 0 K).');
        return;
    }

    // 4. Update Result Display (rounded to 2 decimal places)
    resCelsius.textContent = `${c.toFixed(2)} °C`;
    resFahrenheit.textContent = `${f.toFixed(2)} °F`;
    resKelvin.textContent = `${k.toFixed(2)} K`;
});

function showError(message) {
    const errorMsg = document.getElementById('error-msg');
    errorMsg.textContent = message;
    errorMsg.classList.remove('hidden');

    document.getElementById('result-celsius').textContent = '--';
    document.getElementById('result-fahrenheit').textContent = '--';
    document.getElementById('result-kelvin').textContent = '--';
}
