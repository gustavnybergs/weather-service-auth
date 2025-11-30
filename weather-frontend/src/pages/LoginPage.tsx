import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiPost } from "../utils/api";
import { AuthResponse, LoginRequest } from "../types/types";

export default function LoginPage() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    async function handleLogin(e: React.FormEvent) {
        e.preventDefault();
        setError("");
        setIsLoading(true);

        try {
            const body: LoginRequest = { username, password };
            const res = await apiPost<AuthResponse>("/api/auth/login", body);
            
            // Spara JWT token
            localStorage.setItem("jwt_token", res.token);
            localStorage.setItem("username", res.user.username);
            
            // Navigera till startsidan
            navigate("/");
        } catch (err) {
            setError("Login failed. Check your credentials.");
        } finally {
            setIsLoading(false);
        }
    }

    return (
        <div className="max-w-md mx-auto mt-10">
            <h2 className="text-2xl font-bold mb-6">Login</h2>
            
            <form onSubmit={handleLogin} className="space-y-4 bg-white p-6 rounded shadow">
                <div>
                    <label className="block text-sm font-medium mb-1">Username</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                        className="border rounded p-2 w-full"
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium mb-1">Password</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        className="border rounded p-2 w-full"
                    />
                </div>

                {error && <p className="text-red-600 text-sm">{error}</p>}

                <button
                    type="submit"
                    disabled={isLoading}
                    className="w-full bg-blue-400 text-white py-2 rounded hover:bg-blue-600 disabled:bg-gray-400"
                >
                    {isLoading ? "Logging in..." : "Login"}
                </button>

                <p className="text-sm text-center">
                    Don't have an account?{" "}
                    <a href="/register" className="text-blue-400 hover:underline">
                        Register here
                    </a>
                </p>
            </form>
        </div>
    );
}
