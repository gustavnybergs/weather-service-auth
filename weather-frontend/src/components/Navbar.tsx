import { Link, useNavigate } from "react-router-dom";

export default function Navbar() {
    const navigate = useNavigate();
    const isLoggedIn = !!localStorage.getItem('jwt_token');
    const username = localStorage.getItem('username');

    function handleLogout() {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('username');
        navigate('/login');
    }

    return (
        <nav className="bg-yellow-200 text-blue-500 p-4 flex justify-between items-center mb-6 rounded">
            <div className="flex gap-10">
                <Link to="/" className="hover:underline">Home</Link>
                <Link to="/favorites" className="hover:underline">Favorites</Link>
                <Link to="/forecast" className="hover:underline">Forecast</Link>
            </div>
            
            <div className="flex gap-4 items-center">
                {isLoggedIn ? (
                    <>
                        <span className="text-sm">👤 {username}</span>
                        <button 
                            onClick={handleLogout}
                            className="px-3 py-1 bg-red-400 text-white rounded hover:bg-red-600"
                        >
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        <Link to="/login" className="px-3 py-1 bg-blue-400 text-white rounded hover:bg-blue-600">
                            Login
                        </Link>
                        <Link to="/register" className="px-3 py-1 bg-green-400 text-white rounded hover:bg-green-600">
                            Register
                        </Link>
                    </>
                )}
            </div>
        </nav>
    );
}
