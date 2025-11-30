const API_BASE = "http://localhost:8080";

async function handle<T>(res: Response): Promise<T> {
    if (!res.ok) {
        const msg = `API error: ${res.status}`;
        throw new Error(msg);
    }
    return res.json();
}

function getHeaders(): HeadersInit {
    const headers: HeadersInit = {};
    const token = localStorage.getItem('jwt_token');
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

export async function apiGet<T>(path: string): Promise<T> {
    const res = await fetch(`${API_BASE}${path}`, {
        headers: getHeaders()
    });
    return handle<T>(res);
}

export async function apiPost<T>(path: string, body: any): Promise<T> {
    const res = await fetch(`${API_BASE}${path}`, {
        method: "POST",
        headers: {
            ...getHeaders(),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body)
    });
    return handle<T>(res);
}

export async function apiPut<T>(path: string): Promise<T> {
    const res = await fetch(`${API_BASE}${path}`, {
        method: "PUT",
        headers: getHeaders()
    });
    return handle<T>(res);
}

export async function apiDelete<T>(path: string): Promise<T> {
    const res = await fetch(`${API_BASE}${path}`, {
        method: "DELETE",
        headers: getHeaders()
    });
    return handle<T>(res);
}
