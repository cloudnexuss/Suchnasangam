function togglePassword() {
    const passwordInput = document.getElementById("password");
    const eyeIcon = document.getElementById("eyeIcon");

    if (passwordInput.type === "password") {
        passwordInput.type = "text";
        eyeIcon.textContent = "🙈"; // Change to "hide" icon
        eyeIcon.style.transform = "rotate(180deg)"; // Animate the icon
    } else {
        passwordInput.type = "password";
        eyeIcon.textContent = "👁"; // Change to "show" icon
        eyeIcon.style.transform = "rotate(0deg)"; // Reset animation
    }
}



