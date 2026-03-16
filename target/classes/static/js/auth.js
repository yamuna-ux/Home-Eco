/* ================= CONFIG ================= */
const API = "http://localhost:8081/api/auth";
let timer = 50;
let interval;

/* ================= REGISTER ================= */
function register() {
  const email = document.getElementById("email").value;
  const pass = document.getElementById("password").value;
  const confirm = document.getElementById("confirm").value;

  if (!email || !pass || !confirm) {
    alert("All fields are required");
    return;
  }

  if (pass !== confirm) {
    alert("Passwords do not match");
    return;
  }

  fetch(API + "/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password: pass })
  })
    .then(res => res.text())
    .then(msg => {
      alert(msg);
      if (msg.toLowerCase().includes("success")) {
        window.location.href = "login.html";
      }
    })
    .catch(() => alert("Registration failed"));
}

/* ================= LOGIN ================= */
function login() {
  const email = document.getElementById("email").value;
  const pass = document.getElementById("password").value;

  if (!email || !pass) {
    alert("Email and password required");
    return;
  }

  fetch(API + "/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password: pass })
  })
    .then(res => res.text())
    .then(msg => {
      alert(msg);
      if (!msg.toLowerCase().includes("invalid")) {
        window.location.href = "dashboard.html";
      }
    })
    .catch(() => alert("Login failed"));
}

/* ================= SEND OTP ================= */
function sendOTP() {
  const email = document.getElementById("email").value;
  

  if (!email) {
    alert("Enter email");
    return;
  }

  fetch(`${API}/send-otp?email=${email}`, {
    method: "POST"
  })
    .then(res => res.text())
    .then(msg => {
      alert(msg);
      localStorage.setItem("resetEmail", email);
      startTimer();
      window.location.href = "forgot-otp.html";
    })
    .catch(() => alert("Failed to send OTP"));
}


/* ================= TIMER ================= */
function startTimer() {
  timer = 50;
  const resendBtn = document.getElementById("resendBtn");
  const timerText = document.getElementById("timer");

  if (!resendBtn || !timerText) return;

  resendBtn.disabled = true;

  interval = setInterval(() => {
    timerText.innerText = `Resend OTP in ${timer}s`;
    timer--;

    if (timer < 0) {
      clearInterval(interval);
      timerText.innerText = "";
      resendBtn.disabled = false;
    }
  }, 1000);
}

function resendOTP() {
  sendOTP();
}

/* ================= VERIFY OTP ================= */
function verifyOTP() {
  
 const otpInput = document.getElementById("otp");
const otp = otpInput ? otpInput.value : otp;

  const email = localStorage.getItem("resetEmail");

  if (!otp) {
    alert("Enter OTP");
    return;
  }

  fetch(`${API}/verify-otp?email=${email}&otp=${otp}`, {
    method: "POST"
  })
    .then(res => res.text())
    .then(msg => {
      alert(msg);
      if (msg.toLowerCase().includes("verified")) {
        window.location.href = "reset-password.html";
      }
    })
    .catch(() => alert("OTP verification failed"));
}

/* ================= RESET PASSWORD ================= */
const API_RESET = "http://localhost:8081/api/auth/reset-password";

function resetPasswordAPI(password) {
    const email = localStorage.getItem("resetEmail");

    const msg = document.getElementById("msg");

    if (!email) {
        msg.textContent = "Session expired. Please retry.";
        msg.style.color = "red";
        return;
    }

    fetch(API_RESET, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            email: email,
            password: password
        })
    })
    .then(res => res.text())
    .then(data => {
        if (data.toLowerCase().includes("successful")) {
            msg.style.color = "lightgreen";
            msg.textContent = "Password reset successful ✅";

            // ✅ clear session
            localStorage.removeItem("resetEmail");

            // ✅ redirect to login
            setTimeout(() => {
                window.location.href = "login.html";
            }, 1200);
        } else {
            msg.style.color = "red";
            msg.textContent = data;
        }
    })
    .catch(() => {
        msg.style.color = "red";
        msg.textContent = "Server error";
    });
}


/* ================= AUTO START TIMER ================= */
if (window.location.pathname.includes("forgot-otp.html")) {
  startTimer();
}
