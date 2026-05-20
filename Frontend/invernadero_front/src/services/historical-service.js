const HISTORICAL_API_URL =
  import.meta.env.VITE_HISTORICAL_URL ||
  import.meta.env.VITE_ANALITICA_URL ||
  'http://localhost:8085'

async function historicalRequest(path, options = {}) {
  const response = await fetch(`${HISTORICAL_API_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    },
    ...options
  })

  const data = await response.json()

  if (!response.ok) {
    throw new Error(data.message || 'Error al consultar datos historicos')
  }

  return data
}

export function getLecturas(params = {}) {
  const query = new URLSearchParams(params).toString()
  const path = query ? `/api/lecturas?${query}` : '/api/lecturas'

  return historicalRequest(path)
}

export function getLecturasPorFechas(params = {}) {
  const query = new URLSearchParams(params).toString()
  const path = query ? `/api/lecturas/fechas?${query}` : '/api/lecturas/fechas'

  return historicalRequest(path)
}

export function getLecturasPorSensor(sensorId, params = {}) {
  const query = new URLSearchParams(params).toString()
  const path = query
    ? `/api/lecturas/sensor/${sensorId}?${query}`
    : `/api/lecturas/sensor/${sensorId}`

  return historicalRequest(path)
}
