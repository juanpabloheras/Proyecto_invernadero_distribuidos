import { apiRequest } from './api.js'

export function getAlarmas(params = {}) {
  const query = new URLSearchParams(params).toString()
  const path = query
    ? `/api/configuraciones-alarma?${query}`
    : '/api/configuraciones-alarma'

  return apiRequest(path)
}

export function addAlarma(data) {
  return apiRequest('/api/configuraciones-alarma', {
    method: 'POST',
    body: JSON.stringify(data)
  })
}

export function deleteAlarma(id) {
  return apiRequest(`/api/configuraciones-alarma/${id}`, {
    method: 'DELETE'
  })
}
