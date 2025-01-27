// Functionality for alert notification
document.querySelector('.alert-notification').addEventListener('click', () => {
    alert('This is an alert notification!');
});

// Buttons functionality
document.querySelectorAll('.button').forEach(button => {
    button.addEventListener('click', () => {
        alert(`You clicked on ${button.textContent}`);
    });
});
