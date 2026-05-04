const API_URL = 'http://localhost:3000';

export async function apiRequest(path, options = {}) {
    const response = await fetch(`${API_URL}${path}`, {
        headers: {
            'Content-Type': 'application/json', 
            ...options.headers,
        },
        ...options,
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || 'Error en la petición');
    }
    return data;
}
