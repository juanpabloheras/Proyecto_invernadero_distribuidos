import { auth } from './firebase.js'

const API_URL = 'http://localhost:3000';

export async function apiRequest(path, options = {}) {

    const user = auth.currentUser

    let token = null

    if (user) {
        token = await user.getIdToken()
    }

    const response = await fetch(`${API_URL}${path}`, {
        headers: {
            'Content-Type': 'application/json',

            ...(token && {
                Authorization: `Bearer ${token}`
            }),

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