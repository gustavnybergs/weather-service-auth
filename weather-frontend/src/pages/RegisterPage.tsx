import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiPost } from "../utils/api";
import { RegisterRequest } from "../types/types";

export default function RegisterPage() {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    async function handleRegister(e: React.FormEvent) {
        e.preventDefault();
        setError("");
        setSuccess("");
        setIsLoading(true);

        try {
            const body: RegisterRequest = { username, email, password };
            await apiPost("/api/auth/register", body);
            
            setSuccess("Registration successful! Redirecting to login...");
            
            // Navigera till login efter 2 sekunder
            setTimeout(() => {
                navigate("/login");
            }, 2000);
        } catch (err) {
            setError("Registration failed. Username or email might already exist.");
        } finally {
            setIsLoading(false);
        }
    }

    return (
        <div className="max-w-md mx-auto mt-10">
            <h2 className="text-2xl font-bold mb-6">Register</h2>
            
            <form onSubmit={handleRegister} className="space-y-4 bg-white p-6 rounded shadow">
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
                    <label className="block text-sm font-medium mb-1">Email</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
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
                        minLength={6}
                        className="border rounded p-2 w-full"
                    />
                </div>

                {error && <p className="text-red-600 text-sm">{error}</p>}
                {success && <p className="text-green-600 text-sm">{success}</p>}

                <button
                    type="submit"
                    disabled={isLoading}
                    className="w-full bg-blue-400 text-white py-2 rounded hover:bg-blue-600 disabled:bg-gray-400"
                >
                    {isLoading ? "Registering..." : "Register"}
                </button>

                <p className="text-sm text-center">
                    Already have an account?{" "}
                    <a href="/login" className="text-blue-400 hover:underline">
                        Login here
                    </a>
                </p>
            </form>
        </div>
    );
}
