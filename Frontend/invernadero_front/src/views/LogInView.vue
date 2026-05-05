<template>
  <main class="login-page">
    <div class="bg-layer" />

    <section class="form-panel">
      <div class="login-card">
        <h2>GreenMonitor</h2>

        <form @submit.prevent="handleLogin">
          <div class="field-group">
            <label>EMAIL ADDRESS</label>
            <div class="input-wrap">
              <input
                v-model="email"
                type="email"
                autocomplete="email"
              />
              <span class="input-icon">@</span>
            </div>
          </div>

          <div class="field-group">
            <label>PASSWORD</label>
            <div class="input-wrap">
              <input
                v-model="password"
                type="password"
                autocomplete="current-password"
              />
              
            </div>
          </div>

        
          <button type="submit" class="btn-submit">
            Login
          </button>
        </form>
        <p v-if="error" class="error">{{ error }}</p>
        <p class="help">
          Personal autorizado, ¿necesitas acceso?
          <a href="#">Contáctanos!</a>
        </p>
      </div>

    </section>
  </main>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { loginUsuario } from '../services/usuarios-service.js'

const router = useRouter()
const email = ref('')
const password = ref('')
const remember = ref(false)
const error = ref('')

async function handleLogin() {
  error.value = ''

  if (!email.value || !password.value) {
    error.value = 'Completa todos los campos'
    return
  }

  try {
    const response = await loginUsuario({
      correo: email.value,
      contrasenia: password.value
    })

    localStorage.setItem('currentUser', JSON.stringify(response.data))
    router.push('/home')
  } catch (err) {
    error.value = err.message
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.login-page {
  width: 100vw;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* Background with greenhouse photo + green overlay */
.bg-layer {
  position: fixed;
  inset: 0;
  background:
    white;
  z-index: 0;
}

.form-panel {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  min-height: 100vh;
  justify-content: center;
  padding: 40px 16px;
  gap: 32px;
}

/* Card */
.login-card {
  width: min(100%, 460px);
  background: rgba(255, 255, 255, 0.97);
  border-radius: 24px;
  padding: 44px 44px 36px;
  box-shadow:
    0 32px 80px rgba(0, 0, 0, 0.28),
    0 0 0 1px rgba(255,255,255,0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: slideUp 0.45s cubic-bezier(0.16, 1, 0.3, 1) both;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(24px); }
  to   { opacity: 1; transform: translateY(0); }
}

h2 {
  margin: 0 0 28px;
  font-size: 32px;
  font-weight: 700;
  color: #111827;
  letter-spacing: 0;
}

.subtitle {
  margin: 0 0 32px;
  font-size: 13px;
  color: #6b7280;
  text-align: center;
}

/* Form */
form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 0;
}

.field-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
}

label {
  font-size: 10.5px;
  font-weight: 700;
  letter-spacing: 1px;
  color: #9ca3af;
  margin-bottom: 7px;
}

.input-wrap {
  position: relative;
}

input[type="email"],
input[type="password"],
input[type="text"] {
  width: 100%;
  padding: 13px 42px 13px 16px;
  border-radius: 10px;
  border: 1.5px solid #e5e7eb;
  background: #f9fafb;
  font-size: 14.5px;
  font-family: inherit;
  color: #111827;
  outline: none;
  transition: border-color 0.18s, box-shadow 0.18s, background 0.18s;
}

input[type="email"]:focus,
input[type="password"]:focus,
input[type="text"]:focus {
  border-color: #22c55e;
  background: #ffffff;
  box-shadow: 0 0 0 4px rgba(34, 197, 94, 0.12);
}

input::placeholder {
  color: #d1d5db;
}

.input-icon {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
  font-size: 14px;
  pointer-events: none;
  line-height: 1;
}

.btn-eye {
  pointer-events: all;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  font-size: 14px;
  line-height: 1;
}

/* Extras row */
.row-extras {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  margin-top: 4px;
}

.remember {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #374151;
  cursor: pointer;
  letter-spacing: 0;
  font-weight: 400;
}

.remember input[type="checkbox"] {
  display: none;
}

.check-box {
  width: 17px;
  height: 17px;
  border-radius: 5px;
  border: 1.5px solid #d1d5db;
  background: #f9fafb;
  display: grid;
  place-items: center;
  transition: background 0.15s, border-color 0.15s;
  flex-shrink: 0;
}

.check-box.checked {
  background: #16a34a;
  border-color: #16a34a;
}

.forgot {
  font-size: 13px;
  color: #16a34a;
  font-weight: 600;
  text-decoration: none;
}

.forgot:hover {
  text-decoration: underline;
}

/* Submit */
.btn-submit {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 12px;
  background: #16a34a;
  color: white;
  font-weight: 700;
  font-size: 15px;
  font-family: inherit;
  cursor: pointer;
  letter-spacing: 0.2px;
  transition: background 0.18s, transform 0.12s, box-shadow 0.18s;
  box-shadow: 0 4px 18px rgba(22, 163, 74, 0.35);
}

.btn-submit:hover {
  background: #15803d;
  box-shadow: 0 6px 24px rgba(22, 163, 74, 0.45);
  transform: translateY(-1px);
}

.btn-submit:active {
  transform: translateY(0);
}

/* Error */
.error {
  margin-top: 16px;
  color: #dc2626;
  font-size: 13px;
  font-weight: 600;
  text-align: center;
}

/* Help */
.help {
  margin-top: 22px;
  text-align: center;
  color: #9ca3af;
  font-size: 12.5px;
}

.help a {
  color: #16a34a;
  font-weight: 700;
  text-decoration: none;
}

.help a:hover {
  text-decoration: underline;
}

/* Footer */
.page-footer {
  font-size: 11px;
  letter-spacing: 2px;
  color: rgba(255, 255, 255, 0.6);
  text-transform: uppercase;
}

/* Mobile */
@media (max-width: 520px) {
  .login-card {
    padding: 32px 24px 28px;
  }
}
</style>
