import { apiRequest } from './api.js'
import { auth } from './firebase.js'


export async function obtenerUsuarioActual() {
  return apiRequest('/api/usuarios/me', {
    method: 'GET'
  })
}


export function loginUsuario(data) {
  return apiRequest('/api/usuarios/login', {
    method: 'POST',
    body: JSON.stringify(data)
  })
}
