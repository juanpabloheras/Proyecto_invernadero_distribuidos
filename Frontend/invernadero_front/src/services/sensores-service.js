import { apiRequest } from './api'

export function getSensores(params = {}) {
  const query = new URLSearchParams(params).toString()
  const path = query ? `/api/sensores?${query}` : '/api/sensores'

  return apiRequest(path)
}
