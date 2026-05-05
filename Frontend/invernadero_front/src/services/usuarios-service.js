import { apiRequest } from './api.js'

export function loginUsuario(data) {
  return apiRequest('/api/usuarios/login', {
    method: 'POST',
    body: JSON.stringify(data)
  })
}
