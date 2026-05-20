import { apiRequest } from './api.js'
import { auth } from './firebase.js'


export async function obtenerUsuarioActual() {
  const token = await auth.currentUser?.getIdToken()
  if (!token) {
    throw new Error('No se ha iniciado sesión')
  }
  
  return apiRequest('/api/usuarios/me', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
}

export function loginUsuario(data) {
  return apiRequest('/api/usuarios/login', {
    method: 'POST',
    body: JSON.stringify(data)
  })
}
