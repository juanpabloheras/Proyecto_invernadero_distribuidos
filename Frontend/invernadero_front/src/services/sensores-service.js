import { apiRequest } from './Api'

export function getSensores(params = {}) {
  const query = new URLSearchParams(params).toString()
  const path = query ? `/api/sensores?${query}` : '/api/sensores'

  return apiRequest(path)
}

export function addSensor(data) {
    return apiRequest('/api/sensores', {
        method: 'POST',
        body: JSON.stringify(data),
    })
}

export function deleteSensor(id) {
    return apiRequest('/api/sensores/' + id, {
        method: 'DELETE',
    })
}


