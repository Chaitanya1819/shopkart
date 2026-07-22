import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Login.css';

function Login() {

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errors, setErrors] = useState({});
    const [serverError, setServerError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const validate = () => {
        const newErrors = {};

        if (!email) {
            newErrors.email = 'Email is required';
        } else if (!email.includes('@')) {
            newErrors.email = 'Enter a valid email';
        }

        if (!password) {
            newErrors.password = 'Password is required';
        } else if (password.length < 6) {
            newErrors.password = 'Password must be at least 6 characters';
        }

        return newErrors;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setServerError('');

        const foundErrors = validate();
        if (Object.keys(foundErrors).length > 0) {
            setErrors(foundErrors);
            return;
        }

        setLoading(true);
        try {
            const response = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });

            const data = await response.json();

            if (response.ok) {
                localStorage.setItem('token', data.token);
                localStorage.setItem('userEmail', data.email);
                localStorage.setItem('userName', data.name);
                localStorage.setItem('userRole', data.role);
                navigate('/products');
            } else {
                setServerError(data.message || 'Invalid email or password');
            }

        } catch (error) {
            setServerError('Cannot connect to server. Make sure clothwear service is running on port 8080.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">

            <h2 className="login-title">ShopKart Login</h2>

            {serverError && (
                <div className="login-server-error">
                    {serverError}
                </div>
            )}

            <form onSubmit={handleSubmit}>

                <div className="login-field">
                    <label className="login-label">Email</label>
                    <input
                        type="text"
                        className="login-input"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="chaitanya@test.com"
                    />
                    {errors.email && (
                        <p className="login-field-error">{errors.email}</p>
                    )}
                </div>

                <div className="login-field">
                    <label className="login-label">Password</label>
                    <input
                        type="password"
                        className="login-input"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Your password"
                    />
                    {errors.password && (
                        <p className="login-field-error">{errors.password}</p>
                    )}
                </div>

                <button
                    type="submit"
                    className="login-btn"
                    disabled={loading}
                >
                    {loading ? 'Logging in...' : 'Login'}
                </button>

            </form>

            <p className="login-switch">
                Don't have an account?{' '}
                <span
                    className="login-link"
                    onClick={() => navigate('/register')}
                >
                    Register here
                </span>
            </p>

        </div>
    );
}

export default Login;